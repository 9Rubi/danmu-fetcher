import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.client.j2se.MatrixToImageWriter
import java.io.File
import java.nio.file.Paths

/**
 * 生成二维码
 * @param text 内容，可以是链接或者文本
 * @param path 生成的二维码位置
 * @param width 宽度，默认300
 * @param height 高度，默认300
 * @param format 生成的二维码格式，默认png
 */
fun encodeQRCode(text: String, path: String, width: Int = 300, height: Int = 300, format: String = "png") {
    val file = File(path);
    if (!file.parentFile.exists()) {
        assert(file.parentFile.mkdirs())
    }
    val hints = hashMapOf<EncodeHintType, Any>()
    hints[EncodeHintType.CHARACTER_SET] = "UTF-8";
    val bitMatrix = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);
    val outputPath = Paths.get(path)
    MatrixToImageWriter.writeToPath(bitMatrix, format, outputPath)
}
fun main() {
    encodeQRCode("https://www.bilibili.com","C:\\Users\\13447\\Desktop\\1.png")
}