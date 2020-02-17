package ink.rubi.bilibili.video.data

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