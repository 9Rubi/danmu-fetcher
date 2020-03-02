package ink.rubi.bilibili.live.api

import com.google.gson.JsonElement
import ink.rubi.bilibili.common.api.BILIBILI_DOMAIN
import ink.rubi.bilibili.common.data.DataHolder
import ink.rubi.bilibili.live.data.*
import io.ktor.client.HttpClient
import io.ktor.client.features.cookies.cookies
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlin.random.Random
import io.ktor.client.features.cookies.get


const val ROOM_INIT_URL = "https://api.live.bilibili.com/room/v1/Room/room_init"
const val ROOM_LOAD_BALANCE_URL = "https://api.live.bilibili.com/room/v1/Danmu/getConf"
const val WEB_TITLES = "https://api.live.bilibili.com/rc/v1/Title/webTitles"
const val SEND_MESSAGE = "https://api.live.bilibili.com/msg/send"
const val BAG_LIST = "https://api.live.bilibili.com/xlive/web-room/v1/gift/bag_list"
const val DEFAULT_DANMU_HOST = "broadcastlv.chat.bilibili.com"

/**
 * require cookies
 */
fun HttpClient.getBagDataAsync(roomId: Int? = null): Deferred<BagData> {
    return async {
        get<DataHolder<BagData>>(
            BAG_LIST
        ) {
            roomId?.run { parameter("room_id", roomId) }
            parameter("t", System.currentTimeMillis())
        }.data!!
    }
}


fun HttpClient.getRealRoomIdAsync(roomId: Int): Deferred<Int> {
    return async {
        get<DataHolder<RoomInitInfo>>(
            ROOM_INIT_URL
        ) {
            parameter("id", roomId)
        }.data!!.room_id
    }
}

fun HttpClient.getLoadBalancedWsHostServerAsync(roomId: Int): Deferred<HostServer> {
    return async {
        get<DataHolder<LoadBalanceInfo>>(
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
        get<DataHolder<List<WebTitle>>>(
            WEB_TITLES
        ).data!!
    }
}


/**
 * I don't know what's in the `data` yet
 *
 * if success , response payload should be
 *
 * ```
 * {"code":0,"data":[],"message":"","msg":""}
 * ```
 *
 * there is some fail danmu sample
 *
 * ```
 * {"code":-500,"message":"超出限制长度","msg":"超出限制长度"}
 * {"code":0,"message":"内容非法","msg":"内容非法"}
 * {"code":0,"message":"msg in 1s","msg":"msg in 1s"}
 * {"code":0,"message":"msg repeat","msg":"msg repeat"}
 * ```
 *
 *
 * require cookies
 */
fun HttpClient.sendDanmuAsync(message: String, roomId: Int): Deferred<DataHolder<JsonElement>> {
    return async {
        val csrf = cookies(BILIBILI_DOMAIN)["bili_jct"]!!.value
        post<DataHolder<JsonElement>>(SEND_MESSAGE) {
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

fun DataHolder<JsonElement>.isSuccess(): Boolean {
    return this.code == 0 && this.msg == "" && this.message == ""
}


data class GuardInfo(
    val guard_level: Int,
    val mock_effect: Int,
    val uid: Int,
    val username: String
)

