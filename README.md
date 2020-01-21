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
          <version>0.0.20</version>
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
    compile 'ink.rubi:danmu-fetcher:0.0.20'
    compile 'ch.qos.logback:logback-classic:1.2.1'
}
```



## use kotlin
```kotlin
import ink.rubi.bilibili.live.danmu.DanmuListener.receiveDanmu
import ink.rubi.bilibili.live.danmu.handler.messageHandler
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val log: Logger = LoggerFactory.getLogger("[test]")

object Test {
    @ExperimentalCoroutinesApi
    @FlowPreview
    @KtorExperimentalAPI
    @JvmStatic
    fun main(args: Array<String>) {
        val roomId = 5050
        runBlocking {
            val job = launch {
                receiveDanmu(roomId){
                    messageHandler {
                        onReceiveDanmu { user, said ->
                            log.info("[$user] : $said")
                        }
                        onReceiveGift { user, num, giftName ->
                            log.info("[$user] 送出了 $num 个 [$giftName]")
                        }
                        onSomeOneEnterInLiveRoom {
                            log.info("[$it] 进入了直播间")
                        }
                        onGuardEnterInLiveRoom {
                            log.info("[舰长][$it] 进入了直播间")
                        }
                        onUnknownTypeMessage {
                            log.warn(it)
                        }
                        onAllTypeMessage {
                            log.error(it)
                        }
                    }
                }
            }
            delay(10000)
            job.cancel()
        }
    }
}

```

## use java
```java
    
       ...还没写

```

## 效果如下
```log
Connected to the target VM, address: '127.0.0.1:0', transport: 'socket'
2020-01-20 16:31:51.996 [main] INFO  [danmu-client] - room id => 5050
2020-01-20 16:31:52.052 [main] INFO  [danmu-client] - use        broadcastlv.chat.bilibili.com
2020-01-20 16:31:52.079 [DefaultDispatcher-worker-1] WARN  io.ktor.util.random - NativePRNGNonBlocking is not found, fallback to SHA1PRNG
2020-01-20 16:31:52.293 [main] INFO  [danmu-client] - login ....
2020-01-20 16:31:52.378 [ktor-cio-thread-1] INFO  [danmu-client] - response => {"code":0}
2020-01-20 16:31:52.378 [ktor-cio-thread-1] INFO  [danmu-client] - heart beat packet
2020-01-20 16:31:54.101 [ktor-cio-thread-1] INFO  [test] - [半夏咕噜] 送出了 2 个 [新年红包]
2020-01-20 16:31:54.102 [ktor-cio-thread-1] INFO  [test] - [老板来碗鱼丸牛肉面] : 儿子打爹
2020-01-20 16:31:57.756 [ktor-cio-thread-1] INFO  [test] - [决心丶丶] 送出了 15 个 [辣条]
2020-01-20 16:31:57.757 [ktor-cio-thread-1] INFO  [test] - [白毛小哥w] 送出了 1 个 [辣条]
2020-01-20 16:31:57.758 [ktor-cio-thread-1] INFO  [test] - [举栗子的于理] 送出了 8 个 [辣条]
2020-01-20 16:31:57.758 [ktor-cio-thread-1] INFO  [test] - [时之图] 送出了 1 个 [辣条]
Disconnected from the target VM, address: '127.0.0.1:0', transport: 'socket'

Process finished with exit code 0

```

🍻