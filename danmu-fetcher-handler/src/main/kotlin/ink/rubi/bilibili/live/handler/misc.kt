package ink.rubi.bilibili.live.handler

import com.google.gson.Gson
import ink.rubi.bilibili.common.FetcherContext
import io.ktor.util.KtorExperimentalAPI
import org.slf4j.Logger
import org.slf4j.LoggerFactory


internal val log: Logger = LoggerFactory.getLogger("[danmu-handler]")

internal val useJson = Gson()

@KtorExperimentalAPI
fun defaultEventHandler() = simpleEventHandler {
    onConnect {
        log.info("connect!")
    }
    onConnected {
        log.info("connected!")
    }
    onLoginSuccess {
        log.info("login success!")
    }
    onLoginFail {
        log.info("login failed!")
    }
    onDisconnect {
        log.info("disconnect!")
    }
    onLogin {
        log.info("login ...")
    }
}
@KtorExperimentalAPI
fun defaultMessageHandler() = simpleMessageHandler {
    onReceiveDanmu { user, said ->
        log.info("[$user] : $said")
    }
    onReceiveGift { user, num, giftName ->
        log.info("[$user] 送出了 $num 个 [$giftName]")
    }
}