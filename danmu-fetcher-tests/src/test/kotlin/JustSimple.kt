import ink.rubi.bilibili.live.danmu.DanmuListener.connectLiveRoom
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@KtorExperimentalAPI
@ExperimentalCoroutinesApi
fun main() = runBlocking {
    val job = launch { connectLiveRoom(115) }
    delay(15_000)
    job.cancel()
}