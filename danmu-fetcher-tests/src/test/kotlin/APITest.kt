import ink.rubi.bilibili.live.DanmuListener.connectLiveRoom
import ink.rubi.bilibili.live.client
import ink.rubi.bilibili.live.api.sendNormalMessageAsync
import io.ktor.client.statement.readText
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors

@ExperimentalCoroutinesApi
@KtorExperimentalAPI
object APITest {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val job = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
            .let { CoroutineScope(it).connectLiveRoom(958282) }
        val info = client.getQRCodeLoginUrlAsync().await()
        println(qrcodeHtmlUrl(info.data.url))
        println(client.suspendUtilLoginSuccess(info.data.oauthKey))
        println(client.getUserInfoAsync().await())
        while (true) {
            val line = readLine()
            line?.let {
                if (line.isNotEmpty())
                    println(client.sendNormalMessageAsync(it, 958282).await().readText())
            }
        }
    }
}