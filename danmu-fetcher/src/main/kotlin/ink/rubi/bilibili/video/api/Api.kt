package ink.rubi.bilibili.video.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import ink.rubi.bilibili.common.data.DataHolder
import ink.rubi.bilibili.video.data.LiveStatus
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

const val LIVE_STATUS = "https://api.live.bilibili.com/room/v1/Room/getLiveStatus"
const val REPLY = "https://api.bilibili.com/x/v2/reply"


fun HttpClient.getLiveStatusAsync(uid: Int): Deferred<LiveStatus> {
    return async {
        get<DataHolder<LiveStatus>>(LIVE_STATUS) {
            parameter("uid", uid)
            parameter("mid", uid)
            // jsonp
        }.data!!
    }
}


fun HttpClient.getReplyFromVideoAsync(videoId: Int): Deferred<ReplyData> {
    return async {
        get<DataHolder<ReplyData>>(REPLY) {
            //            callback,jsonp,
            parameter("oid", videoId)
            parameter("type", 1)
            parameter("sort", 2)
            parameter("pn", 1)
            parameter("_", System.currentTimeMillis())
        }.data!!
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class ReplyData(
    val assist: Int,
    val blacklist: Int,
    val config: Config,
    val control: Control,
    val folder: Folder,
    val hots: List<Hot>,
    val lottery_card: Any?,
    val mode: Int,
    val notice: Any?,
    val page: Page,
    val replies: List<Reply>,
    val show_bvid: Boolean,
    val support_mode: List<Int>,
    val top: Any?,
    val upper: Upper,
    val vote: Int
)

data class Page(
    val acount: Int,
    val count: Int,
    val num: Int,
    val size: Int
)

data class Config(
    val read_only: Boolean,
    val show_del_log: Boolean,
    val show_up_flag: Boolean,
    val showadmin: Int,
    val showentry: Int,
    val showfloor: Int,
    val showtopic: Int
)

data class Control(
    val answer_guide_android_url: String,
    val answer_guide_icon_url: String,
    val answer_guide_ios_url: String,
    val answer_guide_text: String,
    val bg_text: String,
    val child_input_text: String,
    val input_disable: Boolean,
    val root_input_text: String,
    val web_selection: Boolean
)

data class Folder(
    val has_folded: Boolean,
    val is_folded: Boolean,
    val rule: String
)

data class Hot(
    val action: Int,
    val assist: Int,
    val attr: Int,
    val content: Content,
    val count: Int,
    val ctime: Int,
    val dialog: Int,
    val fansgrade: Int,
    val folder: Folder,
    val like: Int,
    val member: Member,
    val mid: Int,
    val oid: Int,
    val parent: Int,
    val parent_str: String,
    val rcount: Int,
    val replies: List<Reply>,
    val root: Int,
    val root_str: String,
    val rpid: Long,
    val rpid_str: String,
    val show_follow: Boolean,
    val state: Int,
    val type: Int,
    val up_action: UpAction
)

data class Reply(
    val action: Int,
    val assist: Int,
    val attr: Int,
    val content: Content,
    val count: Int,
    val ctime: Int,
    val dialog: Long,
    val fansgrade: Int,
    val folder: Folder,
    val like: Int,
    val member: Member,
    val mid: Int,
    val oid: Int,
    val parent: Long,
    val parent_str: String,
    val rcount: Int,
    val replies: Any?,
    val root: Long,
    val root_str: String,
    val rpid: Long,
    val rpid_str: String,
    val show_follow: Boolean,
    val state: Int,
    val type: Int,
    val up_action: UpAction
)

data class Member(
    val DisplayRank: String,
    val avatar: String,
    val fans_detail: Any?,
    val following: Int,
    val level_info: LevelInfo,
    val mid: String,
    val nameplate: Nameplate,
    val official_verify: OfficialVerify,
    val pendant: Pendant,
    val rank: String,
    val sex: String,
    val sign: String,
    val uname: String,
    val user_sailing: UserSailing?,
    val vip: Vip
)

data class LevelInfo(
    val current_exp: Int,
    val current_level: Int,
    val current_min: Int,
    val next_exp: Int
)

data class Nameplate(
    val condition: String,
    val image: String,
    val image_small: String,
    val level: String,
    val name: String,
    val nid: Int
)

data class OfficialVerify(
    val desc: String,
    val type: Int
)

data class Pendant(
    val id: Int,
    val expire: Int,
    val image: String,
    val name: String,
    val pid: Int,
    val jump_url: String?,
    val type: String?
)

data class UserSailing(
    val cardbg: Any?,
    val cardbg_with_focus: Any?,
    val pendant: Pendant?
)

data class Vip(
    val accessStatus: Int,
    val dueRemark: String,
    val label: Label,
    val themeType: Int,
    val vipDueDate: Long,
    val vipStatus: Int,
    val vipStatusWarn: String,
    val vipType: Int
)

data class Label(
    val path: String
)


data class UpAction(
    val like: Boolean,
    val reply: Boolean
)

data class Content(
    val device: String,
    val emote: Map<String, Emote>?,
    val members: List<Member>,
    val message: String,
    val plat: Int
)

data class Emote(
    val attr: Int,
    val id: Int,
    val meta: Meta,
    val mtime: Int,
    val package_id: Int,
    val state: Int,
    val text: String,
    val type: Int,
    val url: String
)

data class Meta(
    val label_color: String,
    val label_text: String,
    val label_url: String,
    val size: Int
)

data class Upper(
    val mid: Int,
    val top: Any?,
    val vote: Any?
)

/*

{[捂脸]=
{id=15, package_id=1, state=0, type=1, attr=0, text=[捂脸], url=http://i0.hdslb.com/bfs/emote/6921bb43f0c634870b92f4a8ad41dada94a5296d.png, meta={size=1, label_text=, label_url=, label_color=}, mtime=1577702898}
}
, members=[], message=A队粉已经被A队冠军粉恶心了两天了[捂脸], plat=2), count=0, ctime=1581219674, dialog=2349438327, fansgrade=0, folder=Folder(has_folded=false, is_folded=false, rule=), like=21, member=Member(DisplayRank=0, avatar=http://i2.hdslb.com/bfs/face/382c06e73000e36ca75e342de9d67ca65d41dd86.jpg, fans_detail=null, following=0, level_info=LevelInfo(current_exp=0, current_level=5, current_min=0, next_exp=0), mid=31258182, nameplate=Nameplate(condition=同时拥有粉丝勋章>=15个, image=http://i2.hdslb.com/bfs/face/3f5539e1486303422ffc8595862ccb6606e0b745.png, image_small=http://i2.hdslb.com/bfs/face/cf85e7908095d256e595ec9759f4e7795f23bc22.png, level=普通勋章, name=收集达人, nid=58), official_verify=OfficialVerify(desc=, type=-1), pendant=Pendant(expire=0, image=http://i0.hdslb.com/bfs/face/c27c201231880c021ba1e9dac5048bca54e606f9.png, name=12月打卡, pid=1444), rank=10000, sex=男, sign=以前喜欢看的东西都是会反复去看的，现在不了, uname=虾籽籽籽仔, user_sailing=UserSailing(cardbg=null, cardbg_with_focus=null, pendant={id=1444, name=12月打卡, image=http://i0.hdslb.com/bfs/face/c27c201231880c021ba1e9dac5048bca54e606f9.png, jump_url=, type=pay}), vip=Vip(accessStatus=0, dueRemark=, label=Label(path=), themeType=0, vipDueDate=1634572800000, vipStatus=1, vipStatusWarn=, vipType=2)), mid=31258182, oid=87679483, parent=2349151020, parent_str=2349151020, rcount=0, replies=null, root=2349151020, root_str=2349151020, rpid=2349438327, rpid_str=2349438327, show_follow=false, state=0, type=1, up_action=UpAction(like=false, reply=false)), Reply(action=0, assist=0, attr=0,


 */