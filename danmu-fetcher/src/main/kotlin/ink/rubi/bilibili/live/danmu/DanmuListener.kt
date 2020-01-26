package ink.rubi.bilibili.live.danmu

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import ink.rubi.bilibili.live.danmu.constant.Operation
import ink.rubi.bilibili.live.danmu.constant.Version
import ink.rubi.bilibili.live.danmu.constant.searchOperation
import ink.rubi.bilibili.live.danmu.data.*
import ink.rubi.bilibili.live.danmu.handler.MessageHandler
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.BrowserUserAgent
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
import java.nio.ByteBuffer


const val uid = 0

var loadBalance = true
val log: Logger = LoggerFactory.getLogger("[danmu-client]")
val objectMapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())
val titlesDatabase = mutableMapOf<String, WebTitle>()

@KtorExperimentalAPI
val client = HttpClient(CIO) {
    install(WebSockets)
    install(JsonFeature) {
        serializer = JacksonSerializer()
        acceptContentTypes = acceptContentTypes + ContentType("text", "json")
    }
    install(Logging) {
        level = LogLevel.ALL
    }
    BrowserUserAgent()
}

@ExperimentalCoroutinesApi
object DanmuListener {
    @KtorExperimentalAPI
    fun CoroutineScope.receiveDanmu(
        roomId: Int, // workersContext: CoroutineContext =  Executors.newFixedThreadPool(10).asCoroutineDispatcher(),
        handler: () -> MessageHandler
    ) = launch {
        val realRoomId = client.getRealRoomIdAsync(roomId)
        val hostServer = client.getLoadBalancedWsHostServerAsync(roomId)
        val titleWrap = client.getWebTitlesAsync()
        initData(realRoomId, hostServer, titleWrap)
        val handlerImpl = handler()
        client.wss(
            host = if (loadBalance) hostServer.getCompleted().host else DEFAULT_DANMU_HOST,
            port = if (loadBalance && hostServer.getCompleted().host != DEFAULT_DANMU_HOST)
                hostServer.getCompleted().wss_port else DEFAULT_PORT,
            path = "/sub"
        ) {
            launch(this@receiveDanmu.coroutineContext) {
                while (true)
                    decode(incoming.receive().buffer, handlerImpl)
            }
            log.info("login ....")//0228
            sendPacket(Packets.authPacket(uid, realRoomId.getCompleted()))
            while (true) {
                sendPacket(Packets.heartBeatPacket)
                delay(30_000)
            }
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


    private fun decode(buffer: ByteBuffer, handler: MessageHandler) {
        val packet = Packet.resolve(buffer)
        val header = packet.header
        val payload = packet.payload
        when (header.code) {
            Operation.HEARTBEAT_REPLY -> log.debug("heart beat packet")
            Operation.AUTH_REPLY -> log.info("response => ${payload.array().toString(Charsets.UTF_8)}")
            Operation.SEND_MSG_REPLY -> {
                if (header.version == Version.WS_BODY_PROTOCOL_VERSION_DEFLATE) {
                    decode(ByteBuffer.wrap(uncompressZlib(payload.array())), handler)
                    return
                }
                assert(header.version == Version.WS_BODY_PROTOCOL_VERSION_NORMAL)
                val byteArray = ByteArray(header.packLength - header.headLength)
                payload.get(byteArray)
                byteArray.toString(Charsets.UTF_8).let { message ->
                    log.debug(message)
                    handler.handle(message)
                }
                if (payload.hasRemaining())
                    decode(payload, handler)
            }
            else -> {
                val operation = searchOperation(header.code.code)
                if (operation == Operation.UNKNOWN)
                    log.warn("code unknown! => ${header.code.code}")
                else
                    log.warn("code exist in enums,but now haven't been handle => name : ${operation.name} , code : ${operation.code} ")
            }
        }
    }
}



