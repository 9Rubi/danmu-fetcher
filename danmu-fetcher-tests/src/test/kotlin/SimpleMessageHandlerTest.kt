import ink.rubi.bilibili.live.danmu.DanmuListener.receiveDanmu
import ink.rubi.bilibili.live.danmu.handler.simpleMessageHandler
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
@KtorExperimentalAPI
object SimpleMessageHandlerTest {
    @JvmStatic
    fun main(args: Array<String>) {
        val roomId = 92613
        runBlocking {
            val job = launch {
                receiveDanmu(roomId) {
                    simpleMessageHandler {
                        onReceiveDanmu { user, said ->
                            log.info("[$user] : $said")
                        }
                        onReceiveGift { user, num, giftName ->
                            log.info("[$user] 送出了 $num 个 [$giftName]")
                        }
                        onVipEnterInLiveRoom {
                            log.info("[$it] 进入了直播间")
                        }
                        onGuardEnterInLiveRoom {
                            log.info("[舰长][$it] 进入了直播间")
                        }
                        onUnknownTypeMessage {
                            log.warn(it)
                        }
                        onAllTypeMessage {
                            log.error(it)
                        }
                    }
                }
            }
            delay(10000)
            job.cancel()
        }
    }
}
