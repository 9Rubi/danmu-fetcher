import ink.rubi.bilibili.live.connectLiveRoom
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@KtorExperimentalAPI
@ExperimentalCoroutinesApi
fun main() {
  runBlocking {
      connectLiveRoom(readLine()!!.toInt())
  }
}