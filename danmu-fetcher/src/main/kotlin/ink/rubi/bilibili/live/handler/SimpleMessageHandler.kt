package ink.rubi.bilibili.live.handler

import ink.rubi.bilibili.live.data.CMD.*
import ink.rubi.bilibili.live.data.searchCMD
import ink.rubi.bilibili.live.exception.MessageException
import ink.rubi.bilibili.live.objectMapper

interface SimpleMessageHandler : MessageHandler {
    fun onReceiveDanmu(block: (user: String, said: String) -> Unit)
    fun onReceiveGift(block: (user: String, num: Int, giftName: String) -> Unit)
    fun onVipEnterInLiveRoom(block: (user: String) -> Unit)
    fun onGuardEnterInLiveRoom(block: (user: String) -> Unit)
    fun onAllTypeMessage(block: (message: String) -> Unit)
    fun onUnknownTypeMessage(block: (message: String) -> Unit)
    fun onError(block: (message: String, e: MessageException) -> Unit)
}

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
            val json = objectMapper.readTree(message)
            val cmd = json["cmd"]?.textValue() ?: throw Exception("unexpect json format, missing [cmd] !")
            when (searchCMD(cmd)) {
                DANMU_MSG -> {
                    val said = json["info"][1].textValue()!!
                    val user = json["info"][2][1].textValue()!!
                    receiveDanmu?.invoke(user, said)
                }
                SEND_GIFT -> {
                    val user = json["data"]["uname"].textValue()!!
                    val num = json["data"]["num"].intValue()
                    val giftName = json["data"]["giftName"].textValue()!!
                    receiveGift?.invoke(user, num, giftName)
                }
                WELCOME -> {
                    val user = json["data"]["uname"].textValue()!!
                    vipEnterInLiveRoom?.invoke(user)
                }
                WELCOME_GUARD -> {
                    val user = json["data"]["username"].textValue()!!
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

fun simpleMessageHandler(content: SimpleMessageHandler.() -> Unit) = SimpleMessageHandlerImpl().apply(content)