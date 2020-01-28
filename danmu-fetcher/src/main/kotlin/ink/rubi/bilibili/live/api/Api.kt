package ink.rubi.bilibili.live.api

import ink.rubi.bilibili.common.api.BILIBILI_DOMAIN
import ink.rubi.bilibili.live.data.*
import io.ktor.client.HttpClient
import io.ktor.client.features.cookies.cookies
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlin.random.Random
import io.ktor.client.features.cookies.get


const val ROOM_INIT_URL         = "https://api.live.bilibili.com/room/v1/Room/room_init"
const val ROOM_LOAD_BALANCE_URL = "https://api.live.bilibili.com/room/v1/Danmu/getConf"
const val WEB_TITLES            = "https://api.live.bilibili.com/rc/v1/Title/webTitles"
const val SEND_MESSAGE          = "https://api.live.bilibili.com/msg/send"

const val DEFAULT_DANMU_HOST    = "broadcastlv.chat.bilibili.com"

fun HttpClient.getRealRoomIdAsync(roomId: Int): Deferred<Int> {
    return async {
        this@getRealRoomIdAsync.get<NormalResponse<RoomInitInfo>>(
            ROOM_INIT_URL
        ) {
            parameter("id", roomId)
        }.data!!.room_id
    }
}

fun HttpClient.getLoadBalancedWsHostServerAsync(roomId: Int): Deferred<HostServer> {
    return async {
        this@getLoadBalancedWsHostServerAsync.get<NormalResponse<LoadBalanceInfo>>(
            ROOM_LOAD_BALANCE_URL
        ) {
            parameter("room_id", roomId)
            parameter("platform", "pc")
            parameter("player", "web")
        }.data!!.host_server_list[Random.nextInt(0, 3)]
    }
}

fun HttpClient.getWebTitlesAsync(): Deferred<List<WebTitle>> {
    return async {
        this@getWebTitlesAsync.get<NormalResponse<List<WebTitle>>>(
            WEB_TITLES
        ).data!!
    }
}



fun HttpClient.sendNormalMessageAsync(message: String, roomId: Int): Deferred<HttpResponse> {
    return async {
        val csrf = cookies(BILIBILI_DOMAIN)["bili_jct"]!!.value
        this@sendNormalMessageAsync.post<HttpResponse>(SEND_MESSAGE) {
            parameter("color", 16777215)
            parameter("fontsize", 25)
            parameter("mode", 1)
            parameter("msg", message)
            parameter("rnd", 1580210173)
            parameter("roomid", roomId)
            parameter("bubble", 0)
            parameter("csrf_token", csrf)
            parameter("csrf", csrf)
        }
    }
}



data class GuardInfo(
    val guard_level: Int,
    val mock_effect: Int,
    val uid: Int,
    val username: String
)

