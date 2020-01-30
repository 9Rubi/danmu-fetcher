import ink.rubi.bilibili.auth.api.login
import ink.rubi.bilibili.live.api.sendNormalMessageAsync
import ink.rubi.bilibili.live.bilibiliLiveRoom
import ink.rubi.bilibili.live.client
import io.ktor.client.statement.readText
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.*
import java.awt.Toolkit
import java.util.*
import java.util.concurrent.Executors

const val QRCODE_RESOLVE_BY_CLI = "https://cli.im/api/qrcode/code"
fun qrcodeHtmlUrl(url: String) = "$QRCODE_RESOLVE_BY_CLI?text=$url&mhid=40PDDFm8yJ0hMHcmLNFVMK4"

@ExperimentalCoroutinesApi
@KtorExperimentalAPI
object APITest {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        val userInfo = client.login{
            println(qrcodeHtmlUrl(it))
        }
        userInfo?.let { println("登录成功 : ${it.uname}") }
        val pool = Executors.newFixedThreadPool(10)
        val job1 = pool.asCoroutineDispatcher()
            .let { CoroutineScope(it).bilibiliLiveRoom(958282, anonymous = false) }
        val job2 = launch {
            while (true) {
                val line = readLine()
                line?.let {
                    when (it) {
                        "quit" -> cancel()
                        else -> {
                            if (it.isNotEmpty())
                                println(client.sendNormalMessageAsync(it, 958282).await().readText())
                        }
                    }
                }
            }
        }
    }
}