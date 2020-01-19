# kotlin 实现的弹幕姬

## 使用示例
```xml
<project>
    <repositories>
        <repository>
            <id>bintray-9rubi-for-fun</id>
            <name>bintray</name>
            <url>https://dl.bintray.com/9rubi/for-fun</url>
        </repository>
    </repositories>
    
    <dependencies>
        <dependency>
          <groupId>ink.rubi</groupId>
          <artifactId>danmu-fetcher</artifactId>
          <version>0.0.11</version>
        </dependency>
<!--         日志实现 -->
    </dependencies>
</project>
```
```groovy
repositories {
    maven {
        url  "https://dl.bintray.com/9rubi/danmu-fetcher"
    }
}

dependencies{
    compile 'ink.rubi:danmu-fetcher:0.0.11'
    compile 'ch.qos.logback:logback-classic:1.2.1'
}
```



## kotlin
```kotlin
val log: Logger = LoggerFactory.getLogger("[test]")
val objectMapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())

object Main {
    @KtorExperimentalAPI
    @FlowPreview
    @ExperimentalCoroutinesApi
    @JvmStatic
    fun main(args: Array<String>) {
        println("输入 直播间号码:")
        val roomId = readLine()!!.toInt()
        DanmuListener.doFetchDanmu(roomId) { cmd: String, rawJson: String ->
            val json = objectMapper.readTree(rawJson)
            when (cmd) {
                CMD.DANMU_MSG.name -> {
                    val said = json["info"][1].textValue()!!
                    val who = json["info"][2][1].textValue()!!
                    log.info("[$who] : $said")
                }
                CMD.SEND_GIFT.name -> {
                    val who = unescapeUnicode(json["data"]["uname"].textValue())!!
                    val num = json["data"]["num"].intValue()
                    val gift = unescapeUnicode(json["data"]["giftName"].textValue())!!
                    log.info("[$who] 送出了 $num 个 [$gift]")
                }
                CMD.WELCOME.name -> {
                    val who = json["data"]["uname"].textValue()!!
                    log.info("[$who] 进入了直播间")
                }
                CMD.WELCOME_GUARD.name -> {
                    val who = json["data"]["username"].textValue()!!
                    log.info("[舰长][$who] 进入了直播间")
                }
                else -> {
                    log.warn(rawJson)
                }
            }
        }
    }
}
```

## use java
```java
public class Test {
    public static void main(String[] args) {
        int roomId = Integer.parseInt(new Scanner(System.in).nextLine());
        DanmuListener.doFetchDanmu(roomId,(cmd, json) -> {
            System.out.println(json);
            //就不写两遍了
            return Unit.INSTANCE;
        });

    }
}
```

