package ink.rubi.bilibili.live.danmu.data


data class User(
    val uid: Int,
    val name: String
)

/**
 * 狗牌
 */
data class Badge(
    val level: Int,
    val shortName: String,
    val fullName: String,
    val roomId: Int
)

/**
 * 直播 用户等级
 */
data class UserLevel(
    val level: Int,
    val howManyPeopleInFrontOfYou: Int,
    val simpleDisplayOfRank: String
)
