package ink.rubi.bilibili.auth.api

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.readValue

import ink.rubi.bilibili.auth.data.QRCodeLoginInfo
import ink.rubi.bilibili.auth.data.SuccessScan
import ink.rubi.bilibili.auth.data.UserInfo
import ink.rubi.bilibili.live.data.NormalResponse
import ink.rubi.bilibili.live.objectMapper
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

const val QRCODE_GET_LOGIN_URL = "https://passport.bilibili.com/qrcode/getLoginUrl"
const val QRCODE_GET_LOGIN_INFO = "https://passport.bilibili.com/qrcode/getLoginInfo"
const val QRCODE_RESOLVE_BY_CLI = "https://cli.im/api/qrcode/code"
const val USER_INFO_URL = "https://account.bilibili.com/home/userInfo"

fun HttpClient.getUserInfoAsync(): Deferred<UserInfo?> {
    return async {
        this@getUserInfoAsync.get<NormalResponse<UserInfo>>(
            USER_INFO_URL
        ).data
    }
}


fun HttpClient.getQRCodeLoginUrlAsync(): Deferred<QRCodeLoginInfo> {
    return async {
        this@getQRCodeLoginUrlAsync.get<QRCodeLoginInfo>(QRCODE_GET_LOGIN_URL)
    }
}

fun HttpClient.getQRCodeLoginInfoAsync(oauthKey: String): Deferred<JsonNode> {
    return async {
        this@getQRCodeLoginInfoAsync.post<JsonNode>(QRCODE_GET_LOGIN_INFO) {
            parameter("oauthKey", oauthKey)
            parameter("gourl", "https://passport.bilibili.com/account/security")
        }
    }
}

suspend fun HttpClient.suspendUtilLoginSuccess(oauthKey: String): SuccessScan {
    while (true) {
        delay(5000)
        val json = getQRCodeLoginInfoAsync(oauthKey).await()
        if (json["status"].asBoolean(false)) {
            return objectMapper.readValue<SuccessScan>(json.toString())
        }
    }
}

fun qrcodeHtmlUrl(url: String) = "$QRCODE_RESOLVE_BY_CLI?text=$url&mhid=40PDDFm8yJ0hMHcmLNFVMK4"