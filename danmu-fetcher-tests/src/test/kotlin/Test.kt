import ink.rubi.bilibili.live.connectLiveRoom
import ink.rubi.bilibili.live.handler.typedMessageHandler
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

@ExperimentalCoroutinesApi
@KtorExperimentalAPI
object Test {
    @JvmStatic
    fun main(args: Array<String>) {
        val roomId = 21809202

        val pool = Executors.newFixedThreadPool(10)
        val job1 = pool.asCoroutineDispatcher()
            .let { dispatcher ->
                CoroutineScope(dispatcher).connectLiveRoom(roomId,
                    typedMessageHandler {
                        onReceiveDanmu {
                            val badgeInfo = badge?.let {
                                "[${it.shortName} ${it.level}]"
                            } ?: ""
                            val vipInfo = if (user.isVip)
                                if (user.isAnnualVip) "[年费老爷]" else "[老爷]"
                            else
                                ""
                            val levelInfo = "[UL ${userLevel.level}]"
                            log.info("$vipInfo$badgeInfo$levelInfo${user.name} : $said")
                        }
                        onReceiveGift {
                            log.info("[${gift.uname}] 送出了 ${gift.num} 个 ${gift.giftName}")
                        }
                        onVipEnterInLiveRoom { log.info("[$user] 进入了直播间") }
                        onGuardEnterInLiveRoom { log.info("[舰长][$user] 进入了直播间") }
                        onUnknownTypeMessage {  log.warn(message) }
//                        onAllTypeMessage { log.error(it) }
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
    }


}