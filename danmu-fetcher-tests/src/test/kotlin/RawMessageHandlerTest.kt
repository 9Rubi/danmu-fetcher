import ink.rubi.bilibili.live.danmu.DanmuListener.receiveDanmu
import ink.rubi.bilibili.live.danmu.handler.rawMessageHandler
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@ExperimentalCoroutinesApi
@KtorExperimentalAPI
object RawMessageHandlerTest {

    @JvmStatic
    fun main(args: Array<String>) {
        val roomId = 92613
        runBlocking {
            launch{
                receiveDanmu(roomId) {
                    rawMessageHandler {
                        onMessage {
                            log.warn("raw message:$it")
                        }
                    }
                }
            }
        }
    }
}
