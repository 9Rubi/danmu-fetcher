package ink.rubi.bilibili.live.danmu

import java.io.ByteArrayOutputStream
import java.util.zip.InflaterOutputStream


fun uncompressZlib(input: ByteArray): ByteArray =
    ByteArrayOutputStream().use { InflaterOutputStream(it).use { output -> output.write(input) }; return@use it.toByteArray() }
