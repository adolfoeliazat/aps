/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

import Color.*
import kotlin.browser.window
import kotlin.reflect.KFunction1


@native val React: IReact = noImpl

@native interface ReactElement
@native interface ReactClass

@native interface IReact {
    fun createElement(tagOrClass: dynamic, attrs: dynamic, vararg children: ReactElement): ReactElement
    fun createClass(def: dynamic): ReactClass
}

var jsuit: JSUITools

@native interface JSUITools {
    val isInTestScenario: Boolean
    val worldIsHalted: Boolean
    val art: JSArt
    fun OpenSourceCodeLink(def: dynamic): dynamic
    fun getURLQueryBeforeRunningTest(): dynamic
    fun byid(id: String): JQueryElement
    val debugPanes: dynamic
    fun dlog(vararg args: dynamic)
}

@native interface JSArt {
    val actionPlaceholderTag: String?
    fun renderStepDescriptions(): ReactElement
    fun getTestInstructions(): Array<dynamic>
    val stepDescriptions: Array<dynamic>
}


val exports = object {
    val openTestPassedPane: KFunction1<dynamic, Unit> = ::openTestPassedPane
    val gertrude: KFunction1<dynamic, Unit> = ::gertrude

    fun igniteKotlinShit(_jsuit: JSUITools) {
        jsuit = _jsuit
        println("----- Igniting Kotlin shit -----")
    }
}

fun jsDefined(x: dynamic): Boolean {
    return x !== null && x !== js("undefined")
}

fun openSourceCodeLink(build: OpenSourceCodeLinkBuilder.() -> Unit): ReactElement {
    val builder = OpenSourceCodeLinkBuilder()
    builder.build()
    return builder.reify()
}

class OpenSourceCodeLinkBuilder {
    var tag: String? = null
    var sourceLocation: String? = null
    val style = StyleBuilderQux()

    fun where(something: dynamic) {
        if (something.`$tag` != null) tag = something.`$tag`
        else if (something.`$sourceLocation` != null) sourceLocation = something.`$sourceLocation`
        else throw JSException("Gimme \$tag or \$sourceLocation")
    }

    fun reify(): ReactElement {
        return jsuit.OpenSourceCodeLink(json(
            "where" to json("\$tag" to tag, "\$sourceLocation" to sourceLocation),
            "style" to style.toJSON()))
    }
}

class Prop<T>() {
    var specified = false
    var value: T? = null

    constructor(initialValue: T?) : this() {
        value = initialValue
        specified = true
    }
}

fun plapla() {
    val sb = StyleBuilderQux()
    sb.marginLeft(null)
}

class StyleBuilderQux {
    val props = mutableMapOf<String, Any?>()

    fun toJSON(): Json? {
        val json = js("({})")
        for ((key, value) in props) json[key] = value
        return json
    }

    operator fun invoke(build: StyleBuilderQux.() -> Unit): StyleBuilderQux {
        this.build()
        return this
    }

    fun asn(other: StyleBuilderQux?) {
        props.putAll(other?.props ?: mapOf())
    }

