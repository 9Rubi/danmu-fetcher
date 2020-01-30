package ink.rubi.bilibili.live

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import ink.rubi.bilibili.auth.api.getUidAsync
import ink.rubi.bilibili.live.api.DEFAULT_DANMU_HOST
import ink.rubi.bilibili.live.api.getLoadBalancedWsHostServerAsync
import ink.rubi.bilibili.live.api.getRealRoomIdAsync
import ink.rubi.bilibili.live.api.getWebTitlesAsync
import ink.rubi.bilibili.live.data.*
import ink.rubi.bilibili.live.data.Operation.*
import ink.rubi.bilibili.live.handler.EventHandler
import ink.rubi.bilibili.live.handler.EventType.*
import ink.rubi.bilibili.live.handler.MessageHandler
import ink.rubi.bilibili.live.handler.simpleEventHandler
import ink.rubi.bilibili.live.handler.simpleMessageHandler
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.BrowserUserAgent
import io.ktor.client.features.cookies.HttpCookies
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.wss
import io.ktor.http.ContentType
import io.ktor.http.DEFAULT_PORT
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.zip.InflaterOutputStream

var loadBalance = true
val log: Logger = LoggerFactory.getLogger("[danmu-client]")
val objectMapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())
val titlesDatabase = mutableMapOf<String, WebTitle>()

fun uncompressZlib(input: ByteArray): ByteArray =
    ByteArrayOutputStream().use { InflaterOutputStream(it).use { output -> output.write(input) }; return@use it.toByteArray() }

@KtorExperimentalAPI
val client = HttpClient(CIO) {
    install(WebSockets)
    install(JsonFeature) {
        serializer = JacksonSerializer()
        acceptContentTypes = acceptContentTypes + ContentType("text", "json")
    }
    install(Logging) {
        level = LogLevel.NONE
    }
    install(HttpCookies)
    BrowserUserAgent()
}

@KtorExperimentalAPI
fun CoroutineScope.connectLiveRoom(
    roomId: Int,
    messageHandler: MessageHandler =
        simpleMessageHandler {
            onReceiveDanmu { user, said ->
                log.info("[$user] : $said")
            }
            onReceiveGift { user, num, giftName ->
                log.info("[$user] 送出了 $num 个 [$giftName]")
            }
        }
    ,
    eventHandler: EventHandler = simpleEventHandler {
        onConnect {
            log.info("connect!")
        }
        onConnected {
            log.info("connected!")
        }
        onLoginSuccess {
            log.info("login success!")
        }
        onLoginFail {
            log.info("login failed!")
        }
        onDisconnect {
            log.info("disconnect!")
        }
        onLogin {
            log.info("login ...")
        }
    }, anonymous: Boolean = true
) = launch {
    val realRoomId = client.getRealRoomIdAsync(roomId)
    val hostServer = client.getLoadBalancedWsHostServerAsync(roomId)
    val titleWrap = client.getWebTitlesAsync()
    val uid = if (anonymous) 0 else client.getUidAsync().await()
    initData(realRoomId, hostServer, titleWrap)
    eventHandler.handle(CONNECT)
    try {
        client.wss(
            host = if (loadBalance) hostServer.getCompleted().host else DEFAULT_DANMU_HOST,
            port = if (loadBalance && hostServer.getCompleted().host != DEFAULT_DANMU_HOST)
                hostServer.getCompleted().wss_port else DEFAULT_PORT,
            path = "/sub"

        ) {
            eventHandler.handle(CONNECTED)
            launch(this@connectLiveRoom.coroutineContext) {
                while (true)
                    decode(
                        incoming.receive().buffer,
                        messageHandler,
                        eventHandler
                    )
            }
            eventHandler.handle(LOGIN)
            sendPacket(Packets.authPacket(uid, realRoomId.getCompleted()))
            launch {
                closeReason.await()?.let {
                    with(it) {
                        log.info("code:$code")
                        log.info("message:$message")
                        log.info("reason:$knownReason")
                    }
                }
            }

            while (true) {
                sendPacket(Packets.heartBeatPacket)
                delay(30_000)
            }
        }
    } catch (e: Throwable) {
        eventHandler.handle(DISCONNECT)
        log.error("disconnect from server cause : ",e)
    }

}

private suspend fun initData(
    realRoomId: Deferred<Int>,
    hostServer: Deferred<HostServer>,
    titles: Deferred<List<WebTitle>>
) {
    titles.await().map { it.identification to it }.toMap(titlesDatabase)
    log.info("web-titles        : ${titlesDatabase.size}")
    log.info("room id           : ${realRoomId.await()}")
    log.info("use server        : ${hostServer.await().host}")
}

private fun decode(
    buffer: ByteBuffer,
    messageHandler: MessageHandler,
    eventHandler: EventHandler
) {
    val packet = Packet.resolve(buffer)
    val header = packet.header
    val payload = packet.payload
    when (header.code) {
        HEARTBEAT_REPLY -> log.debug("heart beat packet")
        AUTH_REPLY -> {
            val message = payload.array().toString(Charsets.UTF_8)
            eventHandler.handle(if (message == """{"code":0}""") LOGIN_SUCCESS else LOGIN_FAILED)
            log.info("response => $message")
        }
        SEND_MSG_REPLY -> {
            if (header.version == Version.WS_BODY_PROTOCOL_VERSION_DEFLATE) {
                decode(
                    ByteBuffer.wrap(
                        uncompressZlib(
                            payload.array()
                        )
                    ), messageHandler, eventHandler
                )
                return
            }
            assert(header.version == Version.WS_BODY_PROTOCOL_VERSION_NORMAL)
            val byteArray = ByteArray(header.packLength - header.headLength)
            payload.get(byteArray)
            byteArray.toString(Charsets.UTF_8).let { message ->
                log.debug(message)
                messageHandler.handle(message)
            }
            if (payload.hasRemaining())
                decode(
                    payload,
                    messageHandler,
                    eventHandler
                )
        }
        else -> {
            val operation = searchOperation(header.code.code)
            if (operation == UNKNOWN)
                log.warn("code unknown! => ${header.code.code}")
            else
                log.warn("code exist in enums,but now haven't been handle => name : ${operation.name} , code : ${operation.code} ")
        }
    }
}


