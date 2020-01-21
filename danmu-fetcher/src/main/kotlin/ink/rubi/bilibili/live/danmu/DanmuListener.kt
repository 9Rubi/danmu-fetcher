package ink.rubi.bilibili.live.danmu

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import ink.rubi.bilibili.live.danmu.constant.Operation
import ink.rubi.bilibili.live.danmu.constant.Version
import ink.rubi.bilibili.live.danmu.data.*
import ink.rubi.bilibili.live.danmu.handler.MessageHandler
import ink.rubi.bilibili.live.danmu.util.uncompressZlib
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.BrowserUserAgent
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.websocket.DefaultClientWebSocketSession
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.wss
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.cio.websocket.Frame
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer
import kotlin.random.Random


const val HEADER_LENGTH = 16
const val uid = 0
const val ROOM_INIT_URL = "https://api.live.bilibili.com/room/v1/Room/room_init"
const val ROOM_LOAD_BALANCE_URL = "https://api.live.bilibili.com/room/v1/Danmu/getConf"
val log: Logger = LoggerFactory.getLogger("[danmu-client]")
val objectMapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())
@KtorExperimentalAPI
val client = HttpClient(CIO) {
    install(WebSockets)
    install(JsonFeature) {
        serializer = JacksonSerializer()
    }
    BrowserUserAgent()
}

@ExperimentalCoroutinesApi
object DanmuListener {
    @KtorExperimentalAPI
    fun CoroutineScope.receiveDanmu(roomId: Int, handler: () -> MessageHandler) = launch {
        val realRoomId = client.getRealRoomIdAsync(roomId)
        val hostServer = client.getLoadBalancedWsHostServerAsync(roomId)

        awaitAndLog(realRoomId, hostServer)
        val handlerImpl = handler()
        client.wss(host = hostServer.getCompleted().host, port = hostServer.getCompleted().wss_port, path = "/sub") {
            launch {
                while (true)
                    decode(incoming.receive().buffer, handlerImpl)
            }
            log.info("login ....")//0228
            login(uid, realRoomId.getCompleted())
            while (true) {
                heartBeat()
                delay(25000)
            }
        }
    }

    private suspend fun awaitAndLog(realRoomId: Deferred<Int>, hostServer: Deferred<HostServer>) {
        log.info("room id => ${realRoomId.await()}")
        log.info("use        ${hostServer.await().host}")
    }

    private suspend fun DefaultClientWebSocketSession.heartBeat() {
        send(Frame.Binary(true, heartBeatPacket()))
        flush()
    }

    private fun heartBeatPacket(): ByteArray {
        val body = "[object Object]".toByteArray(Charsets.UTF_8)
        val packetHead =
            PacketHead(
                packLength = body.size + HEADER_LENGTH,
                headLength = HEADER_LENGTH.toShort(),
                code = Operation.HEARTBEAT.code
            )
        val buffer = ByteBuffer.allocate(packetHead.packLength)!!
        with(packetHead) {
            buffer
                .putInt(packLength)
                .putShort(headLength)
                .putShort(version)
                .putInt(code)
                .putInt(seq)
                .put(body)
        }
        return buffer.array()
    }

    private suspend fun DefaultClientWebSocketSession.login(uid: Int, roomId: Int) {
        send(Frame.Binary(true, buildAuthPacket(uid, roomId)))
        flush()
    }

    private fun HttpClient.getRealRoomIdAsync(roomId: Int): Deferred<Int> {
        return async {
            this@getRealRoomIdAsync.get<RoomInit>(ROOM_INIT_URL) {
                parameter("id", roomId)
            }.data.room_id
        }
    }

    private fun HttpClient.getLoadBalancedWsHostServerAsync(roomId: Int): Deferred<HostServer> {
        return async {
            this@getLoadBalancedWsHostServerAsync.get<LoadBalanceInfo>(ROOM_LOAD_BALANCE_URL) {
                parameter("room_id", roomId)
                parameter("platform", "pc")
                parameter("player", "web")
            }.data.host_server_list[Random.nextInt(0, 3)]
        }
    }


    private fun buildAuthPacket(uid: Int, roomId: Int): ByteArray {
        val info = AuthInfo(uid = uid, roomid = roomId)
        val content = objectMapper.writeValueAsString(info)!!
        val body = content.toByteArray(Charsets.UTF_8)
        val packetHead =
            PacketHead(
                packLength = body.size + HEADER_LENGTH,
                headLength = HEADER_LENGTH.toShort(),
                code = Operation.AUTH.code
            )
        val buffer = ByteBuffer.allocate(packetHead.packLength)!!
        with(packetHead) {
            buffer
                .putInt(packLength)
                .putShort(headLength)
                .putShort(version)
                .putInt(code)
                .putInt(seq)
                .put(body)
        }
        return buffer.array()
    }

    private fun decode(buffer: ByteBuffer, handler: MessageHandler) {
        val head = with(buffer) {
            PacketHead(int, short, short, int, int)
        }
        when (head.code) {
            Operation.HEARTBEAT_REPLY.code -> log.info("heart beat packet")
            Operation.AUTH_REPLY.code -> {
                val body = ByteArray(buffer.remaining())
                buffer.get(body)
                log.info("response => ${body.toString(Charsets.UTF_8)}")
            }
            Operation.SEND_MSG_REPLY.code -> {
                if (head.version == Version.WS_BODY_PROTOCOL_VERSION_DEFLATE.version) {
                    val raw = ByteArray(buffer.remaining())
                    buffer.get(raw)
                    decode(ByteBuffer.wrap(uncompressZlib(raw)), handler)
                    return
                }
                assert(head.version == Version.WS_BODY_PROTOCOL_VERSION_NORMAL.version)
                val byteArray = ByteArray(head.packLength - head.headLength)
                buffer.get(byteArray)
                val message = byteArray.toString(Charsets.UTF_8)
                log.debug(message)
                handler.handle(message)

                if (buffer.hasRemaining())
                    decode(buffer, handler)
            }
            else -> log.warn("code => ${Operation.values().first { i -> i.code == head.code }.name} !!!!!")
        }
    }
}