    fun color(value: Color?) { props["color"] = value?.string }
    fun backgroundColor(value: Color?) { props["backgroundColor"] = value?.string }
    fun background(value: String?) { props["background"] = value }
    fun fontSize(value: String?) { props["fontSize"] = value }
    fun position(value: String?) { props["position"] = value }
    fun margin(value: String?) { props["margin"] = value }
    fun margin(value: Int) { props["margin"] = value }
    fun marginTop(value: String?) { props["marginTop"] = value }
    fun marginTop(value: Int) { marginTop("${value}px") }
    fun marginRight(value: String?) { props["marginRight"] = value }
    fun marginRight(value: Int) { marginRight("${value}px") }
    fun marginBottom(value: String?) { props["marginBottom"] = value }
    fun marginBottom(value: Int) { marginBottom("${value}px") }
    fun marginLeft(value: String?) { props["marginLeft"] = value }
    fun marginLeft(value: Int) { marginLeft("${value}px") }
    fun padding(value: String?) { props["padding"] = value }
    fun padding(value: Int) { props["padding"] = value }
    fun paddingTop(value: String?) { props["paddingTop"] = value }
    fun paddingTop(value: Int) { paddingTop("${value}px") }
    fun paddingRight(value: String?) { props["paddingRight"] = value }
    fun paddingRight(value: Int) { paddingRight("${value}px") }
    fun paddingBottom(value: String?) { props["paddingBottom"] = value }
    fun paddingBottom(value: Int) { paddingBottom("${value}px") }
    fun paddingLeft(value: String?) { props["paddingLeft"] = value }
    fun paddingLeft(value: Int) { paddingLeft("${value}px") }
    fun textAlign(value: String?) { props["textAlign"] = value }
    fun fontWeight(value: String?) { props["fontWeight"] = value }
    fun fontStyle(value: String?) { props["fontStyle"] = value }
    fun display(value: String?) { props["display"] = value }
    fun justifyContent(value: String?) { props["justifyContent"] = value }
    fun className(value: String?) { props["className"] = value }
    fun border(value: String?) { props["border"] = value }
    fun borderTop(value: String?) { props["borderTop"] = value }
    fun borderRight(value: String?) { props["borderRight"] = value }
    fun borderBottom(value: String?) { props["borderBottom"] = value }
    fun borderLeft(value: String?) { props["borderLeft"] = value }
    fun opacity(value: Double?) { props["opacity"] = value }
    fun width(value: Int) { props["width"] = value }
    fun width(value: String?) { props["width"] = value }
    fun height(value: Int) { props["height"] = value }
    fun height(value: String?) { props["height"] = value }
    fun top(value: Int) { props["top"] = value }
    fun top(value: String?) { props["top"] = value }
    fun right(value: Int) { props["right"] = value }
    fun right(value: String?) { props["right"] = value }
    fun bottom(value: Int) { props["bottom"] = value }
    fun bottom(value: String?) { props["bottom"] = value }
    fun left(value: Int) { props["left"] = value }
    fun left(value: String?) { props["left"] = value }

//    fun zzz(value: String?) { props["zzz"] = value }
}

typealias DoShit = () -> Unit

@native fun requestAnimationFrame(f: DoShit): Unit = noImpl
@native val document: dynamic

@native interface JQueryElement {
}

fun byid(id: String): JQueryElement = jsuit.byid(id)

object debugPanes {
    fun set(name: String, el: ReactElement, parentJqel: JQueryElement? = null) {
        jsuit.debugPanes.set(json("name" to name, "element" to el, "parentJqel" to parentJqel))
    }
}


fun openTestPassedPane(def: dynamic) {
    val scenario = def.scenario

    var scenarioName: String = scenario.name
    val links = mutableListOf<ReactElement>()

    val m = Regex("\\s+([0-9a-z]{8})-([0-9a-z]{4})-([0-9a-z]{4})-([0-9a-z]{4})-([0-9a-z]{12})\$").find(scenarioName)
    if (m != null) {
        scenarioName = scenarioName.slice(0..m.range.start - 1)
        links.add(openSourceCodeLink { tag = m.value.trim(); style { color(WHITE) } })
    }
    val actionPlaceholderTag = jsuit.art.actionPlaceholderTag
    if (jsDefined(actionPlaceholderTag)) {
        links.add(openSourceCodeLink { tag = actionPlaceholderTag; style { color(WHITE); marginLeft(10) } })
    }

    val uq = jsuit.getURLQueryBeforeRunningTest()
    if (!jsTruthy(uq.scrollToBottom) || uq.scrollToBottom == "yes" || uq.scrollToBottom == "success") {
        requestAnimationFrame { document.body.scrollTop = 99999 }
    }

    val testPassedPane = object : UpdatableElement() {
        override fun render(): ReactElement {
            if (!jsTruthy(scenarioName)) return span {}
            return div { noStateContributions = true
                style {
                    backgroundColor(GREEN_700); color(WHITE)
                    marginTop(10); padding("10px 10px"); textAlign("center"); fontWeight("bold")
                }

                div { style { paddingBottom(10) } }
                spanel(scenarioName)
                div { style { display("flex"); justifyContent("center")}; +links }


                div {
                    style { backgroundColor(WHITE); color(BLACK_BOOT); fontWeight("normal"); textAlign("left"); padding(5) }
                    renderTestStepDescriptions()
                }
            }
        }
    }

    debugPanes.set("openTestPassedPane", testPassedPane.element, parentJqel = byid("underFooter"))
}

