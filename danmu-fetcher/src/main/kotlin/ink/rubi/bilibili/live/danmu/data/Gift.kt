package ink.rubi.bilibili.live.danmu.data

data class Gift(
    val action: String,
    val addFollow: Int,
    val batch_combo_id: String,
    val batch_combo_send: BatchComboSend?,
    val beatId: String,
    val biz_source: String,
    val broadcast_id: Int,
    val capsule: Any?,
    val coin_type: String,
    val combo_send: ComboSend?,
    val crit_prob: Int,
    val draw: Int,
    val effect: Int,
    val effect_block: Int,
    val eventNum: Int,
    val eventScore: Int,
    /**
     * 用户头像图片的url
     */
    val face: String,
    val giftId: Int,
    /**
     * 礼物名称
     */
    val giftName: String,
    val giftType: Int,
    val gold: Int,
    val guard_level: Int,
    val is_first: Boolean,
    val medal: List<Any>,
    val metadata: String,
    val newMedal: Int,
    val newTitle: Int,
    val notice_msg: List<Any>,
    /**
     * 数量
     */
    val num: Int,
    val price: Int,
    val rcost: Int,
    val remain: Int,
    val rnd: String,
    val send_master: Any?,
    val silver: Int,
    val smallTVCountFlag: Boolean,
    val smalltv_msg: List<Any>,
    val specialGift: Any?,
    val `super`: Int,
    val super_batch_gift_num: Int,
    val super_gift_num: Int,
    val tag_image: String,
    val timestamp: Int,
    val title: String,
    val top_list: List<Any>,
    val total_coin: Int,

    val uid: Int,
    /**
     * 用户名
     */
    val uname: String
)

data class BatchComboSend(
    val action: String,
    val batch_combo_id: String,
    val batch_combo_num: Int,
    val gift_id: Int,
    val gift_name: String,
    val gift_num: Int,
    val send_master: Any?,
    val uid: Int,
    val uname: String
)

data class ComboSend(
    val action: String,
    val combo_id: String,
    val combo_num: Int,
    val gift_id: Int,
    val gift_name: String,
    val gift_num: Int,
    val send_master: Any?,
    val uid: Int,
    val uname: String
)