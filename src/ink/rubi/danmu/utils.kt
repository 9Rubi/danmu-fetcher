package ink.rubi.danmu

import java.io.ByteArrayOutputStream
import java.util.zip.InflaterOutputStream

/**
 * copy from [com.sun.javafx.util.Utils]#convertUnicode()
 */
fun unescapeUnicode(src: String): String? {
    val buflen: Int
    var ch: Char
    var unicodeConversionBp = -1
    val buf: CharArray = src.toCharArray()
    buflen = buf.size
    var bp: Int = -1
    val dst = CharArray(buflen)
    var dstIndex = 0
    while (bp < buflen - 1) {
        ch = buf[++bp]
        if (ch == '\\') {
            if (unicodeConversionBp != bp) {
                bp++
                ch = buf[bp]
                if (ch == 'u') {
                    do {
                        bp++
                        ch = buf[bp]
                    } while (ch == 'u')
                    val limit = bp + 3
                    if (limit < buflen) {
                        val c = ch
                        val result = Character.digit(c, 16)
                        if (result >= 0 && c.toInt() > 0x7f) {
                            ch = "0123456789abcdef"[result]
                        }
                        var d = result
                        var code = d
                        while (bp < limit && d >= 0) {
                            bp++
                            ch = buf[bp]
                            val c1 = ch
                            val result1 = Character.digit(c1, 16)
                            if (result1 >= 0 && c1.toInt() > 0x7f) {
                                ch = "0123456789abcdef"[result1]
                            }
                            d = result1
                            code = (code shl 4) + d
                        }
                        if (d >= 0) {
                            ch = code.toChar()
                            unicodeConversionBp = bp
                        }
                    }
                } else {
                    bp--
                    ch = '\\'
                }
            }
        }
        dst[dstIndex++] = ch
    }
    return String(dst, 0, dstIndex)
}

fun uncompressZlib(input: ByteArray): ByteArray =
    ByteArrayOutputStream().use { InflaterOutputStream(it).use { output -> output.write(input) }; return@use it.toByteArray() }

