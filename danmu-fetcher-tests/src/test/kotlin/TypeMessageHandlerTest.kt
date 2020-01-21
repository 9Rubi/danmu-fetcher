import ink.rubi.bilibili.live.danmu.DanmuListener.receiveDanmu
import ink.rubi.bilibili.live.danmu.handler.typedMessageHandler
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@ExperimentalCoroutinesApi
@KtorExperimentalAPI
object TypeMessageHandlerTest {

    @JvmStatic
    fun main(args: Array<String>) {
        val roomId = 734
        runBlocking {
            val job = launch {
                receiveDanmu(roomId) {
                    typedMessageHandler {
                        onReceiveDanmu {user, badge, userLevel, said ->
                            log.info("user          : $user")
                            log.info("badge         : $badge")
                            log.info("userLevel     : $userLevel")
                            log.info("said          : $said")
                        }
                        onReceiveGift { gift ->
                            log.info("$gift")
                        }
                        onSomeOneEnterInLiveRoom {
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
        }
    }


}