import ink.rubi.bilibili.live.bilibiliLiveRoom
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@KtorExperimentalAPI
@ExperimentalCoroutinesApi
fun main() = runBlocking {
    val job = launch { bilibiliLiveRoom(115) }
    delay(15_000)
    job.cancel()
}