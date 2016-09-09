/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

import kotlin.browser.document
import kotlin.browser.window
import Color.*

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

    override fun toString() = string
}

@native interface IKotlinShit {
    fun ignite(_global: dynamic, _jshit: dynamic)
}

var global: dynamic = null
var jshit: dynamic = null

object KotlinShit : IKotlinShit {
    override fun ignite(_global: dynamic, _jshit: dynamic) {
        println("----- Igniting front Kotlin shit -----")
        global = _global; jshit = _jshit
        jshit.art.renderStepDescriptions = ::renderStepDescriptions
        jshit.art.openTestPassedPane = ::openTestPassedPane
        jshit.art.gertrude = ::gertrude

        global.testAsyncShit = ::testAsyncShit
    }

    val kot_melinda = ::melinda
}

@Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE")
fun link(vararg args: dynamic): ReactElement {
    val shit = jshit
    return js("shit.link.apply(null, args)")
}

@Suppress("UNUSED_PARAMETER")
private fun dynamicKeys(obj: dynamic): dynamic {
    return js("Object.keys(obj)")
}

@Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE")
fun jdiva(vararg args: dynamic): ReactElement {
    val shit = jshit
    return js("shit.diva.apply(null, args)")
}

@Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE")
fun hor2(vararg args: dynamic): ReactElement {
    val shit = jshit
    return js("shit.hor2.apply(null, args)")
}

fun fuckingDiviarius(): ReactElement {
    return jshit.diva(js("({})"), "Fucking Diviarius")
}

fun fuckingSpantus(): ReactElement {
    return jshit.spana(js("({})"), "Fucking Spantus")
}

