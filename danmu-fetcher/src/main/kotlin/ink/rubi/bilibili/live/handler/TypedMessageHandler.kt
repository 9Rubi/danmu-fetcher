package ink.rubi.bilibili.live.handler

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.module.kotlin.readValue
import ink.rubi.bilibili.live.data.*
import ink.rubi.bilibili.live.data.CMD.*
import ink.rubi.bilibili.live.exception.MessageException
import ink.rubi.bilibili.live.objectMapper

@DslMarker
private annotation class Dsl

@Dsl
interface TypedMessageHandler : MessageHandler {
    fun onReceiveDanmu(context: OnReceiveDanmuContext.() -> Unit)
    fun onReceiveGift(context: OnReceiveGiftContext.() -> Unit)
    fun onVipEnterInLiveRoom(context: OnVipEnterInLiveRoomContext.() -> Unit)
    fun onGuardEnterInLiveRoom(context: OnGuardEnterInLiveRoomContext.() -> Unit)
    fun onAllTypeMessage(context: OnAllTypeMessageContext.() -> Unit)
    fun onUnknownTypeMessage(context: OnUnknownTypeMessageContext.() -> Unit)
    fun onError(context: OnErrorContext.() -> Unit)
    fun onLive(context: OnLiveContext.() -> Unit)
    fun onPrepare(context: OnPrepareContext.() -> Unit)
    fun onRoomRankChange(context: OnRoomRankChangeContext.() -> Unit)
}

@Dsl data class OnReceiveDanmuContext(val user: User, val badge: Badge?, val userLevel: UserLevel, val said: String)
@Dsl data class OnReceiveGiftContext(val gift: Gift)
@Dsl data class OnVipEnterInLiveRoomContext(val user: String)
@Dsl data class OnGuardEnterInLiveRoomContext(val user: String)
@Dsl data class OnAllTypeMessageContext(val message: String)
@Dsl data class OnUnknownTypeMessageContext(val message: String,val cmd: String)
@Dsl data class OnErrorContext(val message: String,val e: MessageException)
@Dsl data class OnLiveContext(val roomId: Int)
@Dsl data class OnPrepareContext(val roomId: Int)
@Dsl data class OnRoomRankChangeContext(val rank: RoomRank)

class TypedMessageHandlerImpl(
    private var receiveDanmu: (OnReceiveDanmuContext.() -> Unit)?=null,
    private var receiveGift: (OnReceiveGiftContext.() -> Unit)? = null,
    private var vipEnterInLiveRoom: (OnVipEnterInLiveRoomContext.() -> Unit)? = null,
    private var guardEnterInLiveRoom: (OnGuardEnterInLiveRoomContext.() -> Unit)? = null,
    private var allTypeMessage: (OnAllTypeMessageContext.() -> Unit)? = null,
    private var unknownTypeMessage: (OnUnknownTypeMessageContext.() -> Unit)? = null,
    private var error: (OnErrorContext.() -> Unit)? = null,
    private var live: (OnLiveContext.() -> Unit)? = null,
    private var prepare: (OnPrepareContext.() -> Unit)? = null,
    private var roomRankChange: (OnRoomRankChangeContext.() -> Unit)? = null
) : TypedMessageHandler {

    override fun onReceiveDanmu(context: OnReceiveDanmuContext.() -> Unit) {
        receiveDanmu = context
    }

    override fun onReceiveGift(context: OnReceiveGiftContext.() -> Unit) {
        receiveGift = context
    }

    override fun onVipEnterInLiveRoom(context: OnVipEnterInLiveRoomContext.() -> Unit) {
        vipEnterInLiveRoom = context
    }

    override fun onGuardEnterInLiveRoom(context: OnGuardEnterInLiveRoomContext.() -> Unit) {
       guardEnterInLiveRoom = context
    }

    override fun onAllTypeMessage(context: OnAllTypeMessageContext.() -> Unit) {
        allTypeMessage = context
    }

    override fun onUnknownTypeMessage(context: OnUnknownTypeMessageContext.() -> Unit) {
        unknownTypeMessage = context
    }

    override fun onError(context: OnErrorContext.() -> Unit) {
        error = context
    }

    override fun onLive(context: OnLiveContext.() -> Unit) {
        live = context
    }

    override fun onPrepare(context: OnPrepareContext.() -> Unit) {
        prepare = context
    }

    override fun onRoomRankChange(context: OnRoomRankChangeContext.() -> Unit) {
        roomRankChange =  context
    }

    override fun handle(message: String) {
        try {
            allTypeMessage?.invoke(OnAllTypeMessageContext(message))
            val json = objectMapper.readTree(message)
            val cmd = json["cmd"]?.textValue() ?: throw Exception("unexpect json format, missing [cmd] !")
            when (searchCMD(cmd)) {
                DANMU_MSG -> {
                    val info = json["info"]
                    val said = info[1].textValue()!!
                    val user = with(info[2]) {
                        User(
                            uid = this[0].asInt(),
                            name = this[1].asText(),
                            isAdmin = this[2].asBoolean(),//0,1
                            isVip = this[3].asBoolean(),//0,1
                            isAnnualVip = this[4].asBoolean() //0,1
                        )
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
                    receiveDanmu?.invoke(OnReceiveDanmuContext(user, badge, userLevel, said))
                }
                SEND_GIFT -> {
                    val gift = objectMapper.readValue<Gift>(json["data"].toString())
                    receiveGift?.invoke(OnReceiveGiftContext(gift))
                }
                WELCOME -> {
                    val user = json["data"]["uname"].textValue()!!
                    vipEnterInLiveRoom?.invoke(OnVipEnterInLiveRoomContext(user))
                }
                WELCOME_GUARD -> {
                    val user = json["data"]["username"].textValue()!!
                    guardEnterInLiveRoom?.invoke(OnGuardEnterInLiveRoomContext(user))
                }
                LIVE -> {
                    val roomId = json["roomid"].asInt()
                    live?.invoke(OnLiveContext(roomId))
                }
                PREPARING -> {
                    val roomId = json["roomid"].asInt()
                    prepare?.invoke(OnPrepareContext(roomId))
                }
                ROOM_RANK -> {
                    val rank = objectMapper.readValue<RoomRank>(json["data"].toString())
                    roomRankChange?.invoke(OnRoomRankChangeContext(rank))
                }
                UNKNOWN -> {
                    unknownTypeMessage?.invoke(OnUnknownTypeMessageContext(message, cmd))
                }
                else -> {
                }
            }
        } catch (e: Throwable) {
            error?.invoke(OnErrorContext(message, MessageException("catch an exception while handling a message : $message", e)))
        }
    }
}

inline fun typedMessageHandler(content: TypedMessageHandler.() -> Unit) = TypedMessageHandlerImpl().apply(content)