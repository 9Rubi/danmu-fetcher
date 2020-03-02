package ink.rubi.project

import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.ClickAction
import org.spongepowered.api.text.action.HoverAction
import org.spongepowered.api.text.action.ShiftClickAction
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles

fun Text.aqua(): Text = Text.builder().color(TextColors.AQUA).append(this).toText()
fun Text.black(): Text = Text.builder().color(TextColors.BLACK).append(this).toText()
fun Text.blue(): Text = Text.builder().color(TextColors.BLUE).append(this).toText()
fun Text.darkAqua(): Text = Text.builder().color(TextColors.DARK_AQUA).append(this).toText()
fun Text.darkBlue(): Text = Text.builder().color(TextColors.DARK_BLUE).append(this).toText()
fun Text.darkGray(): Text = Text.builder().color(TextColors.DARK_GRAY).append(this).toText()
fun Text.darkGreen(): Text = Text.builder().color(TextColors.DARK_GREEN).append(this).toText()
fun Text.darkPurple(): Text = Text.builder().color(TextColors.DARK_PURPLE).append(this).toText()
fun Text.darkRed(): Text = Text.builder().color(TextColors.DARK_RED).append(this).toText()
fun Text.gold(): Text = Text.builder().color(TextColors.GOLD).append(this).toText()
fun Text.gray(): Text = Text.builder().color(TextColors.GRAY).append(this).toText()
fun Text.green(): Text = Text.builder().color(TextColors.GREEN).append(this).toText()
fun Text.lightPurple(): Text = Text.builder().color(TextColors.LIGHT_PURPLE).append(this).toText()
fun Text.red(): Text = Text.builder().color(TextColors.RED).append(this).toText()
fun Text.yellow(): Text = Text.builder().color(TextColors.YELLOW).append(this).toText()
fun Text.white(): Text = Text.builder().color(TextColors.WHITE).append(this).toText()

fun Text.bold(): Text = Text.builder().style(TextStyles.BOLD).append(this).toText()
fun Text.italic(): Text = Text.builder().style(TextStyles.ITALIC).append(this).toText()
fun Text.obfuscated(): Text = Text.builder().style(TextStyles.OBFUSCATED).append(this).toText()
fun Text.reset(): Text = Text.builder().style(TextStyles.RESET).append(this).toText()
fun Text.strikethrough(): Text = Text.builder().style(TextStyles.STRIKETHROUGH).append(this).toText()
fun Text.underline(): Text = Text.builder().style(TextStyles.UNDERLINE).append(this).toText()

operator fun Text.plus(other: Text): Text = Text.builder().append(this).append(other).toText()

operator fun Text.plus(other: String): Text = Text.builder().append(this).append(Text.of(other)).toText()

fun <T : ClickAction<*>> Text.click(action: T): Text = Text.builder().append(this).onClick(action).toText()
fun <T : HoverAction<*>> Text.hover(action: T): Text = Text.builder().append(this).onHover(action).toText()
fun <T : ShiftClickAction<*>> Text.shiftClick(action: T): Text = Text.builder().append(this).onShiftClick(action).toText()

fun String.aqua(): Text = Text.of(this).aqua()
fun String.black(): Text = Text.of(this).black()
fun String.blue(): Text = Text.of(this).blue()
fun String.darkAqua(): Text = Text.of(this).darkAqua()
fun String.darkBlue(): Text = Text.of(this).darkBlue()
fun String.darkGray(): Text = Text.of(this).darkGray()
fun String.darkGreen(): Text = Text.of(this).darkGreen()
fun String.darkPurple(): Text = Text.of(this).darkPurple()
fun String.darkRed(): Text = Text.of(this).darkRed()
fun String.gold(): Text = Text.of(this).gold()
fun String.gray(): Text = Text.of(this).gray()
fun String.green(): Text = Text.of(this).green()
fun String.lightPurple(): Text = Text.of(this).lightPurple()
fun String.red(): Text = Text.of(this).red()
fun String.yellow(): Text = Text.of(this).yellow()
fun String.white(): Text = Text.of(this).white()

fun String.bold(): Text = Text.of(this).bold()
fun String.italic(): Text = Text.of(this).italic()
fun String.obfuscated(): Text = Text.of(this).obfuscated()
fun String.reset(): Text = Text.of(this).reset()
fun String.strikethrough(): Text = Text.of(this).strikethrough()
fun String.underline(): Text = Text.of(this).underline()

fun <T : ClickAction<*>> String.click(action: T): Text = Text.of(this).click(action)
fun <T : HoverAction<*>> String.hover(action: T): Text = Text.of(this).hover(action)
fun <T : ShiftClickAction<*>> String.shiftClick(action: T): Text = Text.of(this).shiftClick(action)