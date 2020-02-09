package ink.rubi.bilibili.live.handler

@MessageHandlerDsl
interface MessageHandler {
    fun handle(message: String)
}


@DslMarker
annotation class MessageHandlerDsl