fun ElementBuilder.renderTestStepDescriptions(): ReactElement {
    val els = mutableListOf<ReactElement>()

    val testInstructions = jsuit.art.getTestInstructions()
    var stepIndex = 0;
    var indent = 0

    for (instrIndex in testInstructions.indices) {
        data class Instr(val kind: String?, val long: String?, val fulfilled: Boolean, val name: String?)

        val instrdef = testInstructions[instrIndex]
        val opcode: String = jsKeys(instrdef).find { it[0] != '$' } ?: throw JSException("Gimme fucking opcode")
        val instr: Instr = instrdef[opcode]

        fun addLine(stepRowStyle: StyleBuilderQux? = null,
                    rulerContent: ReactElement? = null,
                    lineContent: ReactElement? = null,
                    actions: Iterable<ReactElement> = listOf(),
                    indent: Int) {

            els.add(div {
                style { marginTop(5); display("flex") }
                div { style { fontWeight("bold"); width(40) }; - rulerContent }
                // XXX This `width: 100%` is for fucking flexbox to not change `width: 40` above... http://stackoverflow.com/questions/7985021/css-flexbox-issue-why-is-the-width-of-my-flexchildren-affected-by-their-content
                div {
                    className = "showOnParentHovered-parent"; style { width("100%"); display("flex"); asn(stepRowStyle) }
                    spread((1..indent).map { div { style { width(20); borderLeft("2px dotted $GRAY_500") } } })
                    - lineContent
                    div {
                        className = "showOnParentHovered"
                        hor2 {
                            style { marginLeft(8); paddingLeft(8); borderLeft("2px solid $GRAY_500") }
                            + actions
                            - openSourceCodeLink { where(instrdef); style { marginLeft(20) } }
                        }
                    }
                }
            })
        }

        when (opcode) {
            "step" -> {
                val stepRowStyle = StyleBuilderQux()
                if (!instr.fulfilled) {
                    stepRowStyle { opacity(0.3) }
                }

                val untilParamValue = if (instrIndex == jsuit.art.stepDescriptions.size - 1) "infinity" else instrIndex


                fun label(color: Color, title: String) = span {
                    style { marginRight(5); padding(3); backgroundColor(color); fontSize("75%") }
                    - title
                }

                addLine(indent=indent, stepRowStyle=stepRowStyle,
                    rulerContent = spanel("#" + (stepIndex++ + 1)),
                    lineContent = div {
                        style { display("flex") }
                        when (instr.kind) {
                            "action" -> label(GREEN_100, "Action")
                            "state" -> label(LIGHT_BLUE_100, "State")
                            "navigation" -> label(BROWN_50, "Navigation")
                        }
                        - instr.long
                    },
                    actions = listOf(
                        link { title = "Run until $untilParamValue"
                            onClick {
                                var href: String = window.location.href
                                href = href.replace(Regex("&from[^&]*"), "")
                                href = href.replace(Regex("&until[^&]*"), "")
                                href += "&until=$untilParamValue"
                                window.location.href = href
                            }
                        }
                    )
                )
            }

            "beginSection" -> {
                addLine(indent=indent, lineContent = div { style { fontWeight("bold") }; - instr.long} )
                ++indent
            }

            "endSection" -> {
                --indent
            }

            "worldPoint" -> {
                addLine(indent=indent,
                    lineContent = div { style { fontWeight("normal"); fontStyle("italic") }; - "World point: ${instr.name}" },
                    rulerContent = div { style { position("relative") }
                        ia { className = "fa fa-circle"; style { color(GRAY_500) } }
                        div { style { width(38); position("absolute"); left(0); top(9); borderTop("2px dotted ${GRAY_500}") } }
                    },
                    actions = listOf(
                        link { title = "Run from"
                            onClick {
                                var href = window.location.href
                                href = href.replace(Regex("&from[^&]*"), "")
                                href = href.replace(Regex("&until[^&]*"), "")
                                href += "&from=${instr.name}"
                                window.location.href = href
                            }
                        }
                    )
                )
            }
        }
    }

    return div { controlTypeName = "renderStepDescriptions"; noStateContributions = true
        - div { style { backgroundColor(GRAY_200); fontWeight("bold") } -"Steps" }
        + els
    }
}

