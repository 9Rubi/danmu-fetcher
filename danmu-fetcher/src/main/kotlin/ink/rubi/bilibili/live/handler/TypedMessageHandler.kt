package ink.rubi.bilibili.live.handler

import com.google.gson.JsonParser
import ink.rubi.bilibili.live.data.*
import ink.rubi.bilibili.live.data.CMD.*
import ink.rubi.bilibili.live.exception.MessageException
import io.ktor.util.KtorExperimentalAPI
import ink.rubi.bilibili.common.FetcherContext.gson
import ink.rubi.bilibili.common.fromJson

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

@Dsl
data class OnReceiveDanmuContext(val user: User, val badge: Badge?, val userLevel: UserLevel, val said: String)

@Dsl
data class OnReceiveGiftContext(val gift: Gift)

@Dsl
data class OnVipEnterInLiveRoomContext(val user: String)

@Dsl
data class OnGuardEnterInLiveRoomContext(val user: String)

@Dsl
data class OnAllTypeMessageContext(val message: String)

@Dsl
data class OnUnknownTypeMessageContext(val message: String, val cmd: String)

@Dsl
data class OnErrorContext(val message: String, val e: MessageException)

@Dsl
data class OnLiveContext(val roomId: Int)

@Dsl
data class OnPrepareContext(val roomId: Int)

@Dsl
data class OnRoomRankChangeContext(val rank: RoomRank)

@KtorExperimentalAPI
class TypedMessageHandlerImpl(
    private var receiveDanmu: (OnReceiveDanmuContext.() -> Unit)? = null,
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
        roomRankChange = context
    }

    override fun handle(message: String) {
        try {
            allTypeMessage?.invoke(OnAllTypeMessageContext(message))
            val json = JsonParser.parseString(message)!!
            val cmd = json.asJsonObject["cmd"]?.asString ?: throw Exception("unexpect json format, missing [cmd] !")
            when (searchCMD(cmd)) {
                DANMU_MSG -> {
                    val info = json.asJsonObject["info"]!!
                    val said = info.asJsonArray[1].asString
                    val user = with(info.asJsonArray[2]) {
                        User(
                            uid = asJsonArray[0].asInt,
                            name = asJsonArray[1].asString,
                            isAdmin = asJsonArray[2].asInt == 1,//0,1
                            isVip = asJsonArray[3].asInt == 1,//0,1
                            isAnnualVip = asJsonArray[4].asInt == 1 //0,1
                        )
                    }
                    val badge = with(info.asJsonArray[3]) {
                        if (isJsonNull || asJsonArray.size() == 0)
                            null
                        else
                            Badge(
                                asJsonArray[0].let { it?.asInt ?: 0 },
                                asJsonArray[1].let { it?.asString ?: "" },
                                asJsonArray[2].let { it?.asString ?: "" },
                                asJsonArray[3].let { it?.asInt ?: 0 }
                            )
                    }
                    val userLevel = with(info.asJsonArray[4]) {
                        UserLevel(
                            asJsonArray[0].asInt,
                            asJsonArray[2].asInt,
                            asJsonArray[3].asString
                        )
                    }
                    receiveDanmu?.invoke(OnReceiveDanmuContext(user, badge, userLevel, said))
                }
                SEND_GIFT -> {
                    val gift = gson.fromJson<Gift>(json.asJsonObject["data"]!!.asString)
                    receiveGift?.invoke(OnReceiveGiftContext(gift))
                }
                WELCOME -> {
                    val user = json.asJsonObject["data"]!!.asJsonObject["uname"]!!.asString
                    vipEnterInLiveRoom?.invoke(OnVipEnterInLiveRoomContext(user))
                }
                WELCOME_GUARD -> {
                    val user = json.asJsonObject["data"]!!.asJsonObject["username"]!!.asString
                    guardEnterInLiveRoom?.invoke(OnGuardEnterInLiveRoomContext(user))
                }
                LIVE -> {
                    val roomId = json.asJsonObject["roomid"]!!.asInt
                    live?.invoke(OnLiveContext(roomId))
                }
                PREPARING -> {
                    val roomId = json.asJsonObject["roomid"]!!.asInt
                    prepare?.invoke(OnPrepareContext(roomId))
                }
                ROOM_RANK -> {
                    val rank = gson.fromJson<RoomRank>(json.asJsonObject["data"]!!.toString())
                    roomRankChange?.invoke(OnRoomRankChangeContext(rank))
                }
                UNKNOWN -> {
                    unknownTypeMessage?.invoke(OnUnknownTypeMessageContext(message, cmd))
                }
                else -> {
                }
            }
        } catch (e: Throwable) {
            error?.invoke(OnErrorContext(message, MessageException("catch an exception while handling a message", e)))
        }
    }
}

@KtorExperimentalAPI
inline fun typedMessageHandler(asString: TypedMessageHandler.() -> Unit) = TypedMessageHandlerImpl().apply(asString)