fun gertrude(def: dynamic) {
    // {descr='Describe me', $tag, $definitionStack, actual, expected, expectedExtender, scrollThere=true, thisIsReassertion}

    val tag: String = def.`$tag`; val definitionStack = def.`$definitionStack`; val actual = def.actual
    var expected = def.expected; val expectedExtender = def.expectedExtender
    val thisIsReassertion: Boolean = def.thisIsReassertion
    val descr: String = if (def.descr == undefined) "Describe me" else def.descr
    val scrollThere: Boolean = if (def.scrollThere == undefined) true else def.scrollThere

    jshit.setLastSeenTag(tag)
    if (expected == undefined) {
        raise("WTF")
    }
    if (expected == "---generated-shit---") {
        expected = global.GENERATED_SHIT[tag]
        if (expected == undefined) expected = js("({})")
    }

    var extenderKeys = js("[]")
    if (expectedExtender) {
        expectedExtender(json("expected" to expected))

        val fromExtender = js("({})")
        expectedExtender(json("expected" to fromExtender))
        extenderKeys = global.Object.keys(fromExtender)

        for (k in jsArrayToIterable(extenderKeys)) {
            val komega = k.replace(js("/-i\\d\\d\\d/g"), "-ω")
            if (!extenderKeys.includes(komega)) {
                extenderKeys.push(komega)
            }
        }
    }

    val keyToDefinitionStack = js("({})"); val keyToCallStack = js("({})"); val keyToControl = js("({})")
    for (key in jsArrayToIterable(global.Object.keys(actual))) {
        val value = actual[key]
        if (value && value.`$definitionStack`) {
            actual[key] = value.value
            keyToDefinitionStack[key] = value.`$definitionStack`
        }
        if (value && value.`$callStack`) {
            actual[key] = value.value
            keyToCallStack[key] = value.`$callStack`
        }
        if (value && value.control) {
            actual[key] = value.value
            keyToControl[key] = value.control
        }
    }

    if (jshit.deepEquals(actual, expected)) {
        if (thisIsReassertion) {
            console.warn("// TODO:vgrechka If reassertion after hot reload passed, make it clear in UI    e5d48597-62d4-4740-b915-04f6934c2bc0 ")
        }
        jshit.setArtLastSuccessfulExpected(expected)
        return
    }

    var detailsUI: dynamic = null
    if (jshit.getURLQueryBeforeRunningTest().minimalGertrude == "yes" || global.testGlobal.minimalGertrude) {
        detailsUI = div {styleKludge = json("background" to jshit.WHITE); -"I am minimal because of minimalGertrude"}
//        detailsUI = jshit.diva(json("style" to json("background" to jshit.WHITE)), t("I am minimal because of minimalGertrude"))
    } else {
        detailsUI = jshit.updatableElement(js("({})"), wholeShitCtor@ {updateWholeShit ->
            var stripFuckingIndices = true; var hideFuckingKeyRepetitions = false; var tabs: dynamic = null
            var paneControls: dynamic = null; var lineHideFilter = jshit.noop
            var highlightedKeys = js("({})"); var pileOfShit = js("({})")
            val progressPlaceholder = jshit.Placeholder()
            var unifyIndicesCheck: dynamic = null; var hideKeyRepetitionsCheck: dynamic = null

            fun makeFuckingTabs() {
                fun repr(it: dynamic, opts: dynamic = js("({})")): dynamic {
                    // {stripIndices}={}
                    val stripIndices: Boolean = opts.stripIndices

                    var s = jshit.deepInspect(it)
                    // s = s.replace(/\\n/g, "\n")

                    if (stripIndices) {
                        s = s.replace(js("/-i\\d\\d\\d/g"), "-ω") // omega
                    }

                    if (s[0] == "{") s = s[0] + "\n " + s.slice(1)
                    if (s[s.length - 1] == "}") s = s.slice(0, s.length - 1) + "\n" + s[s.length - 1]

                    s = s
                        .split("\n")
                        .map({x: dynamic ->
                            var res = x
                            res = res.trim()
                            if (res == "{" || res == "}" || jshit.isBlank(res)) return@map res
                            if (!res.endsWith(",")) res += ","
                            return@map res
                        })
                    .join("\n")

                    return s
                }

                fun isExtValueLine(valueLine: dynamic): Boolean {
                    if (valueLine == "{" || valueLine == "}") return false
                    if (jshit.isBlank(valueLine)) return false

                    val colonIndex = valueLine.indexOf(":")
                    invariant(colonIndex != -1, "Expecting colon: ${valueLine}")
                    val key = jshit.trim(valueLine.slice(0, colonIndex).replace(js("/'/g"), ""))
                    if (extenderKeys.includes(key)) {
                        return true
                    }
                    return false
                }


                val pileOfShit = js("({})")

                jshit.sortKeys(actual) // Order of keys sent over the wire is mangled
                jshit.sortKeys(expected)

                val definitionStacks = js("[]"); val callStacks = js("[]"); val controls = js("[]"); val origKeys = js("[]")
                for (key in jsArrayToIterable(global.Object.keys(actual))) {
                    definitionStacks.push(keyToDefinitionStack[key])
                    callStacks.push(keyToCallStack[key])
                    controls.push(keyToControl[key])
                    origKeys.push(key)
                }

                val actualString = repr(actual, json("stripIndices" to stripFuckingIndices))
                val actualStringOrig = repr(actual, json("stripIndices" to false))
                val expectedString = repr(expected, json("stripIndices" to stripFuckingIndices))
                val lastExpectedString = if(jshit.getArtLastSuccessfulExpected()) repr(jshit.getArtLastSuccessfulExpected(), json("stripIndices" to stripFuckingIndices)) else "--- Nothing ---"
                val diffDivs = js("[]"); val diffLastDivs = js("[]")

                var lineIndex = 0; var actualLineIndex = 0

                fun fillDiffDivs(arg: dynamic) {
                    // {divs, string1, string2, greenTitle}
                    val divs = arg.divs; val string1 = arg.string1; val string2 = arg.string2; var greenTitle = arg.greenTitle

                    if (!greenTitle) greenTitle = "Expected"
                    var prevLabel: dynamic = undefined
                    val diffLineItems = jshit.diff.diffLines(string1, string2)
                    for (item in jsArrayToIterable(diffLineItems)) {
                        var backgroundColor: dynamic = undefined
                        var label: dynamic = undefined
                        if (item.added) {
                            backgroundColor = RED_100.string
                            label = "Actual"
                        } else if (item.removed) {
                            backgroundColor = GREEN_100.string
                            label = greenTitle
                        } else {
                            backgroundColor = WHITE.string
                            label = undefined
                        }
                        if (label && label != prevLabel) {
                            divs.push(div { styleKludge = json("backgroundColor" to backgroundColor, "fontWeight" to "bold")
                                label })
                        }
                        prevLabel = label

                        item.value = item.value.replace(js("/\\n*\$/"), "")
                        val valueLines = item.value.split("\n")
                        val keysAdded = js("({})")
                        for (valueLine in jsArrayToIterable(valueLines)) {
                            var shouldBeHiddenCuaseItsFuckingKeyRepetition: dynamic = undefined
                            var shouldBeHighlighted: dynamic = undefined

                            val lineDivID = puid()
                            var gotoIcon: dynamic = undefined; var metaBox: dynamic = undefined
                            if (valueLine != "{" && valueLine != "}" && !item.removed) {
                                val definitionStack = definitionStacks[actualLineIndex] || js("[]")
                                var callStack = callStacks[actualLineIndex] || js("[]")
                                if (definitionStack || callStack) {
                                    var open = false
                                    metaBox = jshit.updatableElement(js("({})"), {update: dynamic ->
                                        {
                                            div { styleKludge = json("display" to "inline-block", "verticalAlign" to "top", "marginLeft" to 10)
                                                -span { className = "fa fa-caret-${if (open) "up" else "down"}"; styleKludge = json("cursor" to "pointer")
                                                    onClick {
                                                        open = !open
                                                        update()
                                                    }
                                                }

                                                if (open)
                                                    -div { styleKludge = json("display" to "inline-block", "verticalAlign" to "top", "marginLeft" to 10)
                                                        -jshit.renderStacks(json("definitionStack" to definitionStack, "callStack" to callStack))
                                                    }
                                            }
                                        }
                                    })
                                }

                                val control = controls[actualLineIndex]
                                if (control) {
                                    gotoIcon = div { className = "showOnParentHovered"
                                        -span { className = "fa fa-search"; styleKludge = json("cursor" to "pointer", "marginLeft" to 10)
                                            onClick {
                                                jshit.revealControl(control, json("scrollToTarget" to true))
                                            }
                                        }
                                    }
                                }

                                val colonIndex = valueLine.indexOf(":")
                                if (colonIndex != -1) {
                                    var key = valueLine.slice(0, colonIndex).trim()
                                    if (key.startsWith("\"") && key.endsWith("'")) {
                                        key = key.slice(1, key.length - 1)
                                    }
                                    shouldBeHiddenCuaseItsFuckingKeyRepetition = hideFuckingKeyRepetitions && keysAdded[key]
                                    keysAdded[key] = true

                                    val origKey = origKeys[actualLineIndex]
                                    shouldBeHighlighted = highlightedKeys[origKey]
                                    pileOfShit["scrollToDivForKey-${origKey}"] = {
                                        global.requestAnimationFrame { jshit.jQuery(document).scrollTop(jshit.byid(lineDivID).offset().top - 50 - 20) }
                                    }
                                } else {
                                    console.warn("WTF colonIndex is -1")
                                }

                                ++actualLineIndex
                            }

                            divs.push({
                                if (!lineHideFilter(valueLine) && !shouldBeHiddenCuaseItsFuckingKeyRepetition)
                                    jshit.updatableElement(js("({})"), {update: dynamic ->
                                        val style = json("backgroundColor" to backgroundColor, "position" to "relative")
                                        if (shouldBeHighlighted) {
                                            global.Object.assign(style, json(
                                                "borderLeft" to "5px solid black",
                                                "paddingLeft" to 5,
                                                "background" to ORANGE_100))
                                        }

                                        return@updatableElement {
                                            div {
                                                id = "" + lineDivID; className = "showOnParentHovered-parent"
                                                styleKludge = style

                                                if (isExtValueLine(valueLine))
                                                    -span {styleKludge = json("marginRight" to 5, "padding" to 3, "background" to jshit.ORANGE_200, "fontSize" to "75%")
                                                        -"ext" }
                                                -div {styleKludge = json("display" to "inline-block", "verticalAlign" to "top")
                                                    -valueLine }

                                                -metaBox
                                                -gotoIcon
                                            }
                                        }
                                    })
                                else undefined
                            })

                            ++lineIndex
                        }
                    }
                }

                fillDiffDivs(json("divs" to diffDivs, "string1" to expectedString, "string2" to actualString))
                lineIndex = 0; actualLineIndex = 0
                fillDiffDivs(json("divs" to diffLastDivs, "string1" to lastExpectedString, "string2" to actualString, "greenTitle" to "Last"))

                val my = js("({})")

                var actualStringForPasting = actualStringOrig.trim()
                if (actualStringForPasting[0] == "{" || actualStringForPasting[0] == "[") {
                    actualStringForPasting = jshit.trimStart(actualStringForPasting.slice(1, actualStringForPasting.length - 1))
                }
                val chars = actualStringForPasting.split("")

                val slash = "\\"
                for (i in 0 until chars.length) {
                    if (chars[i] == "'" && (i == 0 || chars[i - 1] != slash)) {
                        chars[i] = "`"
                    }
                }
                actualStringForPasting = chars.join("")
                actualStringForPasting = actualStringForPasting.replace(js("/`((\\w|\\d\\|_|-|\\$|\\.|:)*?)`: /g"), "'$1': ")
                var replacements = js("[]")
                var backtickIndex: dynamic = undefined; var from = 0; var btis = js("[]")
                while (true) {
                    backtickIndex = actualStringForPasting.indexOf("`", from)
                    if (backtickIndex == -1) break
                    btis.push(backtickIndex)
                    if (btis.length == 2) {
                        var literal = actualStringForPasting.slice(btis[0], btis[1] + 1)
                        if (js("/\\r|\\n/").test(literal)) {
                            literal = literal[0] + "\n" + literal.slice(1)
                            literal = literal.replace(js("/(\\r?\\n)/g"), "$1        ")
                            literal = "dedent(${literal})"
                            replacements.push(json("from" to btis[0], "oldStringLength" to btis[1] - btis[0] + 1, "newString" to literal))
                        }
                        btis = js("[]")
                    }
                    from = backtickIndex + 1
                }
                replacements = jshit.sortBy(replacements, "from")
                var newActualStringForPasting = ""; from = 0
                for (replacement in jsArrayToIterable(replacements)) {
                    newActualStringForPasting += actualStringForPasting.slice(from, replacement.from) + replacement.newString
                    from = replacement.from + replacement.oldStringLength
                }
                newActualStringForPasting += actualStringForPasting.slice(from)
                actualStringForPasting = newActualStringForPasting
                actualStringForPasting = actualStringForPasting + "\n"

                val actualStringForPastingPlusExt = actualStringForPasting

                actualStringForPasting = actualStringForPasting
                        .split("\n")
                        .filter({line -> !isExtValueLine(line)})
                        .join("\n")

                tabs = jshit.Tabs(json(
                    "activeTab" to if (jshit.isEmpty(expected)) "diffLast" else "diff",
                    "tabs" to json(
                        "diff" to json(
                            "title" to "Diff",
                            "content" to run {
                                var args = js("[]")
                                args.push(json("style" to json("whiteSpace" to "pre-wrap")))
                                args = args.concat(diffDivs)
                                jshit.diva.apply(null, args)
                            }),
                        "diffLast" to json(
                            "title" to "Diff Last",
                            "content" to run {
                                var args = js("[]")
                                args.push(json("style" to json("whiteSpace" to "pre-wrap")))
                                args = args.concat(diffLastDivs)
                                jshit.diva.apply(null, args)
                            }),
                            "actual" to json(
                                "title" to "Actual",
                                "content" to jshit.diva(json("style" to json("whiteSpace" to "pre-wrap")), actualString)),
                            "expected" to json(
                                "title" to "Expected",
                                "content" to jshit.diva(json("style" to json("whiteSpace" to "pre-wrap")), expectedString)),
                            "actualPaste" to json(
                                "title" to "Actual Paste",
                                "content" to jshit.diva(json("style" to json("whiteSpace" to "pre-wrap")),
                                    jshit.Input(json("initialValue" to actualStringForPasting, "kind" to "textarea", "rows" to 10, "style" to json("width" to "100%", "height" to "100%"), "untested" to true)))),
                            "actualPasteWithExt" to json(
                                "title" to "Actual Paste + ext",
                                "content" to jshit.diva(json("style" to json("whiteSpace" to "pre-wrap")),
                                    jshit.Input(json("initialValue" to actualStringForPastingPlusExt, "kind" to "textarea", "rows" to 10, "style" to json("width" to "100%", "height" to "100%"), "untested" to true))))
                    )
                ))

                paneControls = div {
                    -hor2 {
                        -hor1 { -unifyIndicesCheck; -"Unify indices" }
                        -hor1 { -hideKeyRepetitionsCheck; -"Hide key repetitions" }
                        -button {level = "primary"; title = t("Update Assertion Code"); icon = "pencil"; onClick {
                            global.testGlobal.minimalGertrude = true
                            jshit.callDebugRPWithProgress(json("msg" to json("fun" to "danger_updateAssertionCode", "assertionTag" to tag, "actualStringForPasting" to actualStringForPasting), "progressPlaceholder" to progressPlaceholder, "progressTitle" to "Updating assertion code"))
                        }}
                    }
                    -progressPlaceholder
                }
            }

            unifyIndicesCheck = jshit.Checkbox(json("initialValue" to stripFuckingIndices, "onChange" to {
                stripFuckingIndices = unifyIndicesCheck.getValue()
                makeFuckingTabs()
                updateWholeShit()
            }))
            hideKeyRepetitionsCheck = jshit.Checkbox(json("initialValue" to hideFuckingKeyRepetitions, "onChange" to {
                hideFuckingKeyRepetitions = hideKeyRepetitionsCheck.getValue()
                makeFuckingTabs()
                updateWholeShit()
            }))


            makeFuckingTabs()

            global.assertionPane = json(
                "setLineHideFilter" to {f: dynamic ->
                    lineHideFilter = f
                    updateWholeShit()
                },

                "highlightStuff" to {arg: dynamic ->
                    // ({keys, scrollThere})
                    val keys = arg.keys; val scrollThere = arg.scrollThere
                    highlightedKeys = js("({})")
                    for (k in jsArrayToIterable(keys)) {
                        highlightedKeys[k] = true
                    }

                    makeFuckingTabs()
                    updateWholeShit()

                    if (scrollThere && keys.length) {
                        jshit.fov(pileOfShit["scrollToDivForKey-${keys[0]}"])
                    }
                }
            )

            return@wholeShitCtor {
                jshit.art.assertionDetailsWithSourceLink(json("\$tag" to tag, "details" to tabs, "controls" to paneControls,
                    "collapsedDetails" to jshit.updatableElement(js("({})"), {update ->
                        return@updatableElement {
                            jshit.diva(js("({})"),
                                definitionStack && jshit.diva(json("style" to json("marginBottom" to 8)), jshit.renderDefinitionStackStrip(json("stack" to definitionStack))),
                                jshit.art.renderStepDescriptions())
                        }
                    })
                ))
            }

        })
    }

    jshit.art.assert(false, descr, json("scrollThere" to scrollThere, "detailsUI" to detailsUI))
}


