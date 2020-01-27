import ink.rubi.bilibili.live.danmu.DanmuListener.connectLiveRoom
import ink.rubi.bilibili.live.danmu.handler.rawMessageHandler
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@ExperimentalCoroutinesApi
@KtorExperimentalAPI
object RawMessageHandlerTest {

    @JvmStatic
    fun main(args: Array<String>) {
        val roomId = 115
        runBlocking {
            val job = launch {
                connectLiveRoom(roomId, rawMessageHandler {
                        onMessage {
                            log.info("raw message:$it")
                        }
                    }
                )
            }
            delay(60000)
            job.cancel()
        }
    }
}
