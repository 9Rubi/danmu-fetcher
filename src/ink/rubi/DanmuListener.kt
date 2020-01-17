package ink.rubi

import AuthInfo
import PacketHead
import RoomInit
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.sun.javafx.util.Utils
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
import io.ktor.http.cio.websocket.Frame
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.zip.InflaterOutputStream


const val HEADER_LENGTH = 16
const val uid = 3220953
//const val id = 958282
const val room = 6
//const val WEBSOCKET_PATH = "wss://broadcastlv.chat.bilibili.com:2245/sub"
const val WEBSOCKET_PATH = "wss://tx-gz-live-comet-11.chat.bilibili.com/sub"
const val ROOM_INIT_URL = "https://api.live.bilibili.com/room/v1/Room/room_init?id=$room"
const val DANMU_SERVER_CONF_URL = "https://api.live.bilibili.com/room/v1/Danmu/getConf"
val log: Logger = LoggerFactory.getLogger("[danmu-client]")
val objectMapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())

object DanmuFetcher {
    @FlowPreview
    @ExperimentalCoroutinesApi
    @KtorExperimentalAPI
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val client = HttpClient(CIO) {
            install(WebSockets)
            install(JsonFeature) {
                serializer = JacksonSerializer()
            }
            install(Logging) {
                level = LogLevel.ALL
            }
            BrowserUserAgent()
        }
        val roomId = client.roomInfo().data.room_id
        log.info("room id => $roomId")
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
                heartBeat(uid, roomId)
                delay(25000)
            }
        }
    }

    private suspend fun DefaultClientWebSocketSession.heartBeat(uid: Int, roomId: Int) {
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

    private suspend fun HttpClient.roomInfo(): RoomInit {
        return this.get(ROOM_INIT_URL)
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
                    decode(ByteBuffer.wrap(uncompress(raw)))
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
                log.info("[${who}] : ${said}")
            }
            CMD.SEND_GIFT.name -> {
                log.info("[${Utils.convertUnicode(json["data"]["uname"].textValue())}] 送出了 ${json["data"]["num"].intValue()} 个 [${Utils.convertUnicode(json["data"]["giftName"].textValue())}]")
            }
            CMD.WELCOME.name ->{
                log.info("[${json["data"]["uname"].textValue()}] 进入了直播间")
            }
            CMD.WELCOME_GUARD.name ->{
                log.info("[舰长][${json["data"]["username"].textValue()}] 进入了直播间")
            }
            else -> {
                log.warn(message)
            }
        }
    }
}
fun uncompress(input: ByteArray): ByteArray {
    val byteArrayOutputStream = ByteArrayOutputStream()
    val inflaterOutputStream = InflaterOutputStream(byteArrayOutputStream)
    inflaterOutputStream.write(input)
    inflaterOutputStream.close()
    return byteArrayOutputStream.toByteArray()
}