fun openTestPassedPane(def: dynamic) {
    val scenario = def.scenario

    val testPassedPane = jshit.statefulElement(json("ctor" to { update: dynamic ->
        var scenarioName: String = scenario.name
        val links = mutableListOf<ReactElement>()

        val m = global.RegExp("\\s+([0-9a-z]{8})-([0-9a-z]{4})-([0-9a-z]{4})-([0-9a-z]{4})-([0-9a-z]{12})$").exec(scenarioName)
        if (m != undefined) {
            scenarioName = scenarioName.substring(0, m.index)
            links.add(jshit.OpenSourceCodeLink(json("where" to json("\$tag" to m[0].trim()), "style" to json("color" to jshit.WHITE))))
        }
        if (jshit.art.actionPlaceholderTag != undefined) {
            links.add(jshit.marginateLeft(10, jshit.OpenSourceCodeLink(json("where" to json("\$tag" to jshit.art.actionPlaceholderTag), "style" to json("color" to jshit.WHITE)))))
        }

        val uq = jshit.getURLQueryBeforeRunningTest()
        if (!uq.scrollToBottom || uq.scrollToBottom == "yes" || uq.scrollToBottom == "success") {
            window.requestAnimationFrame { document.body?.scrollTop = 99999 }
        }

        json(
            "render" to {
                when {
                    scenarioName == undefined -> null
                    else -> div {
                        noStateContributions = true
                        style {
                            backgroundColor = Color.GREEN_700; color = Color.WHITE
                            marginTop(10); padding = "10px 10px"; textAlign = "center"; fontWeight = "bold" }

                        -div { style { paddingBottom(10) }
                            - scenarioName
                            -div { style { display = "flex"; justifyContent = "center" }
                                + links } }

                        -div { style { backgroundColor = Color.WHITE; color = Color.BLACK_BOOT; fontWeight = "normal"; textAlign = "left"; padding(5) }
                            - jshit.art.renderStepDescriptions() } }
                }
            })
    }))

    jshit.debugPanes.set(json(
        "name" to "openTestPassedPane",
        "parentJqel" to jshit.byid("underFooter"),
        "element" to jshit.spana(json(), testPassedPane.element)))
}

fun <T> ifornull(cond: Boolean, f: () -> T): T? {
    return if (cond) f() else null
}

