## 使用示例
```xml
<dependency>
    <groupId>ink.rubi</groupId>
    <artifactId>danmu-fetcher</artifactId>
    <version>0.0.33</version>
</dependency>        
```
```groovy
dependencies{
    compile 'ink.rubi:danmu-fetcher:0.0.33'
    compile 'ch.qos.logback:logback-classic:1.2.1' // ..
}
```



## use kotlin
```kotlin
@KtorExperimentalAPI
@ExperimentalCoroutinesApi
fun main() = runBlocking {
    val job = launch { connectLiveRoom(92613) }
    delay(15_000)
    job.cancel()
}

```

## use java
```java
    
       ...还没写

```

## 效果如下
```log
Connected to the target VM, address: '127.0.0.1:0', transport: 'socket'
2020-01-30 18:24:08.089 [main] INFO  [danmu-client] - web-titles        : 318
2020-01-30 18:24:08.096 [main] INFO  [danmu-client] - room id           : 92613
2020-01-30 18:24:08.121 [main] INFO  [danmu-client] - use server        : tx-gz-live-comet-11.chat.bilibili.com
2020-01-30 18:24:08.123 [main] INFO  [danmu-client] - connect!
2020-01-30 18:24:08.161 [DefaultDispatcher-worker-1] WARN  io.ktor.util.random - NativePRNGNonBlocking is not found, fallback to SHA1PRNG
2020-01-30 18:24:08.401 [main] INFO  [danmu-client] - connected!
2020-01-30 18:24:08.402 [main] INFO  [danmu-client] - login ...
2020-01-30 18:24:08.490 [main] INFO  [danmu-client] - login success!
2020-01-30 18:24:08.490 [main] INFO  [danmu-client] - response => {"code":0}
2020-01-30 18:24:09.092 [main] INFO  [danmu-client] - [疾风幻步] : 改可怜龙吗
2020-01-30 18:24:09.093 [main] INFO  [danmu-client] - [Ｎｉｇｈｔ丶⑨] : api开荒的话直播吗
2020-01-30 18:24:09.094 [main] INFO  [danmu-client] - [荼菲子_Lemon] 送出了 4 个 [辣条]
2020-01-30 18:24:10.788 [main] INFO  [danmu-client] - [荼菲子_Lemon] 送出了 1 个 [新年红包]
2020-01-30 18:24:10.789 [main] INFO  [danmu-client] - [二次元紅茶] : av9367511  刚找到 唱歌Pi
2020-01-30 18:24:14.340 [main] INFO  [danmu-client] - [Leon大哥和他的小弟们] 送出了 5 个 [辣条]
2020-01-30 18:24:14.341 [main] INFO  [danmu-client] - [MnPig] : mc二月番
2020-01-30 18:24:14.342 [main] INFO  [danmu-client] - [猫毛毛毛_] : 233333
2020-01-30 18:24:17.736 [main] INFO  [danmu-client] - [鱼OU] : 不直播
2020-01-30 18:24:18.490 [main] INFO  [danmu-client] - disconnect!
2020-01-30 18:24:18.493 [main] ERROR [danmu-client] - disconnect from server cause : 
kotlinx.coroutines.JobCancellationException: StandaloneCoroutine was cancelled
Disconnected from the target VM, address: '127.0.0.1:0', transport: 'socket'

Process finished with exit code 0

```

🍻