package ink.rubi.bilibili.live.danmu.data

import ink.rubi.bilibili.live.danmu.constant.Operation
import ink.rubi.bilibili.live.danmu.constant.Version
import ink.rubi.bilibili.live.danmu.constant.searchOperation
import ink.rubi.bilibili.live.danmu.constant.searchVersion
import ink.rubi.bilibili.live.danmu.data.Packet.Companion.createPacket
import ink.rubi.bilibili.live.danmu.objectMapper
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import java.nio.ByteBuffer

const val heartBeatContent = "[object Object]"

object Packets {
    val heartBeatPacket = createPacket(
        PacketHead(Version.WS_BODY_PROTOCOL_VERSION_INT, Operation.HEARTBEAT),
        ByteBuffer.wrap(heartBeatContent.toByteArray())
    )
    val authPacket = fun(uid: Int, roomId: Int) = createPacket(
        PacketHead(Version.WS_BODY_PROTOCOL_VERSION_INT, Operation.AUTH),
        ByteBuffer.wrap(objectMapper.writeValueAsString(AuthInfo(uid, roomId))!!.toByteArray())
    )
}

class Packet private constructor(private val _header: PacketHead, private val _payload: ByteBuffer) {
    val header: PacketHead
        get() = _header
    val payload: ByteBuffer
        get() = _payload

    companion object {
        fun createPacket(header: PacketHead, payload: ByteBuffer): Packet {
            return Packet(header, payload).apply { this.calcHeaderLength() }
        }

        fun createPacket(
            header: PacketHead, payload: ByteBuffer, packLength: Int,
            headLength: Short
        ): Packet {
            return Packet(header, payload).apply {
                with(this.header) {
                    this.packLength = packLength
                    this.headLength = headLength
                }
            }
        }

        fun resolve(buffer: ByteBuffer): Packet {
            with(buffer) {
                val packLength = int
                val headLength = short
                val version = short
                val code = int
                val seq = int
                val body = ByteArray(buffer.remaining())
                get(body)
                return createPacket(
                    PacketHead(
                        searchVersion(version, true),
                        searchOperation(code, true),
                        seq
                    ), ByteBuffer.wrap(body), packLength, headLength
                )
            }
        }
    }

    private fun calcHeaderLength() {
        header.packLength = header.headLength + payload.limit()
    }

    fun toFrame(): Frame {
        return ByteBuffer.allocate(header.packLength).apply {
            putInt(header.packLength)
            putShort(header.headLength)
            putShort(header.version.version)
            putInt(header.code.code)
            putInt(header.seq)
            put(payload)
            flip()
        }.let { Frame.Binary(true, it) }
    }
}

data class PacketHead(
    val version: Version,
    val code: Operation,
    val seq: Int = 1
) {
    var packLength: Int = 0
    var headLength: Short = 16
}


internal suspend inline fun WebSocketSession.sendPacket(packet: Packet) = send(packet.toFrame())


data class NormalResponse<T>(
    val code: Int,
    val msg: String,
    val message: String,
    val `data`: T
)

data class WebTitle(
    val colorful: Int,
    val h5_url: String,
    val identification: String,
    val level: String,
    val name: String,
    val source: String,
    val title_id: String,
    val url: String,
    val web_pic_url: String
)

data class RoomInitInfo(
    val encrypted: Boolean,
    val hidden_till: Int,
    val is_hidden: Boolean,
    val is_locked: Boolean,
    val is_portrait: Boolean,
    val is_sp: Int,
    val live_status: Int,
    val live_time: Long,
    val lock_till: Int,
    val need_p2p: Int,
    val pwd_verified: Boolean,
    val room_id: Int,
    val room_shield: Int,
    val short_id: Int,
    val special_type: Int,
    val uid: Int
)

data class LoadBalanceInfo(
    val host: String,
    val host_server_list: List<HostServer>,
    val max_delay: Int,
    val port: Int,
    val refresh_rate: Int,
    val refresh_row_factor: Double,
    val server_list: List<Server>,
    val token: String
)

data class HostServer(
    val host: String,
    val port: Int,
    val ws_port: Int,
    val wss_port: Int
)

data class Server(
    val host: String,
    val port: Int
)


data class AuthInfo(
    val uid: Int,//0表示未登录，否则为用户ID
    val roomid: Int,//房间ID
    val clientver: String = "1.10.1",
    val platform: String = "web",
    val type: Int = 2,
    val protover: Int = 2
//    val key: String = "kI2b1G7RD8DBQs4312ZsLKdWNz2k4yijKKc5NoPBAUNpAxEaC6ai2hKUYVDtCzLGU687Z1NMfCn1IkbDo_75iQq8bq_5N8VJWmZPIGb6MnEedFHJHccG"
)

fun Int.joinToLiveRoomUrl(): String {
    return "https://live.bilibili.com/$this"
}