package ink.rubi.bilibili.auth.data

data class UserInfo(
    val bCoins: Int,
    val coins: Double,
    val face: String,
    val level_info: LevelInfo,
    val nameplate_current: Any?,
    val official_verify: Int,
    val pendant_current: String,
    val pointBalance: Int,
    val uname: String,
    val userStatus: String,
    val vipStatus: Int,
    val vipType: Int
)

data class LevelInfo(
    val current_exp: Int,
    val current_level: Int,
    val current_min: Int,
    val next_exp: Int
)

data class CantScan(
    val `data`: Int,
    val message: String,
    val status: Boolean
)


data class SuccessScan(
    val code: Int,
    val `data`: UrlData,
    val status: Boolean,
    val ts: Int
)

data class UrlData(
    val url: String
)

data class ExpireScan(
    val `data`: Int,
    val message: String,
    val status: Boolean
)

data class QRCodeLoginInfo(
    val code: Int,
    val `data`: KeyInfo,
    val status: Boolean,
    val ts: Int
)

data class KeyInfo(
    val oauthKey: String,
    val url: String
)