fun renderStepDescriptions(): ReactElement {
    val testInstructions = jshit.art.getTestInstructions()
    val els = mutableListOf<ReactElement>()

    var stepIndex = 0; var indent = 0
    for (instrIndex in 0 until testInstructions.length) {
        val instrdef = testInstructions[instrIndex]
        val opcode = dynamicKeys(instrdef).find { x: dynamic -> x[0] != "$" }
        val instr = instrdef[opcode]

        fun addLine(indent: Int, stepRowStyle: dynamic = null, rulerContent: dynamic = null, lineContent: ReactElement? = null, actions: Collection<ReactElement> = listOf()) {
            els.add(div { style { marginTop(5); display = "flex" }
                -div { style { fontWeight = "bold"; width(40) }; - rulerContent }
                // XXX This `width: 100%` is for fucking flexbox to not change `width: 40` above... http://stackoverflow.com/questions/7985021/css-flexbox-issue-why-is-the-width-of-my-flexchildren-affected-by-their-content
                -div { className = "showOnParentHovered-parent"
                    style {
                        width = "100%"; display = "flex"
                        add(stepRowStyle)
                    }

                    + (1..indent).map { div { style { width(20); borderLeft = "2px dotted ${Color.GRAY_500}" } } }
                    - lineContent
                    -div { className = "showOnParentHovered"
                        - hor2 { style { marginLeft(8); paddingLeft(8); borderLeft = "2px solid ${Color.GRAY_500}" }
                            + actions
                            - jshit.OpenSourceCodeLink(json("where" to instrdef, "style" to json("marginLeft" to 20)))
                        } } } })
        }

        if (opcode == "step") {
            val title: Any? = instr.long
            val untilParamValue = if (instrIndex == jshit.art.stepDescriptions.length - 1) "infinity" else instrIndex

            val stepRowStyle = StyleBuilder()
            if (!instr.fulfilled) stepRowStyle {
                opacity = 0.3
            }

            addLine(
                indent, stepRowStyle = stepRowStyle,
                rulerContent = "#" + (stepIndex++ + 1),

                lineContent = div { style { display = "flex" }
                    - when (instr.kind) {
                        "action" -> span { style { marginRight(5); padding(3); backgroundColor = GREEN_100; fontSize = "75%" }; -"Action" }
                        "state" -> span { style { marginRight(5); padding(3); backgroundColor = LIGHT_BLUE_100; fontSize = "75%" }; -"State" }
                        "navigation" -> span { style { marginRight(5); padding(3); backgroundColor = BROWN_50; fontSize = "75%" }; -"Navigation" }
                        else -> raise("WTF is instr.kind")
                    }
                    - title
                },

                actions = listOf(
                    // TODO:vgrechka @duplication 4dfaa71f-4eaa-4ce9-992f-60f9587f69ae 1
                    link(json("title" to "Run until " + untilParamValue, "onClick" to {
                        var href = window.location.href
                        href = href.replace(Regex("&from[^&]*"), "")
                        href = href.replace(Regex("&until[^&]*"), "")
                        href += "&until=" + untilParamValue
                        window.location.href = href
                    }))
                )
            )
        }
        else if (opcode == "beginSection") {
            addLine(indent, lineContent = div { style { fontWeight = "bold" }; - instr.long })
            ++indent
        }
        else if (opcode == "endSection") {
            --indent
        }
        else if (opcode == "worldPoint") {
            addLine(
                indent,
                lineContent = div { style { fontWeight = "normal"; fontStyle = "italic" }; - "World point: ${instr.name}" },
                rulerContent = jdiva(json("style" to json("position" to "relative")),
                    jshit.ia(json("className" to "fa fa-circle", "style" to json("color" to jshit.GRAY_500))),
                    jdiva(json("style" to json("width" to 38, "position" to "absolute", "left" to 0, "top" to 9, "borderTop" to "2px dotted ${jshit.GRAY_500}")))
                ),
                // TODO:vgrechka @duplication 4dfaa71f-4eaa-4ce9-992f-60f9587f69ae 2
                actions = listOf(
                    link(json("title" to "Run from", "onClick" to {
                        var href = window.location.href
                        href = href.replace(Regex("&from[^&]*"), "")
                        href = href.replace(Regex("&until[^&]*"), "")
                        href += "&from=" + instr.name
                        window.location.href = href
                    }))
                )
            )
        }
    }

//    return makeSwearBoxes().toReactElement()

    return jdiva(json("controlTypeName" to "renderStepDescriptions", "noStateContributions" to true), jdiva(json("style" to json("background" to jshit.GRAY_200, "fontWeight" to "bold")), "Steps"),
        *els.toTypedArray())
}

private fun makeSwearBoxes(): StatefulElement {
    return object : StatefulElement() {
        private var shitVisible: Boolean = true

        val swearBox1 = PromiseAsyncSwearBox {
            controlTypeName = "bobobox"; style { width = "100%" }
            onChange {
                println("Box 1 changed")
            }
        }

        val swearBox2 = PromiseAsyncSwearBox {
            style { width = "100%" }
            onChange {
                println("Box 2 changed")
            }
        }

        override fun render() = div {
            -div {
                -simpleButton(if (shitVisible) "Hide Shit" else "Show Shit") {
                    shitVisible = !shitVisible; update()
                }

                ifornull(shitVisible) {
                    -div {
                        -div {
                            style { display = "flex" }
                            -swearBox1
                            -simpleButton("Reset") {
                                swearBox1.reset()
                            }
                        }

                        -div {
                            style { display = "flex" }
                            -swearBox2
                            -simpleButton("Reset") {
                                swearBox2.reset()
                            }
                        }

                        -div { style { height(500) } }
                    }
                }
            }
        }
    }
}

@native class Promise<T>(f: (resolve: (T) -> Unit, reject: (Throwable) -> Unit) -> Unit) {
    fun <U> then(cb: (T) -> Any?): Promise<U> = noImpl
}

class UnitPromise(f: (resolve: () -> Unit, reject: (Throwable) -> Unit) -> Unit) {
    val promise: Promise<Unit>

    init {
        promise = Promise<Unit> {resolve, reject ->
            f({ resolve(Unit) }, reject)
        }
    }

    fun then(cb: () -> Unit) {
        promise.then<Nothing> { cb() }
    }
}

fun promiseUnit(f: (resolve: () -> Unit, reject: (Throwable) -> Unit) -> Unit): Promise<Unit> {
    return Promise {resolve, reject ->
        f({ resolve(Unit) }, reject)
    }
}

typealias Voidy = () -> Unit

class SimpleEventHandlerBuilder {
    var handler: Voidy? = null

    operator fun invoke(_handler: Voidy) {
        handler = _handler
    }

    fun notify() {
        handler?.invoke()
    }
}

class diva(build: FlowElementBuilder.() -> Unit) : StatefulElement() {
    private val element: ReactElement

    init {
        val builder = FlowElementBuilder("div")
        builder {
            id = elementID
        }
        builder.build()
        element = builder.toElement()
    }

    override fun render(): ReactElement = element
}

class PromiseAsyncSwearBox(build: PromiseAsyncSwearBox.() -> Unit) : StatefulElement() {
    val adjectives = arrayOf("Big", "Little")
    val mainWords = arrayOf("Shit", "Fuck", "Bitch")
    var adjectiveIndex = -1
    var mainWordIndex = -1
    var progressStatus: String? = null
    var phrase: String? = null
    val myStyle = StyleBuilder(); val style = myStyle
    val onChange = SimpleEventHandlerBuilder()

    init {
        controlTypeName = "PromiseAsyncSwearBox"

        // @wip sourceLocation

        build()
    }

    // @wip stuff
    override fun render() = div {id = elementID; style {add(myStyle)}
        -hor2 {
            if (phrase == null)
                -diva {style {color = ROSYBROWN}; -"Kittens"}
            else
                -div {
                    -span {style {fontWeight = "bold"}; -"Back to earth:"}
                    -div {-phrase}
                }

            if (progressStatus != null)
                -div {style {fontStyle = "italic"}; -progressStatus}
        }

        -simpleButton("More") {
            var adjective: String? = null

            generateAdjective()
            .then<String> {_adjective ->
                adjective = _adjective
                generateMainWord()
            }.then<Nothing> {mainWord ->
                phrase = "$adjective $mainWord"
                update()
                onChange.notify()
            }
        }
    }

    fun generateAdjective(): Promise<String> {
        progressStatus = "Thinking adjective..."; update()
        return Promise {resolve, reject ->
            timeoutSet(300) {
                progressStatus = null; update()
                if (++adjectiveIndex > adjectives.lastIndex) adjectiveIndex = 0
                resolve(adjectives[adjectiveIndex])
            }
        }
    }

    fun generateMainWord(): Promise<String> {
        progressStatus = "Thinking main word..."; update()
        return Promise {resolve, reject ->
            timeoutSet(300) {
                progressStatus = null; update()
                if (++mainWordIndex > mainWords.lastIndex) mainWordIndex = 0
                resolve(mainWords[mainWordIndex])
            }
        }
    }

