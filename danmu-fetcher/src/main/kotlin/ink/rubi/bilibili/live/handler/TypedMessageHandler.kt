package ink.rubi.bilibili.live.handler

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.module.kotlin.readValue
import ink.rubi.bilibili.live.danmu.data.*
import ink.rubi.bilibili.live.data.*
import ink.rubi.bilibili.live.data.CMD.*
import ink.rubi.bilibili.live.exception.MessageException
import ink.rubi.bilibili.live.objectMapper

interface TypedMessageHandler : MessageHandler {
    fun onReceiveDanmu(block: (user: User, badge: Badge?, userLevel: UserLevel, said: String) -> Unit)
    fun onReceiveGift(block: (gift: Gift) -> Unit)
    fun onVipEnterInLiveRoom(block: (user: String) -> Unit)
    fun onGuardEnterInLiveRoom(block: (user: String) -> Unit)
    fun onAllTypeMessage(block: (message: String) -> Unit)
    fun onUnknownTypeMessage(block: (message: String, cmd: String) -> Unit)
    fun onError(block: (message: String, e: MessageException) -> Unit)
    fun onLive(block: (roomId: Int) -> Unit)
    fun onPrepare(block: (roomId: Int) -> Unit)
    fun onRoomRankChange(block: (rank: RoomRank) -> Unit)
}

class TypedMessageHandlerImpl(
    private var receiveDanmu: ((user: User, badge: Badge?, userLevel: UserLevel, said: String) -> Unit)? = null,
    private var receiveGift: ((gift: Gift) -> Unit)? = null,
    private var vipEnterInLiveRoom: ((user: String) -> Unit)? = null,
    private var guardEnterInLiveRoom: ((user: String) -> Unit)? = null,
    private var allTypeMessage: ((message: String) -> Unit)? = null,
    private var unknownTypeMessage: ((message: String, cmd: String) -> Unit)? = null,
    private var error: ((message: String, e: MessageException) -> Unit)? = null,
    private var live: ((roomId: Int) -> Unit)? = null,
    private var prepare: ((roomId: Int) -> Unit)? = null,
    private var roomRankChange: ((rank: RoomRank) -> Unit)? = null
) : TypedMessageHandler {

    override fun onReceiveDanmu(block: (user: User, badge: Badge?, userLevel: UserLevel, said: String) -> Unit) {
        receiveDanmu = block
    }

    override fun onReceiveGift(block: (gift: Gift) -> Unit) {
        receiveGift = block
    }

    override fun onVipEnterInLiveRoom(block: (user: String) -> Unit) {
        vipEnterInLiveRoom = block
    }

    override fun onGuardEnterInLiveRoom(block: (user: String) -> Unit) {
        guardEnterInLiveRoom = block
    }

    override fun onAllTypeMessage(block: (message: String) -> Unit) {
        allTypeMessage = block
    }

    override fun onUnknownTypeMessage(block: ((message: String, cmd: String) -> Unit)) {
        unknownTypeMessage = block
    }

    override fun onError(block: (message: String, e: MessageException) -> Unit) {
        error = block
    }

    override fun onLive(block: (roomId: Int) -> Unit) {
        live = block
    }

    override fun onPrepare(block: (roomId: Int) -> Unit) {
        prepare = block
    }

    override fun onRoomRankChange(block: (rank: RoomRank) -> Unit) {
        roomRankChange = block
    }

    override fun handle(message: String) {
        try {
            allTypeMessage?.invoke(message)
            val json = objectMapper.readTree(message)
            val cmd = json["cmd"]?.textValue() ?: throw Exception("unexpect json format, missing [cmd] !")
            when (searchCMD(cmd)) {
                DANMU_MSG -> {
                    val info = json["info"]
                    val said = info[1].textValue()!!
                    val user = with(info[2]) {
                        User(this[0].asInt(), this[1].asText())
                    }
                    val badge = with(info[3]) {
                        if (this.isArray && (this as ArrayNode).size() == 0)
                            null
                        else
                            Badge(
                                this[0].asInt(0),
                                this[1].asText(null),
                                this[2].asText(null),
                                this[3].asInt(0)
                            )
                    }
                    val userLevel = with(info[4]) {
                        UserLevel(
                            this[0].asInt(),
                            this[2].asInt(),
                            this[3].asText()
                        )
                    }
                    receiveDanmu?.invoke(user, badge, userLevel, said)
                }
                SEND_GIFT -> {
                    val gift = objectMapper.readValue<Gift>(json["data"].toString())
                    receiveGift?.invoke(gift)
                }
                WELCOME -> {
                    val user = json["data"]["uname"].textValue()!!
                    vipEnterInLiveRoom?.invoke(user)
                }
                WELCOME_GUARD -> {
                    val user = json["data"]["username"].textValue()!!
                    guardEnterInLiveRoom?.invoke(user)
                }
                LIVE -> {
                    val roomId = json["roomid"].asInt()
                    live?.invoke(roomId)
                }
                PREPARING -> {
                    val roomId = json["roomid"].asInt()
                    prepare?.invoke(roomId)
                }
                ROOM_RANK -> {
                    val rank = objectMapper.readValue<RoomRank>(json["data"].toString())
                    roomRankChange?.invoke(rank)
                }
                UNKNOWN -> {
                    unknownTypeMessage?.invoke(message,cmd)
                }
                else -> { }
            }
        } catch (e: Throwable) {
            error?.invoke(message,
                MessageException(
                    "catch an exception while handling a message : $message",
                    e
                )
            )
        }
    }
}

fun typedMessageHandler(content: TypedMessageHandler.() -> Unit) = TypedMessageHandlerImpl().apply(content)