fun link(build: LinkBuilder.() -> Unit): ReactElement {
    val builder = LinkBuilder()
    builder.build()
    return builder.reify()
}

class LinkBuilder {
    val style = StyleBuilderQux()

    // TODO:vgrechka title, onClick

    fun where(something: dynamic) {
        if (something.`$tag` != null) tag = something.`$tag`
        else if (something.`$sourceLocation` != null) sourceLocation = something.`$sourceLocation`
        else throw JSException("Gimme \$tag or \$sourceLocation")
    }

    fun reify(): ReactElement {
        return jsuit.OpenSourceCodeLink(json(
            "where" to json("\$tag" to tag, "\$sourceLocation" to sourceLocation),
            "style" to style.toJSON()))
    }
}


fun jsKeys(obj: dynamic): Array<String> {
    return js("Object.keys(obj)")
}


fun jsTruthy(@Suppress("UNUSED_PARAMETER") x: dynamic): Boolean {
    return js("!!x")
}

fun gertrude(def: dynamic) {
    println("qweqwewrqrqwerqwerqwerqwerqwer")
}

fun makeTestShit() = object {
    val swearBox = object : UpdatableElement() {
        var phraseIndex = -1
        val phrases = arrayOf("Shit", "Fuck", "Bitch")

        override fun render() = div {
            id = "someid"
            if (phraseIndex == -1)
                div { style { color(ROSYBROWN) }; -"Kittens" }
            else
                div {
                    span { style { fontWeight("bold") }; -"Back to earth:" }
                    div { -phrases[phraseIndex] }
                }

            button("More") {
                if (++phraseIndex > phrases.lastIndex) {
                    phraseIndex = -1
                }
                update()
            }
        }
    }
}

interface IToStyleValueString {
    fun toStyleValueString(): String
}