    fun reset() {
        phrase = null; update()
    }

}

fun timeoutSet(ms: Int, cb: () -> Unit) {
    window.setTimeout(cb, ms)
}

@Suppress("UNUSED_PARAMETER")
fun simpleButton(title: String?, onClick: (e: ReactEvent) -> Unit): ReactElement {
    val attrs = js("({onClick: onClick})")
    return React.createElement("button", attrs, title)
}

class ManagedString(val en: String, val ru: String) {
    val meat: String get() = ru // TODO:vgrechka Implement ManagedString.meat    09c55c82-faa1-4734-a45c-5454b755eeab
}

fun t(en: String, ru: String) = ManagedString(en, ru)
fun t(en: String) = ManagedString(en, en)

val nbsp: String = js("String.fromCharCode(0xa0)")

class button(build: button.() -> Unit) : StatefulElement() {
    var icon: String? = null
    var title: ManagedString? = null
    var className = ""
    var level = "default"
    val style = StyleBuilder()
    val iconStyle = StyleBuilder()
    var hint: ManagedString? = null
    var onClick: ReactEventHandler? = null

    fun onClick(handler: ReactEventHandler) {
        onClick = handler
    }

    init {
        build()
    }

    override fun render(): ReactElement = element

    private val element: ReactElement by lazy {
        val attrs = js("({})")
        attrs.id = elementID
        attrs.className = "btn btn-${level} ${className}"
        attrs.style = style.toJSObject()
        attrs.title = hint?.meat
        attrs.onClick = onClick

        React.createElement("button", attrs,
            icon?.let { jshit.glyph(it, json("style" to iconStyle.toJSObject())) },
            if (icon != null && title != null) nbsp else null,
            title?.meat
        )
    }
}

@native interface ReactEvent {
    val ctrlKey: Boolean
    val shiftKey: Boolean

    fun preventDefault()
    fun stopPropagation()
}

fun puid(): Long {
    return jshit.puid()
}

@native val MODE: String = noImpl

fun preventAndStop(e: ReactEvent) {
    e.preventDefault()
    e.stopPropagation()
}

typealias ReactEventHandler = (ReactEvent) -> Unit

abstract class StatefulElement() : ToReactElementable {
    abstract fun render(): ReactElement

    open fun componentWillMount() {}
    open fun componentDidMount() {}
    open fun componentWillUpdate() {}
    open fun componentDidUpdate() {}
    open fun componentWillUnmount() {}

    protected open fun onRootClick(e: ReactEvent) = UnitPromise {resolve, reject ->}
    protected open fun contributeTestState(state: dynamic) {}

    protected open fun getLongRevelationTitle(): String = "// TODO:vgrechka Implement getLongRevelationTitle"

    val id = puid()
    val elementID: String = "" + puid()
    var ignoreDebugCtrlShiftClick: Boolean = false

    val constructionStackAsError: dynamic = js("Error()")
    open val firstSignificantStackLine: Int = 2

    val `$sourceLocation`: Promise<String?> by lazy {
        Promise<String?>({resolve, reject ->
            `$definitionStack`.then<Nothing> {jsArray ->
                resolve(if (jsArray[0]) jsArray[0].loc else null)
            }
        })
    }

    val `$definitionStack`: Promise<dynamic> by lazy {
        Promise<dynamic>({resolve, reject ->
            jshit.errorToMappedClientStackString(constructionStackAsError, json("skipMessage" to true)).then {stackString: String ->
                // @wip sourceLocation
                var lines = stackString.lines()
                lines = lines.slice(firstSignificantStackLine..lines.lastIndex)
                /// println("Liiines"); lines.take(5).forEachIndexed { i, s -> println("$i) $s")}

                val jsArray = js("[]")
                lines.forEach {line ->
                    Regex("\\((.*?\\.kt:\\d+:\\d+)\\)").find(line)?.let {
                        jsArray.push(json(
                            "loc" to it.groupValues[1]
                        ))
                    }
                }
                resolve(jsArray)
            }
        })
    }


    val tattrs = mutableMapOf<String, String>() // TODO
    val noStateContributions: Boolean = false

    private val element: ReactElement
    private var elementThis: dynamic = null

//    fun accessTame(): String {
//        return if (tame != null) tame
//        else "nope"
//    }

//    val debugDisplayName: String by lazy {when{
//        tame != null -> tame
//        else -> "dunno"
////        tame != null -> tame
////        controlTypeName != null -> controlTypeName
////        else -> "dunno"
//    }}

//    val tame: String? by lazy {when{
//        simpleTamy -> tamyPrefix ?: controlTypeName
//        tamy != null -> (tamyPrefix ?: controlTypeName) + "-" + tamy
//        else -> explicitTame
//    }}

//    fun tame(value: String) {
//        explicitTame = value
//    }

    var explicitTame: String? = null
    var tamy: String? = null
    var tamyPrefix: String? = null
    var simpleTamy: Boolean = false
    var controlTypeName: String = "gimme-name"
    var shame: String? = null
    var hasTestManipulationFunctions: Boolean = false

    val effectiveShame: String? = "// TODO:vgrechka Implement effectiveShame"
    val debugDisplayName: String? = "// TODO:vgrechka Implement debugDisplayName"
    val tame: String? = "// TODO:vgrechka Implement tame"

//    val effectiveShame: String? by lazy {when{
//        shame != null -> shame
//        tame != null && hasTestManipulationFunctions -> getTamePath()
//        else -> null
//    }}

    init {
        val def = js("({})")

        def.componentWillMount = {
            elementThis = js("this")

            var elementControls = jshit.elementIDToControls[elementID]
            if (!elementControls) {
                elementControls = js("[]")
                jshit.elementIDToControls[elementID] = elementControls
            }

            if (tame != null) {
                for (another in jsArrayToIterable(elementControls)) {
                    if (another.tame) raise("Control ${debugDisplayName} conflicts with ${another.debugDisplayName}, because both are tamed", json(
                        "\$render" to {
                            "TODO: Implement ce3ace61-41ee-4c31-ac9c-209748b5cc99"
                        }
                    ))
                }
            }

            elementControls.unshift(this)

            componentWillMount()
        }

        def.componentDidMount = {
            addEventListeners()

            jshit.art.uiStateContributions[id] = {state: dynamic ->
                var shouldContribute = !noStateContributions

                if (shouldContribute) {
                    jshit.byid(elementID).parents().each {
                        val parentControls = jshit.elementIDToControls[js("this").id]
                        if (!parentControls) undefined
                        else {
                            for (parentControl in jsArrayToIterable(parentControls)) {
                                if (parentControl.noStateContributions) {
                                    shouldContribute = false
                                    return@each false // break
                                }
                            }
                        }
                    }
                }

                if (shouldContribute) {
                    contributeTestState(state)
                }

                for ((key, value) in tattrs) {
                    if (value != null) {
                        state.put(json("control" to this, "key" to getTamePath() + "." + key, "value" to value)) // TODO Capture source location
                    }
                }

//                if (effectiveShame != null) {
//                    val tp = getTamePath()
//                    if (tp != effectiveShame) {
//                        state.put(json("control" to this, "key" to tp + ".shame", "value" to this.effectiveShame)) // TODO Capture source location
//                    }
//                }
            }

            if (effectiveShame != null) {
                val myEffectiveShame = effectiveShame
                val already = js("Object.keys(testGlobal.controls).includes(myEffectiveShame)")
                if (already) {
                    stickException(global.Error("testGlobal.controls already contains thing shamed ${effectiveShame}"))
                }
                global.testGlobal.controls[effectiveShame] = this
            }

            componentDidMount()
        }

        def.componentWillUpdate = {
            removeEventListeners()
            componentWillUpdate()
        }

        def.componentDidUpdate = {
            addEventListeners()
            componentDidUpdate()
        }

        def.componentWillUnmount = { componentWillUnmount() }
        def.render = { render() }

        val clazz = React.createClass(def)
        element = React.createElement(clazz, js("({})"))
    }

