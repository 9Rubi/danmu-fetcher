import ink.rubi.bilibili.live.DanmuListener.connectLiveRoom
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import java.time.Duration
import java.util.concurrent.Executors

@KtorExperimentalAPI
@ExperimentalCoroutinesApi
object VerySimpleTest {

    @JvmStatic
    fun main(args: Array<String>) {
        val threadPool = Executors.newFixedThreadPool(10)
        val scope = CoroutineScope(context = threadPool.asCoroutineDispatcher())
        val job = scope.launch { connectLiveRoom(115) }
        sleep(Duration.ofSeconds(10))
        job.cancel()
        threadPool.shutdown()
    }
}

fun sleep(duration: Duration) {
    Thread.sleep(duration.toMillis())
}