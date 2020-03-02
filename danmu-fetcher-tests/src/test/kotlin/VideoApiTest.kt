import ink.rubi.bilibili.common.FetcherContext.defaultClient
import ink.rubi.bilibili.video.api.getLiveStatusAsync
import ink.rubi.bilibili.video.api.getReplyFromVideoAsync
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking

@ExperimentalCoroutinesApi
@KtorExperimentalAPI
fun main(): Unit = runBlocking{
    val defaultClient = defaultClient()
    println(defaultClient.getLiveStatusAsync(17996762).await())
    defaultClient.getReplyFromVideoAsync(87679483).await().replies.forEach {
        println("${it.member.uname} : ${it.content.message}")
    }
    Unit
}