    private fun stickException(error: dynamic) {
        console.error("// TODO:vgrechka Implement stickException    034294e5-6488-4c1c-8d82-49c310862cfe")
    }


    private fun captureAction() {
        console.warn("Implement captureAction, fuck you")
    }

    private fun addEventListeners() {
        fun onClick(e: ReactEvent) {
            if (MODE == "debug" && e.ctrlKey) {
                if (e.shiftKey) {
                    if (ignoreDebugCtrlShiftClick) return

                    preventAndStop(e)

                    if (effectiveShame == null) {
                        jshit.raiseWithMeta(json("message" to "Put some shame on me", "meta" to this)) // TODO:vgrechka meta: me
                    }

                    return captureAction()
                }

                preventAndStop(e)
                return jshit.revealControl(this)
            }

            onRootClick(e)
        }

        removeEventListeners() // Several controls can be on same element, and we don't want to handle click several times
        jshit.byid(elementID).on("click", ::onClick)
    }

    private fun removeEventListeners() {
        jshit.byid(elementID).off()
    }

    protected fun update() {
        elementThis.forceUpdate()
    }

    override fun toReactElement() = element

    fun getTamePath(): String? {
        invariant(tame, "getTamePath can only be called on tamed control", json("\$definitionStack" to getDefinitionStackForJS()))

        var res = tame
        val parents = jshit.byid(elementID).parents()
        parents.each {
            val parentControls = jshit.elementIDToControls[js("this").id]
            if (!parentControls) undefined
            else {
                for (parentControl in jsArrayToIterable(js("parentControls.slice().reverse()"))) {
                    if (parentControl.tame) {
                        res = parentControl.tame + "." + res
                    }
                }
            }
        }

        return res
    }

    private fun getDefinitionStackForJS(): Any? {
        console.error("// TODO:vgrechka Implement getDefinitionStackForJS    eb76b0f9-9924-4bc1-bff3-611466da85d5")
        return null
    }

}

fun invariant(cond: dynamic, msg: String, props: dynamic = null) {
    jshit.invariant(cond, msg, props)
}

fun jsArrayToIterable(arr: dynamic): Iterable<dynamic> {
    val list = mutableListOf<dynamic>()
    for (i in 0 until arr.length)
        list.add(arr[i])
    return list
}

fun raise(msg: String, props: dynamic = undefined) {
    jshit.raise(msg, props)
}

@native val React: IReact = noImpl

@native interface IReact {
    fun createElement(tag: dynamic, attrs: dynamic, vararg children: dynamic): ReactElement
    fun createClass(def: dynamic): dynamic
}

@native interface ReactElement {
}

private fun asReactElement(x: Any?): ReactElement {
    val didi: dynamic = x
    return didi
}

fun div(doInsideBuilder: FlowElementBuilder.() -> Unit): ReactElement {
    val builder = FlowElementBuilder("div")
    builder.doInsideBuilder()
    return builder.toElement()
}

fun horizontala(doInsideBuilder: HorizontalaBuilder.() -> Unit): ReactElement {
    val builder = HorizontalaBuilder()
    builder.doInsideBuilder()
    return builder.toElement()
}

fun hor2(doInsideBuilder: HorizontalaBuilder.() -> Unit): ReactElement {
    return horizontala {
        spacing = 8
        doInsideBuilder()
    }
}

fun hor1(doInsideBuilder: HorizontalaBuilder.() -> Unit): ReactElement {
    return horizontala {
        spacing = 4
        doInsideBuilder()
    }
}

class HorizontalaBuilder() : FlowElementBuilder("div") {
    var spacing: Int = 0

    init {
        style { display = "flex" }
    }

    override fun transformChildBeforeAddition(child: ReactElement) =
        div { style { marginLeft(if (children.isEmpty()) 0 else spacing) }
            - child
        }
}

// TODO:vgrechka Make PhraseElementBuilder    cb5b0102-4159-4080-8d06-c324d1cf2e08
fun span(doInsideBuilder: FlowElementBuilder.() -> Unit): ReactElement {
    val builder = FlowElementBuilder("span")
    builder.doInsideBuilder()
    return builder.toElement()
}

open class FlowElementBuilder(val tag: String) {
    private val attrs = mutableMapOf<String, Any>()
    protected val children = mutableListOf<ReactElement>()
    val style = StyleBuilder()
    var styleKludge: dynamic = undefined

    var noStateContributions: Boolean = false

    var className: String? = null; set(value) { if (value == null) attrs.remove("className") else attrs["className"] = value }
    var id: String? = null; set(value) { if (value == null) attrs.remove("id") else attrs["id"] = value }

    operator fun invoke(insideMe: FlowElementBuilder.() -> Unit) {
        insideMe()
    }

    fun onClick(handler: ReactEventHandler) {
        attrs["onClick"] = handler
    }

    operator fun Iterable<ReactElement>.unaryPlus() {
        for (child in this) add(child)
    }

    fun add(child: ReactElement?) {
        if (child != null) children.add(transformChildBeforeAddition(child))
    }

    fun add(child: ToReactElementable?) {
        add(child?.toReactElement())
    }

    operator fun ReactElement?.unaryMinus() {
        add(this)
    }

    operator fun ToReactElementable?.unaryMinus() {
        add(this)
    }

    operator fun String?.unaryMinus() {
        if (this != null) add(asReactElement(this))
    }

    @Suppress("USELESS_CAST")
    operator fun dynamic.unaryMinus() {
        when {
            this == null -> {}
            this is String -> - (this as String)
            this.`$meta` != null -> - this.meat
            this.`$$typeof` == js("Symbol['for']('react.element')") -> - asReactElement(this)
            this.element && this.element.`$$typeof` == js("Symbol['for']('react.element')") -> - asReactElement(this.element)
            else -> raise("Weird shit in FlowElementBuilder child")
        }
    }

    fun toElement(): ReactElement {
        val theStyle = if (styleKludge != undefined) styleKludge else style.toJSObject()
        val allAttrs = (attrs + ("style" to theStyle)).toJSObject()
        // console.log("allAttrs", allAttrs)
        return React.createElement(tag, allAttrs, *children.toTypedArray())
    }

    open fun transformChildBeforeAddition(child: ReactElement) = child
}

interface ToReactElementable {
    fun toReactElement(): ReactElement
}

@native val console: NativeConsole
@native interface NativeConsole {
    fun log(vararg args: Any?)
    fun warn(vararg args: Any?)
    fun error(vararg args: Any?)
}

fun Map<String, Any?>.toJSObject(): dynamic {
    val obj = js("({})")
    for ((k, v) in this) obj[k] = v
    return obj
}



fun melinda() {
}

