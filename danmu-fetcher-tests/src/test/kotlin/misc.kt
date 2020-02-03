import ink.rubi.bilibili.live.api.getWebTitlesAsync
import ink.rubi.bilibili.live.client
import ink.rubi.bilibili.live.titlesDatabase
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val log: Logger = LoggerFactory.getLogger("[test]")

fun main() {
        runBlocking {
            client.getWebTitlesAsync().await().map { it.identification to it }.toMap(titlesDatabase)
            println(titlesDatabase["title-287-1"]!!)

        }
}