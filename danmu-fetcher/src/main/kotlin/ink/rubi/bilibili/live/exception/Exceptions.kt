package ink.rubi.bilibili.live.exception

class MessageException(s: String, e: Throwable) : RuntimeException(s, e)

class UnknownProtocolTypeException(s: String) : RuntimeException(s)