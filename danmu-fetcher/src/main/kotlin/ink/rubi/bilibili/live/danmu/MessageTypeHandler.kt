package ink.rubi.bilibili.live.danmu

class MessageTypeHandler(
    var receiveDanmu: (user: String, said: String) -> Unit = { _, _ -> },
    var receiveGift: (user: String, num: Int, giftName: String) -> Unit = { _, _, _ -> },
    var someOneEnterInLiveRoom: (user: String) -> Unit = { _ -> },
    var guardEnterInLiveRoom: (user: String) -> Unit = { _ -> },
    var allTypeMessage: (message: String) -> Unit = { _ -> },
    var unknownTypeMessage: (message: String) -> Unit = { _ -> },
    var error: (message: String, e: Throwable) -> Unit = { _, _ -> }
) : MessageTypeHandlerDSL {
    override fun onReceiveDanmu(block: (user: String, said: String) -> Unit) {
        receiveDanmu = block
    }

    override fun onReceiveGift(block: (user: String, num: Int, giftName: String) -> Unit) {
        receiveGift = block
    }

    override fun onSomeOneEnterInLiveRoom(block: (user: String) -> Unit) {
        someOneEnterInLiveRoom = block
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

    override fun onError(block: (message: String, e: Throwable) -> Unit) {
        error = block
    }

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
        handler.allTypeMessage(message)
        val json = objectMapper.readTree(message)
        val cmd = json["cmd"]?.textValue() ?: throw Exception("wrong json , missing [cmd] !")
        when (cmd) {
            CMD.DANMU_MSG.name -> {
                val said = json["info"][1].textValue()!!
                val user = json["info"][2][1].textValue()!!
                handler.receiveDanmu(user, said)
            }
            CMD.SEND_GIFT.name -> {
                val user = json["data"]["uname"].textValue()!!
                val num = json["data"]["num"].intValue()
                val giftName = json["data"]["giftName"].textValue()!!
                handler.receiveGift(user, num, giftName)
            }
            CMD.WELCOME.name -> {
                val user = json["data"]["uname"].textValue()!!
                handler.someOneEnterInLiveRoom(user)
            }
            CMD.WELCOME_GUARD.name -> {
                val user = json["data"]["username"].textValue()!!
                handler.guardEnterInLiveRoom(user)
            }
            else -> {
                handler.unknownTypeMessage(message)
            }
        }
    } catch (e: Throwable) {
        handler.error(message, e)
    }
}