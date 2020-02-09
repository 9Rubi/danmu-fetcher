import ink.rubi.bilibili.live.connectLiveRoom
import ink.rubi.bilibili.live.handler.rawMessageHandler
import ink.rubi.bilibili.live.handler.typedMessageHandler
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
        val roomId = readLine()!!.toInt()
        runBlocking {
            val job = launch {
                connectLiveRoom(roomId,
                    typedMessageHandler {
                        onReceiveDanmu {
                            log.info("user          : $user")
                            log.info("badge         : $badge")
                            log.info("userLevel     : $userLevel")
                            log.info("said          : $said")
                        }
                        onReceiveGift {
                            log.info("user          : ${gift.uname}")
                            log.info("num           : ${gift.num}")
                            log.info("giftName      : ${gift.giftName}")
                        }
                        onVipEnterInLiveRoom { log.info("[$user] 进入了直播间") }
                        onGuardEnterInLiveRoom { log.info("[舰长][$user] 进入了直播间") }
                        onUnknownTypeMessage { log.warn(message) }
                        onAllTypeMessage { log.error(message) }
                        onLive { log.info("[${this.roomId}] 开始直播") }
                        onPrepare { log.info("[${this.roomId}] 准备直播") }
                        onRoomRankChange {
                            log.info("roomid        :${rank.roomid}")
                            log.info("room_rank     :${rank.rank_desc}")
                        }
                        onError { log.error("catch exception :", e) }
                    }
                )
            }
            delay(60000)
            job.cancel()
        }
    }


}