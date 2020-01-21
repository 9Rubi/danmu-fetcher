package ink.rubi.bilibili.live.danmu.handler

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.module.kotlin.readValue
import ink.rubi.bilibili.live.danmu.constant.CMD
import ink.rubi.bilibili.live.danmu.data.*
import ink.rubi.bilibili.live.danmu.objectMapper

/*

开播提醒 两次？
2020-01-21 10:42:38.285 [ktor-cio-thread-3] ERROR [test] - {"cmd":"LIVE","roomid":958282}
2020-01-21 10:42:38.303 [ktor-cio-thread-3] WARN  [test] - {"cmd":"LIVE","roomid":958282}
广告
2020-01-21 10:43:23.833 [ktor-cio-thread-0] ERROR [test] - {"cmd":"NOTICE_MSG","full":{"head_icon":"http:\/\/i0.hdslb.com\/bfs\/live\/b29add66421580c3e680d784a827202e512a40a0.webp","tail_icon":"http:\/\/i0.hdslb.com\/bfs\/live\/822da481fdaba986d738db5d8fd469ffa95a8fa1.webp","head_icon_fa":"http:\/\/i0.hdslb.com\/bfs\/live\/49869a52d6225a3e70bbf1f4da63f199a95384b2.png","tail_icon_fa":"http:\/\/i0.hdslb.com\/bfs\/live\/38cb2a9f1209b16c0f15162b0b553e3b28d9f16f.png","head_icon_fan":24,"tail_icon_fan":4,"background":"#66A74EFF","color":"#FFFFFFFF","highlight":"#FDFF2FFF","time":20},"half":{"head_icon":"http:\/\/i0.hdslb.com\/bfs\/live\/ec9b374caec5bd84898f3780a10189be96b86d4e.png","tail_icon":"","background":"#85B971FF","color":"#FFFFFFFF","highlight":"#FDFF2FFF","time":15},"side":{"head_icon":"http:\/\/i0.hdslb.com\/bfs\/live\/e41c7e12b1e08724d2ab2f369515132d30fe1ef7.png","background":"#F4FDE8FF","color":"#79B48EFF","highlight":"#388726FF","border":"#A9DA9FFF"},"roomid":21464873,"real_roomid":21464873,"msg_common":"<%Gerrry11%>\u6295\u5582<%\u827e\u9171biu%>1\u4e2a\u5c0f\u7535\u89c6\u98de\u8239\uff0c\u70b9\u51fb\u524d\u5f80TA\u7684\u623f\u95f4\u53bb\u62bd\u5956\u5427","msg_self":"<%Gerrry11%>\u6295\u5582<%\u827e\u9171biu%>1\u4e2a\u5c0f\u7535\u89c6\u98de\u8239\uff0c\u5feb\u6765\u62bd\u5956\u5427","link_url":"http:\/\/live.bilibili.com\/21464873?live_lottery_type=1&broadcast_type=0","msg_type":2,"shield_uid":-1,"business_id":"25"}

2020-01-21 10:44:34.626 [ktor-cio-thread-1] ERROR [test] - {"cmd":"DANMU_MSG","info":[[0,1,25,16777215,1579574673806,1579574432,0,"50c4a8ef",0,0,0],"1",[3220953,"紫渣带刃鲁比",0,0,0,10000,1,""],[],[13,0,6406234,"\u003e50000"],["",""],0,0,null,{"ts":1579574673,"ct":"77E985CB"},0,0,null,null,0]}

 */

interface TypedMessageHandler : MessageHandler {
    fun onReceiveDanmu(block: (user: User, badge: Badge?, userLevel: UserLevel, said: String) -> Unit)
    fun onReceiveGift(block: (gift: Gift) -> Unit)
    fun onSomeOneEnterInLiveRoom(block: (user: String) -> Unit)
    fun onGuardEnterInLiveRoom(block: (user: String) -> Unit)
    fun onAllTypeMessage(block: (message: String) -> Unit)
    fun onUnknownTypeMessage(block: (message: String, cmd: String) -> Unit)
    fun onError(block: (message: String, e: Throwable) -> Unit)
    fun onLive(block: (roomId: Int) -> Unit)
    fun onPrepare(block: (roomId: Int) -> Unit)
    fun onRoomRankChange(block: (rank: RoomRank) -> Unit)
}