enum class Color(val string: String) {
    // https://www.google.com/design/spec/style/color.html#color-color-palette
    BLACK("#000000"),
    BLACK_BOOT("#333333"), WHITE("#ffffff"),
    RED_50("#ffebee"), RED_100("#ffcdd2"), RED_200("#ef9a9a"), RED_300("#e57373"), RED_400("#ef5350"), RED_500("#f44336"), RED_600("#e53935"), RED_700("#d32f2f"), RED_800("#c62828"), RED_900("#b71c1c"), RED_A100("#ff8a80"), RED_A200("#ff5252"), RED_A400("#ff1744"), RED_A700("#d50000"),
    PINK_50("#fce4ec"), PINK_100("#f8bbd0"), PINK_200("#f48fb1"), PINK_300("#f06292"), PINK_400("#ec407a"), PINK_500("#e91e63"), PINK_600("#d81b60"), PINK_700("#c2185b"), PINK_800("#ad1457"), PINK_900("#880e4f"), PINK_A100("#ff80ab"), PINK_A200("#ff4081"), PINK_A400("#f50057"), PINK_A700("#c51162"),
    PURPLE_50("#f3e5f5"), PURPLE_100("#e1bee7"), PURPLE_200("#ce93d8"), PURPLE_300("#ba68c8"), PURPLE_400("#ab47bc"), PURPLE_500("#9c27b0"), PURPLE_600("#8e24aa"), PURPLE_700("#7b1fa2"), PURPLE_800("#6a1b9a"), PURPLE_900("#4a148c"), PURPLE_A100("#ea80fc"), PURPLE_A200("#e040fb"), PURPLE_A400("#d500f9"), PURPLE_A700("#aa00ff"),
    DEEP_PURPLE_50("#ede7f6"), DEEP_PURPLE_100("#d1c4e9"), DEEP_PURPLE_200("#b39ddb"), DEEP_PURPLE_300("#9575cd"), DEEP_PURPLE_400("#7e57c2"), DEEP_PURPLE_500("#673ab7"), DEEP_PURPLE_600("#5e35b1"), DEEP_PURPLE_700("#512da8"), DEEP_PURPLE_800("#4527a0"), DEEP_PURPLE_900("#311b92"), DEEP_PURPLE_A100("#b388ff"), DEEP_PURPLE_A200("#7c4dff"), DEEP_PURPLE_A400("#651fff"), DEEP_PURPLE_A700("#6200ea"),
    INDIGO_50("#e8eaf6"), INDIGO_100("#c5cae9"), INDIGO_200("#9fa8da"), INDIGO_300("#7986cb"), INDIGO_400("#5c6bc0"), INDIGO_500("#3f51b5"), INDIGO_600("#3949ab"), INDIGO_700("#303f9f"), INDIGO_800("#283593"), INDIGO_900("#1a237e"), INDIGO_A100("#8c9eff"), INDIGO_A200("#536dfe"), INDIGO_A400("#3d5afe"), INDIGO_A700("#304ffe"),
    BLUE_50("#e3f2fd"), BLUE_100("#bbdefb"), BLUE_200("#90caf9"), BLUE_300("#64b5f6"), BLUE_400("#42a5f5"), BLUE_500("#2196f3"), BLUE_600("#1e88e5"), BLUE_700("#1976d2"), BLUE_800("#1565c0"), BLUE_900("#0d47a1"), BLUE_A100("#82b1ff"), BLUE_A200("#448aff"), BLUE_A400("#2979ff"), BLUE_A700("#2962ff"),
    LIGHT_BLUE_50("#e1f5fe"), LIGHT_BLUE_100("#b3e5fc"), LIGHT_BLUE_200("#81d4fa"), LIGHT_BLUE_300("#4fc3f7"), LIGHT_BLUE_400("#29b6f6"), LIGHT_BLUE_500("#03a9f4"), LIGHT_BLUE_600("#039be5"), LIGHT_BLUE_700("#0288d1"), LIGHT_BLUE_800("#0277bd"), LIGHT_BLUE_900("#01579b"), LIGHT_BLUE_A100("#80d8ff"), LIGHT_BLUE_A200("#40c4ff"), LIGHT_BLUE_A400("#00b0ff"), LIGHT_BLUE_A700("#0091ea"),
    CYAN_50("#e0f7fa"), CYAN_100("#b2ebf2"), CYAN_200("#80deea"), CYAN_300("#4dd0e1"), CYAN_400("#26c6da"), CYAN_500("#00bcd4"), CYAN_600("#00acc1"), CYAN_700("#0097a7"), CYAN_800("#00838f"), CYAN_900("#006064"), CYAN_A100("#84ffff"), CYAN_A200("#18ffff"), CYAN_A400("#00e5ff"), CYAN_A700("#00b8d4"),
    TEAL_50("#e0f2f1"), TEAL_100("#b2dfdb"), TEAL_200("#80cbc4"), TEAL_300("#4db6ac"), TEAL_400("#26a69a"), TEAL_500("#009688"), TEAL_600("#00897b"), TEAL_700("#00796b"), TEAL_800("#00695c"), TEAL_900("#004d40"), TEAL_A100("#a7ffeb"), TEAL_A200("#64ffda"), TEAL_A400("#1de9b6"), TEAL_A700("#00bfa5"),
    GREEN_50("#e8f5e9"), GREEN_100("#c8e6c9"), GREEN_200("#a5d6a7"), GREEN_300("#81c784"), GREEN_400("#66bb6a"), GREEN_500("#4caf50"), GREEN_600("#43a047"), GREEN_700("#388e3c"), GREEN_800("#2e7d32"), GREEN_900("#1b5e20"), GREEN_A100("#b9f6ca"), GREEN_A200("#69f0ae"), GREEN_A400("#00e676"), GREEN_A700("#00c853"),
    LIGHT_GREEN_50("#f1f8e9"), LIGHT_GREEN_100("#dcedc8"), LIGHT_GREEN_200("#c5e1a5"), LIGHT_GREEN_300("#aed581"), LIGHT_GREEN_400("#9ccc65"), LIGHT_GREEN_500("#8bc34a"), LIGHT_GREEN_600("#7cb342"), LIGHT_GREEN_700("#689f38"), LIGHT_GREEN_800("#558b2f"), LIGHT_GREEN_900("#33691e"), LIGHT_GREEN_A100("#ccff90"), LIGHT_GREEN_A200("#b2ff59"), LIGHT_GREEN_A400("#76ff03"), LIGHT_GREEN_A700("#64dd17"),
    LIME_50("#f9fbe7"), LIME_100("#f0f4c3"), LIME_200("#e6ee9c"), LIME_300("#dce775"), LIME_400("#d4e157"), LIME_500("#cddc39"), LIME_600("#c0ca33"), LIME_700("#afb42b"), LIME_800("#9e9d24"), LIME_900("#827717"), LIME_A100("#f4ff81"), LIME_A200("#eeff41"), LIME_A400("#c6ff00"), LIME_A700("#aeea00"),
    YELLOW_50("#fffde7"), YELLOW_100("#fff9c4"), YELLOW_200("#fff59d"), YELLOW_300("#fff176"), YELLOW_400("#ffee58"), YELLOW_500("#ffeb3b"), YELLOW_600("#fdd835"), YELLOW_700("#fbc02d"), YELLOW_800("#f9a825"), YELLOW_900("#f57f17"), YELLOW_A100("#ffff8d"), YELLOW_A200("#ffff00"), YELLOW_A400("#ffea00"), YELLOW_A700("#ffd600"),
    AMBER_50("#fff8e1"), AMBER_100("#ffecb3"), AMBER_200("#ffe082"), AMBER_300("#ffd54f"), AMBER_400("#ffca28"), AMBER_500("#ffc107"), AMBER_600("#ffb300"), AMBER_700("#ffa000"), AMBER_800("#ff8f00"), AMBER_900("#ff6f00"), AMBER_A100("#ffe57f"), AMBER_A200("#ffd740"), AMBER_A400("#ffc400"), AMBER_A700("#ffab00"),
    ORANGE_50("#fff3e0"), ORANGE_100("#ffe0b2"), ORANGE_200("#ffcc80"), ORANGE_300("#ffb74d"), ORANGE_400("#ffa726"), ORANGE_500("#ff9800"), ORANGE_600("#fb8c00"), ORANGE_700("#f57c00"), ORANGE_800("#ef6c00"), ORANGE_900("#e65100"), ORANGE_A100("#ffd180"), ORANGE_A200("#ffab40"), ORANGE_A400("#ff9100"), ORANGE_A700("#ff6d00"),
    DEEP_ORANGE_50("#fbe9e7"), DEEP_ORANGE_100("#ffccbc"), DEEP_ORANGE_200("#ffab91"), DEEP_ORANGE_300("#ff8a65"), DEEP_ORANGE_400("#ff7043"), DEEP_ORANGE_500("#ff5722"), DEEP_ORANGE_600("#f4511e"), DEEP_ORANGE_700("#e64a19"), DEEP_ORANGE_800("#d84315"), DEEP_ORANGE_900("#bf360c"), DEEP_ORANGE_A100("#ff9e80"), DEEP_ORANGE_A200("#ff6e40"), DEEP_ORANGE_A400("#ff3d00"), DEEP_ORANGE_A700("#dd2c00"),
    BROWN_50("#efebe9"), BROWN_100("#d7ccc8"), BROWN_200("#bcaaa4"), BROWN_300("#a1887f"), BROWN_400("#8d6e63"), BROWN_500("#795548"), BROWN_600("#6d4c41"), BROWN_700("#5d4037"), BROWN_800("#4e342e"), BROWN_900("#3e2723"),
    GRAY_50("#fafafa"), GRAY_100("#f5f5f5"), GRAY_200("#eeeeee"), GRAY_300("#e0e0e0"), GRAY_400("#bdbdbd"), GRAY_500("#9e9e9e"), GRAY_600("#757575"), GRAY_700("#616161"), GRAY_800("#424242"), GRAY_900("#212121"),
    BLUE_GRAY_50("#eceff1"), BLUE_GRAY_100("#cfd8dc"), BLUE_GRAY_200("#b0bec5"), BLUE_GRAY_300("#90a4ae"), BLUE_GRAY_400("#78909c"), BLUE_GRAY_500("#607d8b"), BLUE_GRAY_600("#546e7a"), BLUE_GRAY_700("#455a64"), BLUE_GRAY_800("#37474f"), BLUE_GRAY_900("#263238"),
    RED("red"), GREEN("green"), BLUE("blue"), ROSYBROWN("rosybrown"),;

