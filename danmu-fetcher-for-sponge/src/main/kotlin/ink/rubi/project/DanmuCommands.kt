package ink.rubi.project

import ink.rubi.bilibili.live.connectLiveRoom
import ink.rubi.bilibili.live.handler.simpleEventHandler
import ink.rubi.project.BilibiliLiveDanmuPlugin.Companion.instance
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text

@ExperimentalCoroutinesApi
@KtorExperimentalAPI
internal fun start(): CommandSpec {
    return CommandSpec.builder().arguments(
        GenericArguments.integer(Text.of("roomId"))
    ).executor { src, args ->
        val roomId = args.getOne<Int>("roomId").get()
        instance.job?.takeIf { it.isActive }?.let {
            src.sendMessage(Text.of("弹幕姬还活着!"))
            return@executor CommandResult.success()
        }
        src.sendMessage(Text.of("启动弹幕姬"))
        instance.danmuMessageChannel.addMember(src)
        instance.job = CoroutineScope(instance.dispatcher).connectLiveRoom(
            roomId = roomId,
             messageHandler = typedMessageHandler {
                 onReceiveDanmu {
                     instance.danmuMessageChannel.send(Text.of("${user.name} : $said"))
                 }
                 onReceiveGift {
                     instance.danmuMessageChannel.send(Text.of("${gift.uname} 送出了 ${gift.num} 个 ${gift.giftName}"))
                 }
                 onError {
                     instance.logger.info("catch exception:",e.cause)
                 }
             },
            eventHandler = simpleEventHandler {
                onLoginSuccess {
                    src.sendMessage("连接成功".green())
                }
                onDisconnect {
                    src.sendMessage("断开连接".red())
                }
            }
            , client = instance.client
        )
        return@executor CommandResult.success()
    }.build()
}

@ExperimentalCoroutinesApi
@KtorExperimentalAPI
internal fun connect(): CommandSpec {
    return CommandSpec.builder().executor { src, _ ->
        instance.danmuMessageChannel.addMember(src)
        src.sendMessage(Text.of("加入了弹幕姬频道"))
        CommandResult.success()
    }.build()
}

@ExperimentalCoroutinesApi
@KtorExperimentalAPI
internal fun disconnect(): CommandSpec {
    return CommandSpec.builder().executor { src, _ ->
        instance.danmuMessageChannel.removeMember(src)
        src.sendMessage(Text.of("退出了弹幕姬频道"))
        CommandResult.success()
    }.build()
}

@ExperimentalCoroutinesApi
@KtorExperimentalAPI
internal fun showAllPlayer(): CommandSpec {
    return CommandSpec.builder().executor { src, _ ->
        src.sendMessage(
            Text.of(
                instance.danmuMessageChannel.members.map { (it as? Player)?.name }.joinToString(
                    prefix = "[",
                    postfix = "]"
                )
            )
        )
        CommandResult.success()
    }.build()
}

@ExperimentalCoroutinesApi
@KtorExperimentalAPI
internal fun stop(): CommandSpec {
    return CommandSpec.builder().executor { src, _ ->
        instance.job?.cancel()
        instance.danmuMessageChannel.send(Text.of("${(src as? Player)?.name ?: ""}关闭了弹幕姬"))
        CommandResult.success()
    }.build()
}