class TypedMessageHandlerImpl(
    private var receiveDanmu: ((user: User, badge: Badge?, userLevel: UserLevel, said: String) -> Unit)? = null,
    private var receiveGift: ((gift: Gift) -> Unit)? = null,
    private var someOneEnterInLiveRoom: ((user: String) -> Unit)? = null,
    private var guardEnterInLiveRoom: ((user: String) -> Unit)? = null,
    private var allTypeMessage: ((message: String) -> Unit)? = null,
    private var unknownTypeMessage: ((message: String, cmd: String) -> Unit)? = null,
    private var error: ((message: String, e: Throwable) -> Unit)? = null,
    private var live: ((roomId: Int) -> Unit)? = null,
    private var prepare: ((roomId: Int) -> Unit)? = null,
    private var roomRankChange: ((rank: RoomRank) -> Unit)? = null
) : TypedMessageHandler {

    override fun onReceiveDanmu(block: (user: User, badge: Badge?, userLevel: UserLevel, said: String) -> Unit) {
        receiveDanmu = block
    }

    override fun onReceiveGift(block: (gift: Gift) -> Unit) {
        receiveGift = block
    }

    override fun onSomeOneEnterInLiveRoom(block: (user: String) -> Unit) {
        someOneEnterInLiveRoom = block
    }

    override fun onGuardEnterInLiveRoom(block: (user: String) -> Unit) {
        guardEnterInLiveRoom = block
    }

    override fun onAllTypeMessage(block: (message: String) -> Unit) {
        allTypeMessage = block
    }

    override fun onUnknownTypeMessage(block: ((message: String, cmd: String) -> Unit)) {
        unknownTypeMessage = block
    }

    override fun onError(block: (message: String, e: Throwable) -> Unit) {
        error = block
    }

    override fun onLive(block: (roomId: Int) -> Unit) {
        live = block
    }

    override fun onPrepare(block: (roomId: Int) -> Unit) {
        prepare = block
    }

    override fun onRoomRankChange(block: (rank: RoomRank) -> Unit) {
        roomRankChange = block
    }

    override fun handle(message: String) {
        try {
            allTypeMessage?.invoke(message)
            val json = objectMapper.readTree(message)
            val cmd = json["cmd"]?.textValue() ?: throw Exception("unexpect json format, missing [cmd] !")
            when (cmd) {
                CMD.DANMU_MSG.name -> {
                    val info = json["info"]
                    val said = info[1].textValue()!!
                    val user = with(info[2]) {
                        User(this[0].asInt(), this[1].asText())
                    }
                    val badge = with(info[3]) {
                        if (this.isArray && (this as ArrayNode).size() == 0)
                            null
                        else
                            Badge(
                                this[0].asInt(0),
                                this[1].asText(null),
                                this[2].asText(null),
                                this[3].asInt(0)
                            )
                    }
                    val userLevel = with(info[4]) {
                        UserLevel(
                            this[0].asInt(),
                            this[2].asInt(),
                            this[3].asText()
                        )
                    }
                    receiveDanmu?.invoke(user, badge, userLevel, said)
                }
                CMD.SEND_GIFT.name -> {
                    val gift = objectMapper.readValue<Gift>(json["data"].toString())
                    receiveGift?.invoke(gift)
                }
                CMD.WELCOME.name -> {
                    val user = json["data"]["uname"].textValue()!!
                    someOneEnterInLiveRoom?.invoke(user)
                }
                CMD.WELCOME_GUARD.name -> {
                    val user = json["data"]["username"].textValue()!!
                    guardEnterInLiveRoom?.invoke(user)
                }
                CMD.LIVE.name -> {
                    val roomId = json["roomid"].asInt()
                    live?.invoke(roomId)
                }
                CMD.PREPARING.name -> {
                    val roomId = json["roomid"].asInt()
                    prepare?.invoke(roomId)
                }
                CMD.ROOM_RANK.name -> {
                    val rank = objectMapper.readValue<RoomRank>(json["data"].toString())
                    roomRankChange?.invoke(rank)
                }
                else -> {
                    unknownTypeMessage?.invoke(message, cmd)
                }
            }
        } catch (e: Throwable) {
            error?.invoke(message, e)
        }
    }
}


fun typedMessageHandler(content: TypedMessageHandler.() -> Unit) = TypedMessageHandlerImpl().apply(content)
