package ink.rubi.bilibili.common.data

data class DataHolder<T>(
    val code: Int? = null,
    val msg: String? = null,
    val status: Boolean? = null,
    val data: T? = null,
    val message: String? = null,
    val ttl: Int? = null
)



