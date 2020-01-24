import ink.rubi.bilibili.live.danmu.DanmuListener.receiveDanmu
import ink.rubi.bilibili.live.danmu.handler.simpleMessageHandler
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
@KtorExperimentalAPI
object MultiFetchersTest {

    @ObsoleteCoroutinesApi
    @JvmStatic
    fun main(args: Array<String>) {
        val roomIds = arrayOf(115, 92613)
        runBlocking {
            launch(newFixedThreadPoolContext(10, "陆夫人")) {
                receiveDanmu(roomIds[0]) {
                    simpleMessageHandler {
                        onReceiveDanmu { user, said ->
                            log.info("$user : $said")
                        }
                    }
                }
            }
            launch(newFixedThreadPoolContext(10, "A_PI")) {
                receiveDanmu(roomIds[1]) {
                    simpleMessageHandler {
                        onReceiveDanmu { user, said ->
                            log.warn("$user : $said")
                        }
                    }
                }
            }
//            delay(60000)
//            job.cancel()
        }
    }
}