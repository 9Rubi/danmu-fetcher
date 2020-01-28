package ink.rubi.bilibili.live.exception

class MessageException(s: String, e: Throwable) : RuntimeException()

class UnknownProtocolTypeException(s: String) : RuntimeException()