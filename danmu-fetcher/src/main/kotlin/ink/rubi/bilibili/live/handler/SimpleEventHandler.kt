package ink.rubi.bilibili.live.handler

import ink.rubi.bilibili.live.handler.EventType.*

interface SimpleEventHandler : EventHandler {
    fun onConnect(block: () -> Unit)
    fun onConnected(block: () -> Unit)
    fun onDisconnect(block: () -> Unit)
    fun onLogin(block: () -> Unit)
    fun onLoginSuccess(block: () -> Unit)
    fun onLoginFail(block: () -> Unit)
}


class SimpleEventHandlerImpl : SimpleEventHandler {
    private var connect: (() -> Unit)? = null
    private var connected: (() -> Unit)? = null
    private var disconnect: (() -> Unit)? = null
    private var loginFail: (() -> Unit)? = null
    private var login: (() -> Unit)? = null
    private var loginSuccess: (() -> Unit)? = null
    override fun onConnect(block: () -> Unit) {
        connect = block
    }

    override fun onConnected(block: () -> Unit) {
        connected = block
    }

    override fun onDisconnect(block: () -> Unit) {
        disconnect = block
    }

    override fun onLogin(block: () -> Unit) {
        login = block
    }

    override fun onLoginSuccess(block: () -> Unit) {
        loginSuccess = block
    }

    override fun onLoginFail(block: () -> Unit) {
        loginFail = block
    }

    override fun handle(eventType: EventType) {
        when (eventType) {
            CONNECTED -> connected?.invoke()
            CONNECT -> connect?.invoke()
            LOGIN -> login?.invoke()
            LOGIN_SUCCESS -> loginSuccess?.invoke()
            LOGIN_FAILED -> loginFail?.invoke()
            DISCONNECT -> disconnect?.invoke()
            else -> {
            }
        }
    }
}

fun simpleEventHandler(content: SimpleEventHandler.() -> Unit) = SimpleEventHandlerImpl().apply(content)
