import ink.rubi.bilibili.live.connectLiveRoom
import ink.rubi.bilibili.live.handler.rawMessageHandler
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@ExperimentalCoroutinesApi
@KtorExperimentalAPI
object RawMessageHandlerTest {

    @JvmStatic
    fun main(args: Array<String>) {
        val roomId = 958282
        runBlocking {
            val job = launch {
                connectLiveRoom(roomId, rawMessageHandler {
                    onMessage {
                        log.info("raw message:$it")
                    }
                }
                )
            }
//            delay(958282)
//            job.cancel()
        }
    }
}
