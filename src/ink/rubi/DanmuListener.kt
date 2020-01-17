package ink.rubi

import AuthInfo
import PacketHead
import RoomInit
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.BrowserUserAgent
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
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

object DanmuFetcher {
    @FlowPreview
    @ExperimentalCoroutinesApi
    @KtorExperimentalAPI
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        println("输入 直播间号码:")
        val roomId = readLine()!!.toInt()

        val client = HttpClient(CIO) {
            install(WebSockets)
            install(JsonFeature) {
                serializer = JacksonSerializer()
            }
            install(Logging) {
                level = LogLevel.NONE
            }
            BrowserUserAgent()
        }
        val realRoomId = client.roomInfo(roomId).data.room_id
        log.info("room id => $realRoomId")
        client.wss(
            urlString = WEBSOCKET_PATH
        ) {
            log.info("main ${Thread.currentThread().name} ")
            launch {
                while (true) {
                    handlePacket(incoming.receive().buffer)
                }
            }
            log.info("login ....")
            login(uid, roomId)
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

    private fun handlePacket(buffer: ByteBuffer) {
        decode(buffer)
    }

    private suspend fun HttpClient.roomInfo(roomId: Int): RoomInit {
        return this.get(ROOM_INIT_URL) {
            parameter("id",roomId)
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

    private fun decode(buffer: ByteBuffer) {
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
                    decode(ByteBuffer.wrap(uncompressZlib(raw)))
                    return
                }
                assert(head.version == Version.WS_BODY_PROTOCOL_VERSION_NORMAL.version)
                val byteArray = ByteArray(head.packLength - head.headLength)
                buffer.get(byteArray)
                handleMessage(byteArray.toString(Charsets.UTF_8))
                if (buffer.hasRemaining())
                    decode(buffer)
            }
            else -> log.warn("code => ${Operation.values().first { i -> i.code == head.code }.name} !!!!!")
        }
    }

    private fun handleMessage(message: String) {
        log.debug(message)
        val json = objectMapper.readTree(message)
        val cmd = json["cmd"]?.textValue() ?: throw Exception("wrong json , missing cmd ")
        when (cmd) {
            CMD.DANMU_MSG.name -> {
                val said = json["info"][1].textValue()!!
                val who = json["info"][2][1].textValue()!!
                log.info("[$who] : $said")
            }
            CMD.SEND_GIFT.name -> {
                val who = unescapeUnicode(json["data"]["uname"].textValue())!!
                val num = json["data"]["num"].intValue()
                val gift = unescapeUnicode(json["data"]["giftName"].textValue())!!
                log.info("[$who] 送出了 $num 个 [$gift]")
            }
            CMD.WELCOME.name -> {
                val who = json["data"]["uname"].textValue()!!
                log.info("[$who] 进入了直播间")
            }
            CMD.WELCOME_GUARD.name -> {
                val who = json["data"]["username"].textValue()!!
                log.info("[舰长][$who] 进入了直播间")
            }
            else -> {
                log.warn(message)
            }
        }
    }
}