## 效果如下
```log
Connected to the target VM, address: '127.0.0.1:0', transport: 'socket'
输入 直播间号码:
2233
2020-01-18 16:10:24.478 [main] INFO  [danmu-client] - room id => 544641
2020-01-18 16:10:24.511 [DefaultDispatcher-worker-2] WARN  io.ktor.util.random - NativePRNGNonBlocking is not found, fallback to SHA1PRNG
2020-01-18 16:10:24.697 [main] INFO  [danmu-client] - login ....
2020-01-18 16:10:24.773 [ktor-cio-thread-3] INFO  [danmu-client] - response => {"code":0}
2020-01-18 16:10:24.773 [ktor-cio-thread-3] INFO  [danmu-client] - heart beat packet
2020-01-18 16:10:24.924 [ktor-cio-thread-1] INFO  [test] - [王牌复读机] : 那先生
2020-01-18 16:10:24.924 [ktor-cio-thread-1] INFO  [test] - [time蟹黄堡] : 那岩
2020-01-18 16:10:24.924 [ktor-cio-thread-1] INFO  [test] - [Shadow-娇妹] 进入了直播间
2020-01-18 16:10:24.926 [ktor-cio-thread-1] INFO  [test] - [没有脑袋的大鸽] : 出的问题
2020-01-18 16:10:24.926 [ktor-cio-thread-1] INFO  [test] - [钮钴禄中奖] : 哈哈哈哈哈
2020-01-18 16:10:24.927 [ktor-cio-thread-1] INFO  [test] - [无名的飞鱼] : 钠盐
2020-01-18 16:10:24.928 [ktor-cio-thread-1] INFO  [test] - [别闹-别闹] 送出了 55 个 [辣条]
2020-01-18 16:10:24.929 [ktor-cio-thread-1] INFO  [test] - [大杰霸] 送出了 30 个 [辣条]
2020-01-18 16:10:24.930 [ktor-cio-thread-1] INFO  [test] - [是聚米吗] : 大虾过了吗
2020-01-18 16:10:24.930 [ktor-cio-thread-1] INFO  [test] - [paopaoyong898] : 那岩
2020-01-18 16:10:24.931 [ktor-cio-thread-1] INFO  [test] - [过期酸奶拉] :  哔哩哔哩 (゜-゜)つロ 干杯~
2020-01-18 16:10:24.931 [ktor-cio-thread-1] INFO  [test] - [是只猪o] : ！
2020-01-18 16:10:24.932 [ktor-cio-thread-1] INFO  [test] - [野原一灰] : 某幻睡了吗
2020-01-18 16:10:24.932 [ktor-cio-thread-1] INFO  [test] - [国家一级混吃等死废物] : 不知所措
2020-01-18 16:10:26.082 [ktor-cio-thread-0] INFO  [test] - [JH有人叫我小星星] : 那岩来啦
2020-01-18 16:10:26.083 [ktor-cio-thread-0] INFO  [test] - [LcPanthVeil] 送出了 25 个 [辣条]
2020-01-18 16:10:26.083 [ktor-cio-thread-0] INFO  [test] - [苏打气泡猪猪侠] : 有柴西西吗兄弟们
2020-01-18 16:10:26.083 [ktor-cio-thread-0] INFO  [test] - [TSStudio] : 圆飞
2020-01-18 16:10:26.084 [ktor-cio-thread-0] INFO  [test] - [Mitel_云飞定墨] : 罗汉过了吗
2020-01-18 16:10:26.084 [ktor-cio-thread-0] INFO  [test] - [雾黑cyan] : 那岩
2020-01-18 16:10:26.084 [ktor-cio-thread-0] INFO  [test] - [早起的虫虫被鸟吃] : 那岩
2020-01-18 16:10:26.085 [ktor-cio-thread-0] INFO  [test] - [许松泠] : 朱一旦过了吗
2020-01-18 16:10:26.085 [ktor-cio-thread-0] INFO  [test] - [多玖e] : 那岩姐
2020-01-18 16:10:26.085 [ktor-cio-thread-0] INFO  [test] - [Changer丶L] : 那先生
2020-01-18 16:10:26.085 [ktor-cio-thread-0] INFO  [test] - [红烧空调] : 罚站环节
2020-01-18 16:10:26.086 [ktor-cio-thread-0] INFO  [test] - [呻吟着潜水] 送出了 2 个 [辣条]
2020-01-18 16:10:26.086 [ktor-cio-thread-0] INFO  [test] - [一邪琊一] : 祥哥，打赏略
2020-01-18 16:10:26.610 [ktor-cio-thread-0] WARN  [test] - {"cmd":"NOTICE_MSG","full":{"head_icon":"http:\/\/i0.hdslb.com\/bfs\/live\/b29add66421580c3e680d784a827202e512a40a0.webp","tail_icon":"http:\/\/i0.hdslb.com\/bfs\/live\/822da481fdaba986d738db5d8fd469ffa95a8fa1.webp","head_icon_fa":"http:\/\/i0.hdslb.com\/bfs\/live\/49869a52d6225a3e70bbf1f4da63f199a95384b2.png","tail_icon_fa":"http:\/\/i0.hdslb.com\/bfs\/live\/38cb2a9f1209b16c0f15162b0b553e3b28d9f16f.png","head_icon_fan":24,"tail_icon_fan":4,"background":"#66A74EFF","color":"#FFFFFFFF","highlight":"#FDFF2FFF","time":20},"half":{"head_icon":"http:\/\/i0.hdslb.com\/bfs\/live\/ec9b374caec5bd84898f3780a10189be96b86d4e.png","tail_icon":"","background":"#85B971FF","color":"#FFFFFFFF","highlight":"#FDFF2FFF","time":15},"side":{"head_icon":"http:\/\/i0.hdslb.com\/bfs\/live\/e41c7e12b1e08724d2ab2f369515132d30fe1ef7.png","background":"#F4FDE8FF","color":"#79B48EFF","highlight":"#388726FF","border":"#A9DA9FFF"},"roomid":21742114,"real_roomid":21742114,"msg_common":"<%v2207S0vC%>\u6295\u5582<%\u6843\u6843\u997c\u5e72\u5440%>1\u4e2a\u5c0f\u7535\u89c6\u98de\u8239\uff0c\u70b9\u51fb\u524d\u5f80TA\u7684\u623f\u95f4\u53bb\u62bd\u5956\u5427","msg_self":"<%v2207S0vC%>\u6295\u5582<%\u6843\u6843\u997c\u5e72\u5440%>1\u4e2a\u5c0f\u7535\u89c6\u98de\u8239\uff0c\u5feb\u6765\u62bd\u5956\u5427","link_url":"http:\/\/live.bilibili.com\/21742114?live_lottery_type=1&broadcast_type=0","msg_type":2,"shield_uid":-1,"business_id":"25"}
2020-01-18 16:10:27.163 [ktor-cio-thread-0] INFO  [test] - [浮空白] : 钠盐!!!
2020-01-18 16:10:27.163 [ktor-cio-thread-0] INFO  [test] - [keith-h] : 那言
2020-01-18 16:10:27.163 [ktor-cio-thread-0] INFO  [test] - [CAR-lU-LIER] : 那老师
2020-01-18 16:10:27.164 [ktor-cio-thread-0] INFO  [test] - [伪娘保护之家] 送出了 1 个 [辣条]
2020-01-18 16:10:27.164 [ktor-cio-thread-0] INFO  [test] - [伪娘保护之家] 送出了 1 个 [辣条]
2020-01-18 16:10:27.164 [ktor-cio-thread-0] INFO  [test] - [晴天大熊猫1122] : 94
2020-01-18 16:10:27.165 [ktor-cio-thread-0] INFO  [test] - [伪娘保护之家] 送出了 1 个 [辣条]
2020-01-18 16:10:27.165 [ktor-cio-thread-0] INFO  [test] - [阿文是阿文不是阿文] : yygq都没过呢
2020-01-18 16:10:27.165 [ktor-cio-thread-0] INFO  [test] - [爱FayeWong1314] : 那岩！
2020-01-18 16:10:27.165 [ktor-cio-thread-0] INFO  [test] - [大概叫做喻星吧] : 那岩
2020-01-18 16:10:28.186 [ktor-cio-thread-2] INFO  [test] - [狂火DAZE] : 啊哈哈
2020-01-18 16:10:28.187 [ktor-cio-thread-2] INFO  [test] - [散华禮姬] : 那岩！
2020-01-18 16:10:28.187 [ktor-cio-thread-2] INFO  [test] - [张思勰] 送出了 1 个 [辣条]
2020-01-18 16:10:28.187 [ktor-cio-thread-2] INFO  [test] - [秋零汐] : 少爷没过
2020-01-18 16:10:28.187 [ktor-cio-thread-2] INFO  [test] - [郝连啥时候能睡觉] : yygq过了吗
2020-01-18 16:10:28.188 [ktor-cio-thread-2] INFO  [test] - [Yami是天使] : 那岩！
2020-01-18 16:10:28.188 [ktor-cio-thread-2] INFO  [test] - [小辣鸡__] : 钠盐
2020-01-18 16:10:29.192 [ktor-cio-thread-1] INFO  [test] - [暗夜7T] : 太阳骑士过来没
2020-01-18 16:10:29.192 [ktor-cio-thread-1] INFO  [test] - [魑魅魍魉HT] : lex要来了
2020-01-18 16:10:29.192 [ktor-cio-thread-1] INFO  [test] - [别闹-别闹] 送出了 31 个 [辣条]
2020-01-18 16:10:29.193 [ktor-cio-thread-1] INFO  [test] - [一枚沙雕鸭] : 山山有没有过鸭
2020-01-18 16:10:29.193 [ktor-cio-thread-1] INFO  [test] - [微风拂过行道树] : 娜妍！！！
2020-01-18 16:10:29.193 [ktor-cio-thread-1] INFO  [test] - [朝贺谢俞] 送出了 4 个 [辣条]
2020-01-18 16:10:29.193 [ktor-cio-thread-1] INFO  [test] - [聒噪不可爱] : 。。。。
2020-01-18 16:10:29.193 [ktor-cio-thread-1] INFO  [test] - [刷时间先没个妈] : 那岩c我
2020-01-18 16:10:29.194 [ktor-cio-thread-1] INFO  [test] - [问君知否丿] : 钠盐
2020-01-18 16:10:29.195 [ktor-cio-thread-1] INFO  [test] - [北柠港] : 北子哥过了吗
2020-01-18 16:10:29.195 [ktor-cio-thread-1] INFO  [test] - [蜡笔晓莘] : 老e过了吗
2020-01-18 16:10:29.195 [ktor-cio-thread-1] INFO  [test] - [一边凉快的妥鸟] : 那先生
2020-01-18 16:10:29.195 [ktor-cio-thread-1] INFO  [test] - [吴大饼的日记] : 蜡笔小新过了
2020-01-18 16:10:29.195 [ktor-cio-thread-1] INFO  [test] - [小狼崽么么] : 少爷过了没
2020-01-18 16:10:29.195 [ktor-cio-thread-1] INFO  [test] - [阿文真的很宅] : 那岩
2020-01-18 16:10:30.214 [ktor-cio-thread-1] INFO  [test] - [流星在早晨] 送出了 25 个 [辣条]
2020-01-18 16:10:30.214 [ktor-cio-thread-1] INFO  [test] - [真的皮faded] : fa
2020-01-18 16:10:30.214 [ktor-cio-thread-1] INFO  [test] - [笑玥の烁] : 大祥哥来了吗
2020-01-18 16:10:30.214 [ktor-cio-thread-1] INFO  [test] - [花少蕊r] : 咋了咋了
2020-01-18 16:10:30.215 [ktor-cio-thread-1] INFO  [test] - [三笠Ackermanア] : 钠盐
2020-01-18 16:10:30.215 [ktor-cio-thread-1] INFO  [test] - [北子哥眼熟我了就改名] : 蕾丝还没过噢
2020-01-18 16:10:30.215 [ktor-cio-thread-1] INFO  [test] - [白er纸] : 那先生
2020-01-18 16:10:30.215 [ktor-cio-thread-1] INFO  [test] - [甜甜甜甜豆吖-] : ……
2020-01-18 16:10:30.215 [ktor-cio-thread-1] INFO  [test] - [牧羊犬airsly] : 那岩！！
2020-01-18 16:10:30.215 [ktor-cio-thread-1] INFO  [test] - [爱喝牛奶的刘姥姥] : 那岩
2020-01-18 16:10:30.215 [ktor-cio-thread-1] INFO  [test] - [纭竹゛] : 罚站
2020-01-18 16:10:30.216 [ktor-cio-thread-1] INFO  [test] - [Five有糖口嚼糖] : 老张过了吗
2020-01-18 16:10:30.216 [ktor-cio-thread-1] INFO  [test] - [猫蛋喵mua] : 北北
2020-01-18 16:10:30.216 [ktor-cio-thread-1] INFO  [test] - [zangaiWang] : 发生什么了...
2020-01-18 16:10:30.216 [ktor-cio-thread-1] INFO  [test] - [FriuDont] : hhhhhhhhhhhh
2020-01-18 16:10:31.256 [ktor-cio-thread-3] INFO  [test] - [Mis李丶] : 噶住啦
2020-01-18 16:10:31.256 [ktor-cio-thread-3] INFO  [test] - [大魔王丶小鳄鱼] : 那岩！
2020-01-18 16:10:31.256 [ktor-cio-thread-3] INFO  [test] - [魔缘之翼] 送出了 1 个 [辣条]
2020-01-18 16:10:31.256 [ktor-cio-thread-3] INFO  [test] - [无用之蠛] : 哈哈哈哈哈
2020-01-18 16:10:31.256 [ktor-cio-thread-3] INFO  [test] - [dealdean] : 那言！！！！
2020-01-18 16:10:31.256 [ktor-cio-thread-3] INFO  [test] - [xaledy] : 罚站什么情况哈哈哈哈
2020-01-18 16:10:31.257 [ktor-cio-thread-3] INFO  [test] - [魔缘之翼] 送出了 1 个 [辣条]
2020-01-18 16:10:31.257 [ktor-cio-thread-3] INFO  [test] - [kjwlei] : 那老师
2020-01-18 16:10:31.257 [ktor-cio-thread-3] INFO  [test] - [山西省省草0311] : 大家一起上厕所
2020-01-18 16:10:32.339 [ktor-cio-thread-2] INFO  [test] - [魔缘之翼] 送出了 1 个 [辣条]
2020-01-18 16:10:32.339 [ktor-cio-thread-2] INFO  [test] - [果羿翰深] : 钠盐！！！！！
2020-01-18 16:10:32.339 [ktor-cio-thread-2] INFO  [test] - [c三3三d] : 过了过了
2020-01-18 16:10:32.339 [ktor-cio-thread-2] INFO  [test] - [姜少侠太帅了] : 科技美学
2020-01-18 16:10:32.339 [ktor-cio-thread-2] INFO  [test] - [魔缘之翼] 送出了 1 个 [辣条]
2020-01-18 16:10:32.339 [ktor-cio-thread-2] INFO  [test] - [魔缘之翼] 送出了 1 个 [辣条]
2020-01-18 16:10:32.340 [ktor-cio-thread-2] INFO  [test] - [diviner12138] : 怎么
2020-01-18 16:10:32.340 [ktor-cio-thread-2] INFO  [test] - [DirkZQ] : 那岩！！！！！！！！！！！！！！！！！！
2020-01-18 16:10:32.340 [ktor-cio-thread-2] INFO  [test] - [白哥哇哈哈] : 那岩
2020-01-18 16:10:32.340 [ktor-cio-thread-2] INFO  [test] - [蓝泽溪奈] : 是青衣宝贝啊！
2020-01-18 16:10:32.340 [ktor-cio-thread-2] INFO  [test] - [我和我的一只猫] : 大祥哥来了吗
2020-01-18 16:10:32.340 [ktor-cio-thread-2] INFO  [test] - [MM诚] 送出了 6 个 [辣条]
Disconnected from the target VM, address: '127.0.0.1:0', transport: 'socket'
```

🍻