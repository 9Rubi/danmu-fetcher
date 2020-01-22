import ink.rubi.bilibili.live.danmu.DanmuListener.receiveDanmu
import ink.rubi.bilibili.live.danmu.handler.rawMessageHandler
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
@KtorExperimentalAPI
object RawMessageHandlerTest {

    @JvmStatic
    fun main(args: Array<String>) {
        val roomId = 92613
        runBlocking {
            val job = launch {
                receiveDanmu(roomId) {
                    rawMessageHandler {
                        onMessage {
                            log.info("raw message:$it")
                        }
                    }
                }
            }
            delay(10000)
//            job.cancel()
        }
    }
}