//fun melinda(def: dynamic) {
//    val trainName: dynamic = def.trainName; val urlPath: dynamic = def.urlPath; val urlEntityParamName: dynamic = def.urlEntityParamName;
//    val tabDefs: dynamic = def.tabDefs; val defaultActiveTab: dynamic = def.defaultActiveTab; val header: dynamic = def.header;
//    val entityID: dynamic = def.entityID;val entityFun: dynamic = def.entityFun; val itemsFun: dynamic = def.itemsFun;
//    val emptyMessage: dynamic = def.emptyMessage; val plusFormDef: dynamic = def.plusFormDef; val editFormDef: dynamic = def.editFormDef;
//    val aboveItems: dynamic = def.aboveItems; val renderItem: dynamic = def.renderItem; val hasFilterSelect: dynamic = def.hasFilterSelect;
//    val filterSelectValues: dynamic = def.filterSelectValues; val defaultFilter: dynamic = def.defaultFilter
//
//    val plusIcon: dynamic = if (def.plusIcon != undefined) def.plusIcon else "plus"
//    val defaultOrdering: dynamic = if (def.defaultOrdering != undefined) def.defaultOrdering else "desc"
//    val hasSearchBox: dynamic = if (def.hasSearchBox != undefined) def.hasSearchBox else true
//    val hasOrderingSelect: dynamic = if (def.hasOrderingSelect != undefined) def.hasOrderingSelect else true
//    val hasHeaderControls: dynamic = if (def.hasHeaderControls != undefined) def.hasHeaderControls else true
//
//    if (trainName) jshit.beginTrain(json("name" to trainName)); try {
//        var entityRes: dynamic = undefined
//        if (entityFun) {
//            entityRes = await ui.rpcSoft({fun: entityFun, entityID})
//            if (entityRes.error) return showBadResponse(entityRes)
//        }
//
//        val searchString = ui.urlQuery.search
//
//            var filter
//            if (hasFilterSelect) {
//                filter = ui.urlQuery.filter
//                val saneFilters = filterSelectValues.map(x => x.value)
//                if (!saneFilters.includes(filter)) filter = defaultFilter
//            }
//
//        var ordering = ui.urlQuery.ordering
//            if (!['asc', 'desc'].includes(ordering)) ordering = defaultOrdering
//
//        var tabs, activeTab
//        if (tabDefs) {
//            activeTab = ui.urlQuery.tab || defaultActiveTab
//            tabs = ui.tabs({name: 'main', active: activeTab, tabDefs})
//        }
//
//        val itemsReq = s{fun: fov(itemsFun, {activeTab}), entityID, filter, ordering, searchString}
//        val itemsRes = await ui.rpcSoft(itemsReq)
//        if (itemsRes.error) return showBadResponse(itemsRes)
//
//        var items, showEmptyLabel = true,
//        headerControlsVisible = true, headerControlsClass, headerControlsDisabled,
//        cancelForm,
//        plusShit, editShit
//
//        var searchBox, searchBoxInput
//        if (hasSearchBox) {
//            searchBoxInput = Input({
//                tamyShamy: 'search',
//                style: {paddingLeft: 30, width: 160},
//                placeholder: t(`TOTE`, `Поиск...`),
//                disabled: _=> headerControlsDisabled,
//                async onKeyDown(e) {
//                    if (e.keyCode === 13) {
//                        preventAndStop(e)
//                        await applyHeaderControls+({controlToBlink: searchBoxInput})
//                    }
//                }
//            })
//            searchBoxInput.setValueExt({value: itemsRes.actualSearchString, notify: false})
//
//            searchBox = diva({style: {position: 'relative'}},
//                searchBoxInput,
//                faIcon({icon: 'search', style: {position: 'absolute', left: 10, top: 10, color: GRAY_500}}),
//                )
//        }
//
//        var filterSelect
//            if (hasFilterSelect) {
//                filterSelect = Select({
//                    tamyShamy: 'filter', isAction: true, style: {width: 160},
//                    values: filterSelectValues,
//                    initialValue: filter,
//                    disabled: _=> headerControlsDisabled,
//                    async onChange() {
//                        await applyHeaderControls+({controlToBlink: filterSelect})
//                    },
//                })
//
//                filterSelect.setValueExt({value: itemsRes.actualFilter, notify: false})
//            }
//
//        var orderingSelect
//            if (hasOrderingSelect) {
//                orderingSelect = Select({
//                    tamyShamy: 'ordering', isAction: true, style: {width: 160},
//                    values: [{value: 'desc', title: t(`TOTE`, `Сначала новые`)}, {value: 'asc', title: t(`TOTE`, `Сначала старые`)}],
//                    initialValue: ordering,
//                    disabled: _=> headerControlsDisabled,
//                    async onChange() {
//                        await applyHeaderControls+({controlToBlink: orderingSelect})
//                    },
//                })
//
//                orderingSelect.setValueExt({value: itemsRes.actualOrdering, notify: false})
//            }
//
//        async function applyHeaderControls~({controlToBlink}) {
//            setHeaderControlsDisabled(true)
//            controlToBlink.setBlinking(true)
//
//            val urlParamParts = []
//
//            if (urlEntityParamName) {
//                urlParamParts.push(`${urlEntityParamName}=${entityID}`)
//            }
//
//            urlParamParts.push(`filter=${filterSelect.getValue()}`)
//            urlParamParts.push(`ordering=${orderingSelect.getValue()}`)
//
//            val searchString = searchBoxInput.getValue().trim()
//            if (searchString) {
//                urlParamParts.push(`search=${encodeURIComponent(searchString)}`)
//            }
//
//            val url = `${urlPath}?${urlParamParts.join('&')}`
//                await ui.pushNavigate(url)
//
//            setHeaderControlsDisabled(false)
//            controlToBlink.setBlinking(false)
//        }
//
//        if (plusFormDef) {
//            plusShit = makeButtonFormShit(s{name: 'plus', level: 'primary', icon: plusIcon, formDef: plusFormDef})
//        }
//        if (editFormDef) {
//            editShit = makeButtonFormShit(s{name: 'edit', level: 'default', icon: 'edit', formDef: editFormDef})
//        }
//
//
//        function makeButtonFormShit(def) {
//            #extract {name, level, icon, formDef} from def
//
//            var form, formClass
//
//            return {
//                button() {
//                    return button({tamyShamy: name, style: {marginLeft: 0}, level, icon, disabled: headerControlsDisabled, onClick() {
//                        showEmptyLabel = false
//                        setHeaderControlsDisappearing()
//                        formClass = 'aniFadeIn'
//
//                        cancelForm = function() {
//                            setHeaderControlsAppearing()
//                            form = undefined
//                            ui.updatePage()
//                        }
//
//                        form = ui.Form(asn(formDef, {
//                            onCancel: cancelForm,
//                        }))
//
//                        ui.updatePage()
//                    }})
//                },
//
//                form() {
//                    return form && diva({className: formClass, style: {marginBottom: 15}}, form)
//                },
//            }
//        }
//
//        var updateHeaderControls
//
//            ui.setPage(s{
//                header: fov(header, entityRes),
//                body: _=> diva({style: {marginBottom: 15}},
//                tabs,
//                editShit && editShit.form,
//                plusShit && plusShit.form,
//                fov(aboveItems, entityRes),
//                run(function renderItems() {
//                    if (!itemsRes.items.length) {
//                        if (showEmptyLabel) {
//                            return diva({style: {marginTop: 10}}, emptyMessage || spanc({tame: 'nothingLabel', content: t(`TOTE`, `Савсэм ничего нэт, да...`)}))
//                        }
//                        return ''
//                    }
//                    return ui.renderMoreable(s{itemsRes, itemsReq, renderItem,})
//                }),
//                ),
//                headerControls: _=> updatableElement(s{}, update => {
//                updateHeaderControls = update
//                if (!fov(hasHeaderControls, entityRes) || !headerControlsVisible) return
//
//                return _=> hor2({
//                style: {display: 'flex', marginTop: tabDefs ? 55 : 0},
//                className: headerControlsClass},
//
//                searchBox,
//                filterSelect,
//                orderingSelect,
//                editShit && editShit.button,
//                plusShit && plusShit.button,
//                )
//            }),
//
//                onKeyDown(e) {
//                    if (e.keyCode === 27) {
//                        fov(cancelForm)
//                    }
//                }
//            })
//
//
//        function setHeaderControlsDisappearing() {
//            headerControlsVisible = false
//            headerControlsClass = undefined
//        }
//
//        function setHeaderControlsAppearing() {
//            headerControlsVisible = true
//            headerControlsClass = 'aniFadeIn'
//            timeoutSet(500, _=> {
//                headerControlsClass = undefined
//                ui.updatePage()
//            })
//        }
//
//        function setHeaderControlsDisabled(b) {
//            headerControlsDisabled = b
//            updateHeaderControls()
//        }
//
//        function showBadResponse(res) {
//            return ui.setPage(s{
//                header: pageHeader({title: t(`TOTE`, `Облом`)}),
//                body: diva({}, errorBanner(s{content: res.error}))
//            })
//        }
//    } finally { if (trainName) endTrain() }
//}

