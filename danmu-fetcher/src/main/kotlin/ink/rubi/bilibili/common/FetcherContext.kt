package ink.rubi.bilibili.common

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import ink.rubi.bilibili.live.data.WebTitle
import ink.rubi.bilibili.live.handler.simpleEventHandler
import ink.rubi.bilibili.live.handler.simpleMessageHandler
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.BrowserUserAgent
import io.ktor.client.features.cookies.HttpCookies
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import io.ktor.client.features.websocket.WebSockets
import io.ktor.http.ContentType
import io.ktor.util.KtorExperimentalAPI
import org.slf4j.Logger
import org.slf4j.LoggerFactory




@KtorExperimentalAPI
object FetcherContext {
    internal const val loadBalance = true
    internal val log: Logger = LoggerFactory.getLogger("[danmu-client]")
    internal val titlesDatabase = mutableMapOf<String, WebTitle>()
    internal val gson =GsonBuilder().apply {

    }.create()!!
    val defaultClient = HttpClient(CIO) {
        install(WebSockets)
        install(Logging) {
            level = LogLevel.NONE
        }
        install(HttpCookies)
        install(JsonFeature) {
            serializer = GsonSerializer()
            acceptContentTypes = acceptContentTypes + ContentType("text", "json")
        }
        BrowserUserAgent()
    }
    internal val defaultEventHandler = simpleEventHandler {
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
    internal val defaultMessageHandler = simpleMessageHandler {
        onReceiveDanmu { user, said ->
            log.info("[$user] : $said")
        }
        onReceiveGift { user, num, giftName ->
            log.info("[$user] 送出了 $num 个 [$giftName]")
        }
    }
}