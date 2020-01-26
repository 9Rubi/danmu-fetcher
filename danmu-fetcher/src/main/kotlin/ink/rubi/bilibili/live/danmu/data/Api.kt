package ink.rubi.bilibili.live.danmu.data

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlin.random.Random


const val ROOM_INIT_URL = "https://api.live.bilibili.com/room/v1/Room/room_init"
const val ROOM_LOAD_BALANCE_URL = "https://api.live.bilibili.com/room/v1/Danmu/getConf"
const val WEB_TITLES = "https://api.live.bilibili.com/rc/v1/Title/webTitles"
const val DEFAULT_DANMU_HOST = "broadcastlv.chat.bilibili.com"
fun HttpClient.getRealRoomIdAsync(roomId: Int): Deferred<Int> {
    return async {
        this@getRealRoomIdAsync.get<NormalResponse<RoomInitInfo>>(ROOM_INIT_URL) {
            parameter("id", roomId)
        }.data.room_id
    }
}

fun HttpClient.getLoadBalancedWsHostServerAsync(roomId: Int): Deferred<HostServer> {
    return async {
        this@getLoadBalancedWsHostServerAsync.get<NormalResponse<LoadBalanceInfo>>(ROOM_LOAD_BALANCE_URL) {
            parameter("room_id", roomId)
            parameter("platform", "pc")
            parameter("player", "web")
        }.data.host_server_list[Random.nextInt(0, 3)]
    }
}

fun HttpClient.getWebTitlesAsync(): Deferred<List<WebTitle>> {
    return async {
        this@getWebTitlesAsync.get<NormalResponse<List<WebTitle>>>(WEB_TITLES).data
    }
}

data class GuardInfo(
    val guard_level: Int,
    val mock_effect: Int,
    val uid: Int,
    val username: String
)
