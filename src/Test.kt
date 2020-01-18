import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import ink.rubi.danmu.CMD
import ink.rubi.danmu.DanmuListener
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val log: Logger = LoggerFactory.getLogger("[test]")
val objectMapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())


object Test {
    @KtorExperimentalAPI
    @FlowPreview
    @ExperimentalCoroutinesApi
    @JvmStatic
    fun main(args: Array<String>) {
        println("输入 直播间号码:")
        val roomId = readLine()!!.toInt()
        DanmuListener.doFetchDanmu(roomId) { cmd: String, rawJson: String ->
            val json = objectMapper.readTree(rawJson)
            when (cmd) {
                CMD.DANMU_MSG.name -> {
                    val said = json["info"][1].textValue()!!
                    val who = json["info"][2][1].textValue()!!
                    log.info("[$who] : $said")
                }
                CMD.SEND_GIFT.name -> {
                    val who = json["data"]["uname"].textValue()!!
                    val num = json["data"]["num"].intValue()
                    val gift = json["data"]["giftName"].textValue()!!
                    log.info("[$who] 送出了 $num 个 [$gift]")
                }
                CMD.WELCOME.name -> {
                    val who = json["data"]["uname"].textValue()!!
                    log.info("[$who] 进入了直播间")
                }
                CMD.WELCOME_GUARD.name -> {
                    val who = json["data"]["username"].textValue()!!
                    log.info("[舰长][$who] 进入了直播间")
                }
                else -> {
                    log.warn(rawJson)
                }
            }
        }
    }
}