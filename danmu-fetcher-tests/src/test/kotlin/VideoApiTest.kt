import ink.rubi.bilibili.live.client
import ink.rubi.bilibili.video.api.getLiveStatusAsync
import ink.rubi.bilibili.video.api.getReplyFromVideoAsync
import io.ktor.client.statement.readText
import io.ktor.client.statement.response
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.runBlocking

@KtorExperimentalAPI
fun main(): Unit = runBlocking{
    println(client.getLiveStatusAsync(17996762).await())
    client.getReplyFromVideoAsync(87679483).await().replies.forEach {
        println("${it.member.uname} : ${it.content.message}")
    }
    Unit
}