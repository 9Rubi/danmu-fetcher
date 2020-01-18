package ink.rubi.danmu

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
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


const val HEADER_LENGTH = 16
const val uid = 0
//const val WEBSOCKET_PATH = "wss://broadcastlv.chat.bilibili.com:2245/sub"
const val WEBSOCKET_PATH = "wss://tx-gz-live-comet-11.chat.bilibili.com/sub"
const val ROOM_INIT_URL = "https://api.live.bilibili.com/room/v1/Room/room_init"
const val DANMU_SERVER_CONF_URL = "https://api.live.bilibili.com/room/v1/Danmu/getConf"
val log: Logger = LoggerFactory.getLogger("[danmu-client]")
val objectMapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())

object DanmuListener {
    @FlowPreview
    @ExperimentalCoroutinesApi
    @KtorExperimentalAPI
    @JvmStatic
    fun doFetchDanmu(roomId: Int, block: (cmd: String, rawJson: String) -> Unit) = runBlocking {
        val client = HttpClient(CIO) {
            install(WebSockets)
            install(JsonFeature) {
                serializer = JacksonSerializer()
            }
            BrowserUserAgent()
        }
        val realRoomId = client.roomInfo(roomId).data.room_id
        log.info("room id => $realRoomId")
        client.wss(WEBSOCKET_PATH) {
            launch {
                while (true)
                    decode(incoming.receive().buffer, block)
            }
            log.info("login ....")
            login(uid, realRoomId)
            while (true) {
                heartBeat()
                delay(25000)
            }
        }
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

    private suspend fun HttpClient.roomInfo(roomId: Int): RoomInit {
        return this.get(ROOM_INIT_URL) {
            parameter("id", roomId)
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

    private fun decode(buffer: ByteBuffer, block: (cmd: String, rawJson: String) -> Unit) {
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
                    decode(ByteBuffer.wrap(uncompressZlib(raw)), block)
                    return
                }
                assert(head.version == Version.WS_BODY_PROTOCOL_VERSION_NORMAL.version)
                val byteArray = ByteArray(head.packLength - head.headLength)
                buffer.get(byteArray)
                val message = byteArray.toString(Charsets.UTF_8)
                log.debug(message)
                val cmd = objectMapper.readTree(message)["cmd"]?.textValue() ?: throw Exception("wrong json , missing [cmd] !")
                block(cmd, message)
                if (buffer.hasRemaining())
                    decode(buffer, block)
            }
            else -> log.warn("code => ${Operation.values().first { i -> i.code == head.code }.name} !!!!!")
        }
    }

}

