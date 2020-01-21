package ink.rubi.bilibili.live.danmu.util

import com.fasterxml.jackson.module.kotlin.readValue
import ink.rubi.bilibili.live.danmu.WEB_TITLES
import ink.rubi.bilibili.live.danmu.client
import ink.rubi.bilibili.live.danmu.data.NormalResponse
import ink.rubi.bilibili.live.danmu.data.WebTitle
import ink.rubi.bilibili.live.danmu.objectMapper
import io.ktor.client.request.get
import java.io.ByteArrayOutputStream
import java.util.zip.InflaterOutputStream


fun uncompressZlib(input: ByteArray): ByteArray =
    ByteArrayOutputStream().use { InflaterOutputStream(it).use { output -> output.write(input) }; return@use it.toByteArray() }


suspend fun main() {

    val s = client.get<String>(WEB_TITLES)
    println(s)
    s.let { println(objectMapper.readValue<NormalResponse<List<WebTitle>>>(it)) }

}


data class Data(
    val colorful: Int,
    val h5_url: String,
    val identification: String,
    val level: String,
    val name: String,
    val source: String,
    val title_id: String,
    val url: String,
    val web_pic_url: String
)