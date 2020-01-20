import ink.rubi.bilibili.live.danmu.DanmuListener.receiveDanmu
import ink.rubi.bilibili.live.danmu.messageHandler
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val log: Logger = LoggerFactory.getLogger("[test]")

object Test {
    @ExperimentalCoroutinesApi
    @FlowPreview
    @KtorExperimentalAPI
    @JvmStatic
    fun main(args: Array<String>) {
        val roomId = 5050
        runBlocking {
            val job = launch {
                receiveDanmu(roomId){
                    messageHandler {
                        onReceiveDanmu { user, said ->
                            log.info("[$user] : $said")
                        }
                        onReceiveGift { user, num, giftName ->
                            log.info("[$user] 送出了 $num 个 [$giftName]")
                        }
                        onSomeOneEnterInLiveRoom {
                            log.info("[$it] 进入了直播间")
                        }
                        onGuardEnterInLiveRoom {
                            log.info("[舰长][$it] 进入了直播间")
                        }
                    }
                }
            }
            delay(10000)
            job.cancel()
        }
    }
}