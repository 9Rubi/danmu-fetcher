package ink.rubi.project

import com.google.inject.Inject
import ink.rubi.bilibili.common.FetcherContext
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.*
import org.slf4j.Logger
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.state.GameInitializationEvent
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.channel.MessageReceiver
import org.spongepowered.api.text.channel.MutableMessageChannel
import org.spongepowered.api.text.chat.ChatType
import org.spongepowered.api.text.format.TextColors
import java.util.*
import java.util.concurrent.Executors


@ExperimentalCoroutinesApi
@KtorExperimentalAPI
@Plugin(
    id = "bilibili-live-danmu-fetcher",
    name = "bilibili直播弹幕插件",
    authors = ["rubi"],
    version = "0.1.0",
    description = """
    在聊天栏展示直播弹幕的插件
"""
)
class BilibiliLiveDanmuPlugin {
    @Inject
    internal lateinit var logger: Logger

    internal var job: Job? = null

    internal val dispatcher: ExecutorCoroutineDispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()

    internal val danmuMessageChannel = DanmuMessageChannel()

    internal val client = FetcherContext.defaultClient()

    companion object {
        lateinit var instance: BilibiliLiveDanmuPlugin
    }

    @Listener
    fun initPlugin(event: GameInitializationEvent) {
        instance = this
        Sponge.getCommandManager().register(
            instance,
            CommandSpec.builder()
                .child(start(), "start")
                .child(connect(), "connect")
                .child(disconnect(), "disconnect")
                .child(showAllPlayer(), "show-all-player")
                .child(stop(), "stop")
                .executor { src, _ ->
                    src.sendMessage(Text.of(TextColors.RED, "wrong usage!"))
                    CommandResult.success()
                }.build(), "danmu")
        logger.info("bilibili 弹幕姬 加载完成")
    }
}


class DanmuMessageChannel : MutableMessageChannel {
    private var members: MutableSet<MessageReceiver?> = Collections.newSetFromMap(WeakHashMap())

    constructor() : this(Collections.emptySet())
    constructor(members: Collection<MessageReceiver?>) {
        this.members.addAll(members)
    }

    override fun removeMember(member: MessageReceiver): Boolean {
        return this.members.remove(member)
    }

    override fun transformMessage(
        sender: Any?,
        recipient: MessageReceiver,
        original: Text,
        type: ChatType
    ): Optional<Text> {
        var text: Text? = original
        text = Text.of(TextColors.AQUA, "[弹幕姬]", TextColors.RESET, text)
        return Optional.of(text)
    }

    override fun getMembers(): MutableSet<MessageReceiver?> {
        return Collections.unmodifiableSet(this.members)
    }

    override fun addMember(member: MessageReceiver): Boolean {
        return this.members.add(member)
    }

    override fun clearMembers() {
        this.members.clear()
    }
}
