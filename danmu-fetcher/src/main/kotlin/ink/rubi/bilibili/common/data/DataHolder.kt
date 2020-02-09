package ink.rubi.bilibili.common.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties


@JsonIgnoreProperties(ignoreUnknown = true)
data class DataHolder<T>(
    val code: Int,
    val msg: String?,
    val status: Boolean?,
    val `data`: T?,
    val message: String?,
    val ttl: Int?
)