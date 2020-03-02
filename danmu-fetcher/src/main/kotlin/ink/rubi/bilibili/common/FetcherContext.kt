package ink.rubi.bilibili.common

import com.google.gson.GsonBuilder
import ink.rubi.bilibili.live.data.WebTitle
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.BrowserUserAgent
import io.ktor.client.features.cookies.HttpCookies
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.websocket.WebSockets
import io.ktor.http.ContentType
import io.ktor.util.KtorExperimentalAPI
import org.slf4j.Logger
import org.slf4j.LoggerFactory


@KtorExperimentalAPI
object FetcherContext {
    internal val log: Logger = LoggerFactory.getLogger("[danmu-client]")
    internal val titlesDatabase = mutableMapOf<String, WebTitle>()
    internal val gson = GsonBuilder().create()!!
    fun defaultClient() = HttpClient(CIO) {
        install(WebSockets)
        install(HttpCookies)
        install(JsonFeature) {
            serializer = GsonSerializer()
            acceptContentTypes = acceptContentTypes + ContentType("text", "json")
        }
        BrowserUserAgent()
    }
}