class StyleBuilder {
    private val attrs = mutableMapOf<String, String>()

    fun toJSObject(): dynamic {
        return attrs.toJSObject()
    }

    operator fun invoke(insideMe: StyleBuilder.() -> Unit) {
        insideMe()
    }

    fun add(other: StyleBuilder?) {
        if (other != null) attrs.putAll(other.attrs)
    }

    var display: String? = null; set(value) { if (value == null) attrs.remove("display") else attrs["display"] = value }
    var fontWeight: String? = null; set(value) { if (value == null) attrs.remove("fontWeight") else attrs["fontWeight"] = value }
    var fontStyle: String? = null; set(value) { if (value == null) attrs.remove("fontStyle") else attrs["fontStyle"] = value }
    var color: Color? = null; set(value) { if (value == null) attrs.remove("color") else attrs["color"] = value.string }
    var backgroundColor: Color? = null; set(value) { if (value == null) attrs.remove("backgroundColor") else attrs["backgroundColor"] = value.string }
    var opacity: Double? = null; set(value) { if (value == null) attrs.remove("opacity") else attrs["opacity"] = "" + value }
    var textAlign: String? = null; set(value) { if (value == null) attrs.remove("textAlign") else attrs["textAlign"] = value }
    var justifyContent: String? = null; set(value) { if (value == null) attrs.remove("justifyContent") else attrs["justifyContent"] = value }

    var marginTop: String? = null; set(value) { if (value == null) attrs.remove("marginTop") else attrs["marginTop"] = value }
    fun marginTop(value: Int) { marginTop = "${value}px" }
    var marginRight: String? = null; set(value) { if (value == null) attrs.remove("marginRight") else attrs["marginRight"] = value }
    fun marginRight(value: Int) { marginRight = "${value}px" }
    var marginBottom: String? = null; set(value) { if (value == null) attrs.remove("marginBottom") else attrs["marginBottom"] = value }
    fun marginBottom(value: Int) { marginBottom = "${value}px" }
    var marginLeft: String? = null; set(value) { if (value == null) attrs.remove("marginLeft") else attrs["marginLeft"] = value }
    fun marginLeft(value: Int) { marginLeft = "${value}px" }
    var paddingTop: String? = null; set(value) { if (value == null) attrs.remove("paddingTop") else attrs["paddingTop"] = value }
    fun paddingTop(value: Int) { paddingTop = "${value}px" }
    var paddingRight: String? = null; set(value) { if (value == null) attrs.remove("paddingRight") else attrs["paddingRight"] = value }
    fun paddingRight(value: Int) { paddingRight = "${value}px" }
    var paddingBottom: String? = null; set(value) { if (value == null) attrs.remove("paddingBottom") else attrs["paddingBottom"] = value }
    fun paddingBottom(value: Int) { paddingBottom = "${value}px" }
    var paddingLeft: String? = null; set(value) { if (value == null) attrs.remove("paddingLeft") else attrs["paddingLeft"] = value }
    fun paddingLeft(value: Int) { paddingLeft = "${value}px" }
    var borderTop: String? = null; set(value) { if (value == null) attrs.remove("borderTop") else attrs["borderTop"] = value }
    fun borderTop(value: Int) { borderTop = "${value}px" }
    var borderRight: String? = null; set(value) { if (value == null) attrs.remove("borderRight") else attrs["borderRight"] = value }
    fun borderRight(value: Int) { borderRight = "${value}px" }
    var borderBottom: String? = null; set(value) { if (value == null) attrs.remove("borderBottom") else attrs["borderBottom"] = value }
    fun borderBottom(value: Int) { borderBottom = "${value}px" }
    var borderLeft: String? = null; set(value) { if (value == null) attrs.remove("borderLeft") else attrs["borderLeft"] = value }
    fun borderLeft(value: Int) { borderLeft = "${value}px" }
    var width: String? = null; set(value) { if (value == null) attrs.remove("width") else attrs["width"] = value }
    fun width(value: Int) { width = "${value}px" }
    var height: String? = null; set(value) { if (value == null) attrs.remove("height") else attrs["height"] = value }
    fun height(value: Int) { height = "${value}px" }
    var fontSize: String? = null; set(value) { if (value == null) attrs.remove("fontSize") else attrs["fontSize"] = value }
    fun fontSize(value: Int) { fontSize = "${value}px" }
    var borderWidth: String? = null; set(value) { if (value == null) attrs.remove("borderWidth") else attrs["borderWidth"] = value }
    fun borderWidth(value: Int) { borderWidth = "${value}px" }
    var margin: String? = null; set(value) { if (value == null) attrs.remove("margin") else attrs["margin"] = value }
    fun margin(value: Int) { margin = "${value}px" }
    var padding: String? = null; set(value) { if (value == null) attrs.remove("padding") else attrs["padding"] = value }
    fun padding(value: Int) { padding = "${value}px" }
}







// ------------------------- Async Playground -------------------------

@native fun <T> __await(x: Promise<T>): T = noImpl
@native fun <T> __asyncResult(x: T): Promise<T> = noImpl

fun testAsyncShit() {"__async"
    println("Begin testAsyncShit")

    val methodRes: Int = __await(SomeClass().someAsyncShit_method())
    println("Async method result: $methodRes")

    val topLevelRes: Int = __await(someAsyncShit_topLevel())
    println("Top-level function result: $topLevelRes")

    someAsyncShit_localContainer()

    println("End testAsyncShit")
}

fun promise10(): Promise<Int> {
    return Promise {resolve, reject ->
        timeoutSet(500) {
            resolve(10)
        }
    }
}

fun promise20(): Promise<Int> {
    return Promise {resolve, reject ->
        timeoutSet(1000) {
            resolve(20)
        }
    }
}

class SomeClass {
    fun someAsyncShit_method(): Promise<Int> {"__async"
        println("Begin someAsyncShit_method")
        val a = __await(promise10())
        println("Got someAsyncShit_method a: $a")
        val b = __await(promise20())
        println("Got someAsyncShit_method b: $b")
        return __asyncResult(a + b)
    }
}

fun someAsyncShit_topLevel(): Promise<Int> {"__async"
    println("Begin someAsyncShit_topLevel")
    val a = __await(promise10())
    println("Got someAsyncShit_topLevel a: $a")
    val b = __await(promise20())
    println("Got someAsyncShit_topLevel b: $b")
    return __asyncResult(a + b)
}

fun someAsyncShit_localContainer() {
    fun someAsyncShit_local(): Promise<Int> {"__async"
        println("Begin someAsyncShit_local")
        val a = __await(promise10())
        println("Got someAsyncShit_local a: $a")
        val b = __await(promise20())
        println("Got someAsyncShit_local b: $b")
        return __asyncResult(a + b)
    }

    someAsyncShit_local().then<Nothing> {res: Int ->
        println("Got result in `then`: $res")
    }
}


















