package ink.rubi.bilibili.live.handler

enum class EventType(val desc: String) {
    LOGIN("登录事件"),
    LOGIN_SUCCESS("登录成功"),
    LOGIN_FAILED("登录失败"),

    CONNECT("连接事件"),
    CONNECTED("已连接"),
    CONNECT_FAILED("连接失败"),

    DISCONNECT("失去连接")
}

interface EventHandler {
    fun handle(eventType: EventType)
}