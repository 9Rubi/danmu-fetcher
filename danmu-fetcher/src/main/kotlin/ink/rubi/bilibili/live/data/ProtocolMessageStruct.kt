package ink.rubi.bilibili.live.data

import ink.rubi.bilibili.live.data.CMD.Companion.byCommand
import ink.rubi.bilibili.live.data.Operation.Companion.byCode
import ink.rubi.bilibili.live.data.Packet.Companion.createPacket
import ink.rubi.bilibili.live.data.Version.Companion.byVersion
import ink.rubi.bilibili.live.exception.UnknownProtocolTypeException
import ink.rubi.bilibili.live.objectMapper
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import java.nio.ByteBuffer

const val heartBeatContent = "[object Object]"

object Packets {
    val heartBeatPacket = createPacket(
        PacketHead(
            Version.WS_BODY_PROTOCOL_VERSION_INT,
            Operation.HEARTBEAT
        ),
        ByteBuffer.wrap(heartBeatContent.toByteArray())
    )
    val authPacket = fun(uid: Int, roomId: Int) = createPacket(
        PacketHead(
            Version.WS_BODY_PROTOCOL_VERSION_INT,
            Operation.AUTH
        ),
        ByteBuffer.wrap(
            objectMapper.writeValueAsString(
                AuthInfo(
                    uid,
                    roomId
                )
            )!!.toByteArray()
        )
    )
}

class Packet private constructor(header: PacketHead, payload: ByteBuffer) {
    var header: PacketHead = header
        private set
    var payload: ByteBuffer = payload
        private set

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


enum class Version(val version: Short) {
    WS_BODY_PROTOCOL_VERSION_NORMAL(0),
    WS_BODY_PROTOCOL_VERSION_INT(1), // 用于心跳包
    WS_BODY_PROTOCOL_VERSION_DEFLATE(2),
    UNKNOWN(Short.MIN_VALUE);

    companion object {
        val byVersion = values().associateBy { it.version }
    }
}

enum class Operation(val code: Int) {
    HANDSHAKE(0),
    HANDSHAKE_REPLY(1),
    HEARTBEAT(2),
    HEARTBEAT_REPLY(3),
    SEND_MSG(4),
    SEND_MSG_REPLY(5),
    DISCONNECT_REPLY(6),
    AUTH(7),
    AUTH_REPLY(8),
    RAW(9),
    PROTO_READY(10),
    PROTO_FINISH(11),
    CHANGE_ROOM(12),
    CHANGE_ROOM_REPLY(13),
    REGISTER(14),
    REGISTER_REPLY(15),
    UNREGISTER(16),
    UNREGISTER_REPLY(17),
    UNKNOWN(Int.MIN_VALUE);

    companion object {
        val byCode = values().associateBy { it.code }
    }
}

enum class CMD(val desc: String) {
    DANMU_MSG("收到弹幕"),
    SEND_GIFT("有人送礼"),
    SUPER_CHAT_MESSAGE("醒目留言"),
    SUPER_CHAT_MESSAGE_DELETE("删除醒目留言"),
    GUARD_BUY("有人上舰"),
    ACTIVITY_BANNER_RED_NOTICE_CLOSE(""),
    ACTIVITY_BANNER_UPDATE_V2(""),
    ACTIVITY_BANNER_UPDATE_BLS("BLS活动"),
    ACTIVITY_EVENT(""),
    ACTIVITY_MATCH_GIFT(""),
    ACTIVITY_RED_PACKET(""),
    BLOCK(""),
    CHANGE_ROOM_INFO("房间设置变更"),
    CLOSE(""),
    COMBO_END("COMBO结束"),
    COMBO_SEND("COMBO赠送"),
    CUT_OFF("直播强制切断"),
    DAILY_QUEST_NEWDAY(""),
    END(""),
    ENTRY_EFFECT("加入效果"),
    GUARD_LOTTERY_START(""),
    GUARD_MSG("舰队消息"),
    GUIARD_MSG(""),
    HOUR_RANK_AWARDS(""),
    EVENT_CMD("活动相关"),
    LIVE("开始直播"),
    LOL_ACTIVITY(""),
    LUCK_GIFT_AWARD_USER(""),
    MESSAGEBOX_USER_GAIN_MEDAL(""),
    new_anchor_reward(""),
    NOTICE_MSG(""),
    PK_AGAIN(""),
    PK_END(""),
    PK_MATCH(""),
    PK_MIC_END(""),
    PK_PRE(""),
    PK_PROCESS(""),
    PK_SETTLE(""),
    PK_START(""),
    PREPARING("准备直播"),
    RAFFLE_END("抽奖结束"),
    RAFFLE_START("抽奖开始"),
    REFRESH(""),
    ROOM_ADMINS("管理员变更"),
    ROOM_REAL_TIME_MESSAGE_UPDATE("房间时间更新"),
    room_admin_entrance(""),
    ROOM_BLOCK_INTO(""),
    ROOM_BLOCK_MSG("房间封禁消息"),
    ROOM_BOX_MASTER(""),
    ROOM_CHANGE(""),
    ROOM_KICKOUT(""),
    ROOM_LIMIT(""),
    ROOM_LOCK(""),
    ROOM_RANK("周星榜"),
    ROOM_REFRESH(""),
    ROOM_SHIELD("房间屏蔽"),
    ROOM_SILENT_OFF("房间禁言结束"),
    ROOM_SILENT_ON("房间开启禁言"),
    ROOM_SKIN_MSG("房间皮肤消息"),
    ROUND(""),
    SCORE_CARD(""),
    SEND_TOP(""),
    SPECIAL_GIFT("特殊礼物消息 节奏风暴"),
    SUPER_CHAT_ENTRANCE(""),
    SUPER_CHAT_MESSAGE_JPN(""),
    SYS_GIFT("系统礼物,广播"),
    SYS_MSG("系统消息,广播"),
    TV_END("小电视抽奖结束"),
    TV_START("小电视抽奖开始"),
    USER_TOAST_MSG(""),
    WARNING(""),
    WEEK_STAR_CLOCK(""),
    WELCOME(" `老爷` 进入直播间"),
    WELCOME_ACTIVITY(""),
    WELCOME_GUARD(" `舰长` 进入直播间"),
    WIN_ACTIVITY(""),
    WISH_BOTTLE("许愿瓶"),
    UNKNOWN("待补充，暂时无法处理");

    companion object {
        val byCommand = values().associateBy { it.name }
    }
}

fun searchCMD(cmd: String, throwIfNotFound: Boolean = false) = byCommand[cmd]
    ?: if (throwIfNotFound) throw UnknownProtocolTypeException("unknown cmd : $cmd") else CMD.UNKNOWN

fun searchOperation(operation: Int, throwIfNotFound: Boolean = false) = byCode[operation]
    ?: if (throwIfNotFound) throw UnknownProtocolTypeException("unknown operation : $operation") else Operation.UNKNOWN

fun searchVersion(version: Short, throwIfNotFound: Boolean = false) = byVersion[version]
    ?: if (throwIfNotFound) throw UnknownProtocolTypeException("unknown version : $version") else Version.UNKNOWN

