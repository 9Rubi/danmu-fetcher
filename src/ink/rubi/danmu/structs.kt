package ink.rubi.danmu

data class RoomInit(
    val code: Int,
    val `data`: Data,
    val message: String,
    val msg: String
)

data class Data(
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

//封包格式
//封包由头部和数据组成，字节序均为大端模式
//客户端要每30s发一次
data class PacketHead(
    val packLength: Int, //封包总大小
    val headLength: Short,//头部长度
    val version: Short = 1, //协议版本，目前是1
    /**
     *```
     * 2 	客户端发送的心跳包
     * 3 	人气值，数据不是JSON，是4字节整数
     * 5 	命令，数据中['cmd']表示具体命令
     * 7 	认证并加入房间
     * 8 	服务器发送的心跳包
     *```
     */
    val code: Int, //操作码（封包类型）
    val seq: Int = 1 //sequence，可以取常数1
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

fun main() {
}