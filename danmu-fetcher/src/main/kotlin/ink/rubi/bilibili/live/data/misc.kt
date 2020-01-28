package ink.rubi.bilibili.live.data

import java.io.ByteArrayOutputStream
import java.util.zip.InflaterOutputStream

fun uncompressZlib(input: ByteArray): ByteArray =
    ByteArrayOutputStream().use { InflaterOutputStream(it).use { output -> output.write(input) }; return@use it.toByteArray() }


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