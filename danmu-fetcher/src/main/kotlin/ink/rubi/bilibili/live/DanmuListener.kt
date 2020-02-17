package ink.rubi.bilibili.live

import ink.rubi.bilibili.auth.api.getUidAsync
import ink.rubi.bilibili.common.FetcherContext.defaultClient
import ink.rubi.bilibili.common.FetcherContext.defaultEventHandler
import ink.rubi.bilibili.common.FetcherContext.defaultMessageHandler
import ink.rubi.bilibili.common.FetcherContext.loadBalance
import ink.rubi.bilibili.common.FetcherContext.log
import ink.rubi.bilibili.common.FetcherContext.titlesDatabase
import ink.rubi.bilibili.live.api.DEFAULT_DANMU_HOST
import ink.rubi.bilibili.live.api.getLoadBalancedWsHostServerAsync
import ink.rubi.bilibili.live.api.getRealRoomIdAsync
import ink.rubi.bilibili.live.api.getWebTitlesAsync
import ink.rubi.bilibili.live.data.*
import ink.rubi.bilibili.live.data.Operation.*
import ink.rubi.bilibili.live.handler.EventHandler
import ink.rubi.bilibili.live.handler.EventType.*
import ink.rubi.bilibili.live.handler.MessageHandler
import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.wss
import io.ktor.http.DEFAULT_PORT
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.*
import java.nio.ByteBuffer

@ExperimentalCoroutinesApi
@KtorExperimentalAPI
fun CoroutineScope.connectLiveRoom(
    roomId: Int,
    messageHandler: MessageHandler = defaultMessageHandler, eventHandler: EventHandler = defaultEventHandler,
    anonymous: Boolean = true, client: HttpClient = defaultClient
) = launch {
    val realRoomId = client.getRealRoomIdAsync(roomId)
    val hostServer = client.getLoadBalancedWsHostServerAsync(roomId)
    val titleWrap = client.getWebTitlesAsync()
    val uid = if (anonymous) 0 else client.getUidAsync().await()
    try {
        initData(realRoomId, hostServer, titleWrap)
    } catch (e: Throwable) {
        log.error("danmuji init exception :", e)
        throw CancellationException("init error", e)
    }
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
            launch(this@connectLiveRoom.coroutineContext) {
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
        log.error("disconnect from server cause :", e)
    }

}

@KtorExperimentalAPI
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

@KtorExperimentalAPI
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
            require(header.version == Version.WS_BODY_PROTOCOL_VERSION_NORMAL)
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


