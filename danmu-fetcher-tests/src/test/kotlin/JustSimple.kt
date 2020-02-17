import ink.rubi.bilibili.live.connectLiveRoom
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking

@ExperimentalCoroutinesApi
@KtorExperimentalAPI
fun main() = runBlocking {
    connectLiveRoom(readLine()!!.toInt())
    Unit
}