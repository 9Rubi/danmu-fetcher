package ink.rubi.bilibili.live.danmu

import com.fasterxml.jackson.core.JsonParseException

class MessageTypeHandler(
    var receiveDanmu: ((user: String, said: String) -> Unit)? = null,
    var receiveGift: ((user: String, num: Int, giftName: String) -> Unit)? = null,
    var someOneEnterInLiveRoom: ((user: String) -> Unit)? = null,
    var guardEnterInLiveRoom: ((user: String) -> Unit)? = null,
    var allTypeMessage: ((message: String) -> Unit)? = null,
    var unknownTypeMessage: ((message: String) -> Unit)? = null,
    var error: ((message: String, e: Throwable) -> Unit)? = null
) : MessageTypeHandlerDSL {
    override fun onReceiveDanmu(block: (user: String, said: String) -> Unit) { receiveDanmu = block }
    override fun onReceiveGift(block: (user: String, num: Int, giftName: String) -> Unit){ receiveGift = block }
    override fun onSomeOneEnterInLiveRoom(block: (user: String) -> Unit)  { someOneEnterInLiveRoom = block }
    override fun onGuardEnterInLiveRoom(block: (user: String) -> Unit){guardEnterInLiveRoom = block }
    override fun onAllTypeMessage(block: (message: String) -> Unit) { allTypeMessage = block }
    override fun onUnknownTypeMessage(block: (message: String) -> Unit) {unknownTypeMessage = block }
    override fun onError(block: (message: String, e: Throwable) -> Unit) { error = block }
}

fun messageHandler(content: MessageTypeHandlerDSL.() -> Unit) = MessageTypeHandler().apply(content)

interface MessageTypeHandlerDSL {
    fun onReceiveDanmu(block: (user: String, said: String) -> Unit)
    fun onReceiveGift(block: (user: String, num: Int, giftName: String) -> Unit)
    fun onSomeOneEnterInLiveRoom(block: (user: String) -> Unit)
    fun onGuardEnterInLiveRoom(block: (user: String) -> Unit)
    fun onAllTypeMessage(block: (message: String) -> Unit)
    fun onUnknownTypeMessage(block: (message: String) -> Unit)
    fun onError(block: (message: String, e: Throwable) -> Unit)
}

internal fun handleMessage(message: String, handler: MessageTypeHandler) {
    try {
        handler.allTypeMessage?.invoke(message)
        val json = objectMapper.readTree(message)
        val cmd = json["cmd"]?.textValue() ?: throw Exception("unexpect json format, missing [cmd] !")
        when (cmd) {
            CMD.DANMU_MSG.name -> {
                val said = json["info"][1].textValue()!!
                val user = json["info"][2][1].textValue()!!
                handler.receiveDanmu?.invoke(user, said)
            }
            CMD.SEND_GIFT.name -> {
                val user = json["data"]["uname"].textValue()!!
                val num = json["data"]["num"].intValue()
                val giftName = json["data"]["giftName"].textValue()!!
                handler.receiveGift?.invoke(user, num, giftName)
            }
            CMD.WELCOME.name -> {
                val user = json["data"]["uname"].textValue()!!
                handler.someOneEnterInLiveRoom?.invoke(user)
            }
            CMD.WELCOME_GUARD.name -> {
                val user = json["data"]["username"].textValue()!!
                handler.guardEnterInLiveRoom?.invoke(user)
            }
            else -> {
                handler.unknownTypeMessage?.invoke(message)
            }
        }
    } catch (e: Throwable) {
        handler.error?.invoke(message, e)
    }
}