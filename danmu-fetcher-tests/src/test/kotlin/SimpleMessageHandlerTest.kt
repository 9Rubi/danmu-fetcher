import ink.rubi.bilibili.live.connectLiveRoom
import ink.rubi.bilibili.live.handler.simpleEventHandler
import ink.rubi.bilibili.live.handler.simpleMessageHandler
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking

@ExperimentalCoroutinesApi
@KtorExperimentalAPI
object SimpleMessageHandlerTest {
    @JvmStatic
    fun main(args: Array<String>) {
        val roomId = 5486320
        val job = runBlocking {
            connectLiveRoom(roomId,
                simpleMessageHandler {
                    onReceiveDanmu { user, said -> log.info("[$user] : $said") }
                    onReceiveGift { user, num, giftName -> log.info("[$user] 送出了 $num 个 [$giftName]") }
                    onVipEnterInLiveRoom { log.info("[$it] 进入了直播间!") }
                    onGuardEnterInLiveRoom { log.info("[舰长][$it] 进入了直播间!") }
                    onUnknownTypeMessage { log.warn(it) }
//                onAllTypeMessage { log.error(it) }
                }, simpleEventHandler {
                    onLogin { log.info("准备登录") }
                    onLoginSuccess { log.info("登录成功") }
                    onLoginFail { log.info("登录失败") }
                    onConnect { log.info("正在连接到服务器...") }
                    onConnected { log.info("连接成功") }
                    onDisconnect { log.info("失去连接") }
                })
        }
    }
}