    override fun toString(): String {
        return string
    }
}

class JSException(message: String) : Throwable(message) {
    override val message = message
    val stack = js("Error().stack")
}

abstract class UpdatableElement {
    abstract fun render(): ReactElement

    var inst: dynamic = null

    val element = React.createElement(React.createClass(json(
        "componentDidMount" to fun() {
            inst = js("this")
        },

        "componentWillUnmount" to fun() {
            inst = null
        },

        "render" to fun(): ReactElement {
            return render()
        }
    )), json())

    fun update() {
        if (jsuit.isInTestScenario && jsuit.worldIsHalted) return
        if (inst == null) return

        try {
            inst.forceUpdate()
        } catch (e: dynamic) {
            js("raiseWithMeta")(json("message" to e.message, "cause" to e))
        }
    }
}

typealias ReactEventHandler = (ReactEvent) -> Unit

@native interface ReactEvent {
    fun preventDefault()
    fun stopPropagation()
}

fun preventAndStop(e: ReactEvent) {
    e.preventDefault()
    e.stopPropagation()
}

fun horizontal_usageSample1() {
    var maybeMissing1: ReactElement? = null

    val shit = div {
        horizontal(15) {
            - "first"
            - "second"
            - maybeMissing1
            - "third"

            - div {
                - "foo"
                - "bar"
            }
        }
    }
}

