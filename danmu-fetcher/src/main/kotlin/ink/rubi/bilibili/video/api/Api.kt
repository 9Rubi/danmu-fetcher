package ink.rubi.bilibili.video.api

import ink.rubi.bilibili.common.data.DataHolder
import ink.rubi.bilibili.video.data.LiveStatus
import ink.rubi.bilibili.video.data.ReplyData
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



/*

{[捂脸]=
{id=15, package_id=1, state=0, type=1, attr=0, text=[捂脸], url=http://i0.hdslb.com/bfs/emote/6921bb43f0c634870b92f4a8ad41dada94a5296d.png, meta={size=1, label_text=, label_url=, label_color=}, mtime=1577702898}
}
, members=[], message=A队粉已经被A队冠军粉恶心了两天了[捂脸], plat=2), count=0, ctime=1581219674, dialog=2349438327, fansgrade=0, folder=Folder(has_folded=false, is_folded=false, rule=), like=21, member=Member(DisplayRank=0, avatar=http://i2.hdslb.com/bfs/face/382c06e73000e36ca75e342de9d67ca65d41dd86.jpg, fans_detail=null, following=0, level_info=LevelInfo(current_exp=0, current_level=5, current_min=0, next_exp=0), mid=31258182, nameplate=Nameplate(condition=同时拥有粉丝勋章>=15个, image=http://i2.hdslb.com/bfs/face/3f5539e1486303422ffc8595862ccb6606e0b745.png, image_small=http://i2.hdslb.com/bfs/face/cf85e7908095d256e595ec9759f4e7795f23bc22.png, level=普通勋章, name=收集达人, nid=58), official_verify=OfficialVerify(desc=, type=-1), pendant=Pendant(expire=0, image=http://i0.hdslb.com/bfs/face/c27c201231880c021ba1e9dac5048bca54e606f9.png, name=12月打卡, pid=1444), rank=10000, sex=男, sign=以前喜欢看的东西都是会反复去看的，现在不了, uname=虾籽籽籽仔, user_sailing=UserSailing(cardbg=null, cardbg_with_focus=null, pendant={id=1444, name=12月打卡, image=http://i0.hdslb.com/bfs/face/c27c201231880c021ba1e9dac5048bca54e606f9.png, jump_url=, type=pay}), vip=Vip(accessStatus=0, dueRemark=, label=Label(path=), themeType=0, vipDueDate=1634572800000, vipStatus=1, vipStatusWarn=, vipType=2)), mid=31258182, oid=87679483, parent=2349151020, parent_str=2349151020, rcount=0, replies=null, root=2349151020, root_str=2349151020, rpid=2349438327, rpid_str=2349438327, show_follow=false, state=0, type=1, up_action=UpAction(like=false, reply=false)), Reply(action=0, assist=0, attr=0,


 */