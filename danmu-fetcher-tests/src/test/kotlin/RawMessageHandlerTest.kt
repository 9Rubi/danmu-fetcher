import ink.rubi.bilibili.live.connectLiveRoom
import ink.rubi.bilibili.live.handler.rawMessageHandler
import ink.rubi.bilibili.live.objectMapper
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@ExperimentalCoroutinesApi
@KtorExperimentalAPI
object RawMessageHandlerTest {

    @JvmStatic
    fun main(args: Array<String>) {
        val roomId = 308543
        runBlocking {
            val job = launch {
                connectLiveRoom(roomId, rawMessageHandler {
                    onMessage {
                        log.info("raw message:$it")
                        val json = objectMapper.readTree(it)
                        if (json["cmd"].asText() == "DANMU_MSG"){
                            val userInfo =json["info"][2].toString()
                            log.info("user info :$userInfo")
                        }
                    }
                })
            }
//            delay(958282)
//            job.cancel()
        }
    }
}
