package ink.rubi.bilibili.video.data

data class LiveStatus(
    val status: Int,
    val url: String
) {
    fun isOnLive(): Boolean {
        return status == 1
    }
}