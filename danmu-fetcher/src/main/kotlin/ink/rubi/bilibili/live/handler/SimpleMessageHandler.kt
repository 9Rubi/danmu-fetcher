package ink.rubi.bilibili.live.handler

import com.google.gson.JsonParser
import ink.rubi.bilibili.common.FetcherContext.gson
import ink.rubi.bilibili.live.data.CMD.*
import ink.rubi.bilibili.live.data.searchCMD
import ink.rubi.bilibili.live.exception.MessageException
import io.ktor.util.KtorExperimentalAPI

interface SimpleMessageHandler : MessageHandler {
    fun onReceiveDanmu(block: (user: String, said: String) -> Unit)
    fun onReceiveGift(block: (user: String, num: Int, giftName: String) -> Unit)
    fun onVipEnterInLiveRoom(block: (user: String) -> Unit)
    fun onGuardEnterInLiveRoom(block: (user: String) -> Unit)
    fun onAllTypeMessage(block: (message: String) -> Unit)
    fun onUnknownTypeMessage(block: (message: String) -> Unit)
    fun onError(block: (message: String, e: MessageException) -> Unit)
}

@KtorExperimentalAPI
class SimpleMessageHandlerImpl(
    private var receiveDanmu: ((user: String, said: String) -> Unit)? = null,
    private var receiveGift: ((user: String, num: Int, giftName: String) -> Unit)? = null,
    private var vipEnterInLiveRoom: ((user: String) -> Unit)? = null,
    private var guardEnterInLiveRoom: ((user: String) -> Unit)? = null,
    private var allTypeMessage: ((message: String) -> Unit)? = null,
    private var unknownTypeMessage: ((message: String) -> Unit)? = null,
    private var error: ((message: String, e: MessageException) -> Unit)? = null
) : SimpleMessageHandler {
    override fun onReceiveDanmu(block: (user: String, said: String) -> Unit) {
        receiveDanmu = block
    }

    override fun onReceiveGift(block: (user: String, num: Int, giftName: String) -> Unit) {
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

    override fun onUnknownTypeMessage(block: (message: String) -> Unit) {
        unknownTypeMessage = block
    }

    override fun onError(block: (message: String, e: MessageException) -> Unit) {
        error = block
    }

    override fun handle(message: String) {
        try {
            allTypeMessage?.invoke(message)
            val json = JsonParser.parseString(message)
            val cmd = json.asJsonObject["cmd"]?.asString ?: throw Exception("unexpect json format, missing [cmd] !")
            when (searchCMD(cmd)) {
                DANMU_MSG -> {
                    val said = json.asJsonObject["info"]!!.asJsonArray[1].asString
                    val user = json.asJsonObject["info"]!!.asJsonArray[2].asJsonArray[1].asString
                    receiveDanmu?.invoke(user, said)
                }
                SEND_GIFT -> {
                    val user = json.asJsonObject["data"]!!.asJsonObject["uname"]!!.asString
                    val num = json.asJsonObject["data"]!!.asJsonObject["num"]!!.asInt
                    val giftName = json.asJsonObject["data"]!!.asJsonObject["giftName"]!!.asString
                    receiveGift?.invoke(user, num, giftName)
                }
                WELCOME -> {
                    val user = json.asJsonObject["data"]!!.asJsonObject["uname"]!!.asString
                    vipEnterInLiveRoom?.invoke(user)
                }
                WELCOME_GUARD -> {
                    val user = json.asJsonObject["data"]!!.asJsonObject["username"]!!.asString
                    guardEnterInLiveRoom?.invoke(user)
                }
                UNKNOWN -> {
                    unknownTypeMessage?.invoke(message)
                }
                else -> {
                }
            }
        } catch (e: Throwable) {
            error?.invoke(message, MessageException("catch an exception while handling a message : $message", e))
        }
    }
}
@KtorExperimentalAPI
inline fun simpleMessageHandler(asString: SimpleMessageHandler.() -> Unit) = SimpleMessageHandlerImpl().apply(asString)