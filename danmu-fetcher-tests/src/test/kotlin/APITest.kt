import ink.rubi.bilibili.auth.api.login
import ink.rubi.bilibili.common.FetcherContext.defaultClient
import ink.rubi.bilibili.live.api.getBagDataAsync
import ink.rubi.bilibili.live.api.isSuccess
import ink.rubi.bilibili.live.api.sendDanmuAsync
import ink.rubi.bilibili.live.connectLiveRoom
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.*
import java.util.concurrent.Executors

const val QRCODE_RESOLVE_BY_CLI = "https://cli.im/api/qrcode/code"
fun qrcodeHtmlUrl(url: String) = "$QRCODE_RESOLVE_BY_CLI?text=$url&mhid=40PDDFm8yJ0hMHcmLNFVMK4"

@ExperimentalCoroutinesApi
@KtorExperimentalAPI
object APITest {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val roomId = readLine()!!.toInt()
        val defaultClient = defaultClient()
        val userInfo = defaultClient.login {
            println(qrcodeHtmlUrl(it))
        }
        userInfo?.let { println("登录成功 : ${it.uname}") }
        val bagData = defaultClient.getBagDataAsync().await()
        bagData.list.forEach(::println)
        val pool = Executors.newFixedThreadPool(10)
        val job1 = pool.asCoroutineDispatcher()
            .let { CoroutineScope(it).connectLiveRoom(roomId, anonymous = false ) }


        val job2 = launch {
            while (true) {
                val line = readLine()
                line?.let {
                    when (it) {
                        "quit" -> cancel()

                        else -> {
                            if (it.isNotEmpty()) {
                                val response = defaultClient.sendDanmuAsync(it, roomId).await()
                                println(if (!response.isSuccess()) response.message else "发送成功")
                            }

                        }
                    }
                }
            }
        }
    }
}