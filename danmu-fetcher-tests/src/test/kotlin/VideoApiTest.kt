import ink.rubi.bilibili.live.DanmuListenerContext.defaultClient
import ink.rubi.bilibili.video.api.getLiveStatusAsync
import ink.rubi.bilibili.video.api.getReplyFromVideoAsync
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.runBlocking

@KtorExperimentalAPI
fun main(): Unit = runBlocking{
    println(defaultClient.getLiveStatusAsync(17996762).await())
    defaultClient.getReplyFromVideoAsync(87679483).await().replies.forEach {
        println("${it.member.uname} : ${it.content.message}")
    }
    Unit
}