fun horizontal(spacing: Int, build: ElementBuilder.() -> Unit): ReactElement {
    return div {
        controlTypeName = "horizontal"
        style { display("flex") }

        transformAddedChild = {div {
            style { if (children.isNotEmpty()) marginLeft(spacing) }
            - it
        }}

        build()
    }

//    #extract {spacing, style={}, className=''} from def
//
//    return diva({controlTypeName: def.controlTypeName || 'horizontala', className, style: {display: 'flex'}.asnn(style)}.asn1(def),
//        ...items.map((item, i) => item && diva({style: {marginLeft: i > 0 ? spacing : 0}}, item)))
}

fun hor1(build: ElementBuilder.() -> Unit) {
    horizontal(4) { controlTypeName = "hor2" }
}

fun hor2(build: ElementBuilder.() -> Unit) {
    horizontal(8) { controlTypeName = "hor2" }
}


val div = makeElementCtor("div")
val ia = makeElementCtor("i")
val span = makeElementCtor("span")

fun makeElementCtor(tag: String, parentElementBuilder: ElementBuilder? = null): (ElementBuilder.() -> Unit) -> ReactElement {
    return fun(build: ElementBuilder.() -> Unit): ReactElement {
        val builder = ElementBuilder(tag)
        builder.build()
        parentElementBuilder?.add(builder)
        return builder
    }
}

fun spanel(string: String): ReactElement {
    val child: dynamic = string
    return React.createElement("span", js("({})"), child)
}

class ElementBuilder(val tag: String) : ReactElement {
    var id: String? = null
    var className: String? = null
    var onClickHandler: ReactEventHandler? = null
    var noStateContributions: Boolean = false
    val children = mutableListOf<ReactElement>()
    val style = StyleBuilderQux()
    var controlTypeName: String? = null
    var transformAddedChild: ((ReactElement) -> ReactElement)? = null

    operator fun CharSequence?.unaryMinus() {
        add(spanel(this.toString()))
    }

    operator fun ReactElement?.unaryMinus() {
        if (this != null) add(this)
    }

    fun add(el: ReactElement?) {
        if (el != null) children.add(el)
    }

    fun spread(xs: Iterable<ReactElement?>) {
        for (x in xs.filterNotNull()) add(x)
    }

    operator fun Iterable<ReactElement?>.unaryPlus() {
        spread(this)
    }

//    val div = makeElementCtor("div", this)
//    val span = makeElementCtor("div", this)

    fun toReactElement(): ReactElement {
        val jsAttrs = js("({})")

        jsAttrs.style = style.toJSON()
        // jsuit.dlog("---style", jsAttrs.style)
        if (onClickHandler != null) jsAttrs.onClick = onClickHandler

        return React.createElement(tag, jsAttrs, *children.toTypedArray())
    }

    fun onClick(handler: ReactEventHandler) {
        this.onClickHandler = handler
    }
}

fun ElementBuilder.button(title: String, onClick: ReactEventHandler): ReactElement {
    return makeElementCtor("button", this)() {
        spanel(title)
        this.onClick { e ->
            preventAndStop(e)
            onClick(e)
        }
    }
}




