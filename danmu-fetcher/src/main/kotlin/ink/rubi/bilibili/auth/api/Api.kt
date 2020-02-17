package ink.rubi.bilibili.auth.api

import com.google.gson.JsonElement
import ink.rubi.bilibili.auth.data.QRCodeLoginInfo
import ink.rubi.bilibili.auth.data.UrlData
import ink.rubi.bilibili.auth.data.UserInfo
import ink.rubi.bilibili.auth.exception.AuthException
import ink.rubi.bilibili.common.FetcherContext.gson
import ink.rubi.bilibili.common.api.BILIBILI_DOMAIN
import ink.rubi.bilibili.common.data.DataHolder
import ink.rubi.bilibili.common.fromJson
import io.ktor.client.HttpClient
import io.ktor.client.features.cookies.cookies
import io.ktor.client.features.cookies.get
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

const val QRCODE_GET_LOGIN_URL = "https://passport.bilibili.com/qrcode/getLoginUrl"
const val QRCODE_GET_LOGIN_INFO = "https://passport.bilibili.com/qrcode/getLoginInfo"
const val USER_INFO_URL = "https://account.bilibili.com/home/userInfo"

fun HttpClient.getUserInfoAsync(): Deferred<UserInfo?> {
    return async {
        get<DataHolder<UserInfo>>(USER_INFO_URL).data
    }
}


fun HttpClient.getQRCodeLoginUrlAsync(): Deferred<QRCodeLoginInfo> {
    return async {
        get<QRCodeLoginInfo>(QRCODE_GET_LOGIN_URL)
    }
}

fun HttpClient.getQRCodeLoginInfoAsync(oauthKey: String): Deferred<DataHolder<JsonElement>> {
    return async {
        post<DataHolder<JsonElement>>(QRCODE_GET_LOGIN_INFO) {
            parameter("oauthKey", oauthKey)
            parameter("gourl", "https://passport.bilibili.com/account/security")
        }
    }
}

@KtorExperimentalAPI
suspend fun HttpClient.suspendUtilLoginSuccess(oauthKey: String): UrlData {
    while (true) {
        delay(5000)
        val response = getQRCodeLoginInfoAsync(oauthKey).await()
        if (response.status!!) {
            return gson.fromJson(response.data!!.toString())
        }
    }
}

@KtorExperimentalAPI
suspend fun HttpClient.login(howToShowQRCode: (urlString: String) -> Unit): UserInfo? {
    val info = getQRCodeLoginUrlAsync().await()
    howToShowQRCode(info.data.url)
    suspendUtilLoginSuccess(info.data.oauthKey)
    return getUserInfoAsync().await()
}

fun HttpClient.getUidAsync(): Deferred<Int> {
    return async {
        (cookies(BILIBILI_DOMAIN)["DedeUserID"]
            ?: throw AuthException("can't get uid,check login status")).value.toInt()
    }
}