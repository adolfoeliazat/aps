/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import into.kommon.*
import org.w3c.dom.events.KeyboardEvent
import kotlin.js.Json
import kotlin.js.json

class InputKey(override val fqn: String) : Fucker(), FQNed

// TODO:vgrechka Fuck, shit... kill this abomination already!
fun jsFacing_Input(legacySpec: Json, key: InputKey? = null) {
    val input = Input(legacySpec, key)
    return input.legacyShit
}

val inputReactClass by lazy {React.createClass(json(
    "render" to render@{
        val self = js("this")

        val kind: String? =  self.props.kind
        val defaultValue: dynamic =  self.props.value
        val onChange: dynamic =  self.props.onChange
        val onMount: dynamic =  self.props.onMount
        val style: dynamic =  self.props.style
        val autoFocus: dynamic =  self.props.autoFocus

        val rest = lodash.omit( self.props, "kind", "value", "onChange", "onMount", "style")

        return@render React.createElement(kind ?: "input", aps.global.Object.assign(json(
            "ref" to "input",
            "defaultValue" to defaultValue,
            "style" to aps.global.Object.assign(json("resize" to "none"), style),
            "autoFocus" to autoFocus,
            "onChange" to {e: dynamic ->
                self.manualValue = e.target.value
                onChange && onChange(e)
            }
        ), rest))
    },

    "componentDidUpdate" to {
        if (js("this").props.value !== js("this").manualValue) {
            aps.global.ReactDOM.findDOMNode(js("this").refs.input).value = js("this").props.value
        }
    },

    "componentDidMount" to {
        js("this").props.onMount && js("this").props.onMount()
    }
))}


class Input(
    val legacySpec: Json, val key: InputKey? = null, val onValueChanged: () -> Unit = {}) : ToReactElementable, Blinkable {
    enum class Kind {INPUT, TEXTAREA}

    companion object {
        val instances = mutableMapOf<InputKey, Input>()

        fun instance(key: InputKey): Input {
            return instances[key] ?: bitch("No Input keyed `${key.fqn}`")
        }

        fun kindToLegacy(kind: Kind): String = when (kind) {
            Input.Kind.INPUT -> "input"
            Input.Kind.TEXTAREA -> "textarea"
        }
    }

    constructor(
        key: InputKey? = null,
        kind: Kind = Kind.INPUT,
        rows: Int = 5,
        style: Style = Style(),
        placeholder: String = "",
        initialValue: String = "",
        autoFocus: Boolean = false,
        volatileDisabled: () -> Boolean = {false},
        onKeyDown: (KeyboardEvent) -> Unit = {},
        onKeyDowna: suspend (KeyboardEvent) -> Unit = {}
    ) : this(json(
        "style" to style.toReactStyle(),
        "placeholder" to placeholder,
        "initialValue" to initialValue,
        "disabled" to volatileDisabled,
        "onKeyDown" to onKeyDown,
        "kind" to kindToLegacy(kind),
        "rows" to rows
    ), key)
    {
        this.onKeyDown = onKeyDown
        this.onKeyDowna = onKeyDowna
        this.autoFocus = autoFocus
    }

    lateinit var elementID: String
    lateinit var onKeyDown: (KeyboardEvent) -> Unit
    lateinit var onKeyDowna: suspend (KeyboardEvent) -> Unit
    var autoFocus = false

    suspend fun keyDown(e: KeyboardEvent) {
        onKeyDown(e)
        onKeyDowna(e)
    }

    fun LegacyCtor(): dynamic {
        val onChange: dynamic = legacySpec.get("onChange")
        val style: dynamic = legacySpec.get("style")
        val volatileStyle: dynamic = legacySpec.get("volatileStyle")
        val placeholder: dynamic = legacySpec.get("placeholder")
        val disabled: dynamic = legacySpec.get("disabled")
        val rows: dynamic = legacySpec.get("rows") ?: 5
        val kind: dynamic = legacySpec.get("kind") ?: "input"
        val type: dynamic = legacySpec.get("type") ?: "text"
        val initialValue: dynamic = legacySpec.get("initialValue") ?: ""

        onKeyDown = legacySpec.get("onKeyDown").asDynamic() ?: {}
        onKeyDowna = legacySpec.get("onKeyDowna").asDynamic() ?: {async{}}

        val def = aps.global.Object.assign(js("({})"), legacySpec)
        js("delete def.onChange")
        js("delete def.style")
        js("delete def.volatileStyle")
        js("delete def.placeholder")
        js("delete def.onKeyDown")
        js("delete def.disabled")
        js("delete def.rows")
        js("delete def.kind")
        js("delete def.type")
        js("delete def.initialValue")

        return Shitus.statefulElement(statefulElementCtor@{update ->
            var value = initialValue
            var loading = false
            var _isDisabled = false
            var blinker: BlinkerOperations? = null

            var me: dynamic = null
            me = json(
                "render" to render@{
                    var isRenderingDisabled = _isDisabled
                        if (disabled != null) {
                            isRenderingDisabled = shittyFov(disabled)
                        }

                    return@render React.createElement(inputReactClass, json(
                        "id" to me.elementID,
                        "rows" to rows,
                        "placeholder" to textMeat(placeholder),
                        "kind" to kind,
                        "type" to type,
                        "value" to value,
                        "className" to "form-control",
                        "disabled" to isRenderingDisabled,
                        "onClick" to me.onPhysicalClick,
                        "autoFocus" to autoFocus,
                        "style" to aps.global.Object.assign(json(),
                                                            shittyFov(volatileStyle),
                                                            style,
                            // TODO:vgrechka Implement new-style spinner for Input    d39c80df-fc4e-4b83-8318-c79963b6a010
                                                            if (loading) json(
                                "background" to "url('${Preloader.srcDefault32()}') no-repeat right center",
                                "paddingRight" to 36
                            ) else null
                        ),

                        "onChange" to {e: dynamic ->
                            value = e.target.value
                            update()
                            onChange && onChange(e.target.value)
                        },

                        "onKeyDown" to {e: dynamic ->
                            asu {keyDown(e)}
                        }
                    ))
                },

                "contributeTestState" to {state: TestStateContributions ->
                    if (me.tame) {
                        val key = me.getTamePath()
                        state.put(me, key, me.getValue())
                        if (type != "text") {
                            state.put(me, key + ".type", type)
                        }
                        if (kind != "input") {
                            state.put(me, key + ".kind", kind)
                        }
                    }
                },

                "getValue" to { value },
                "setLoading" to {x: dynamic -> loading = x; update() },
                "focus" to { Shitus.byid(me.elementID).focus() },
                "select" to { Shitus.byid(legacySpec.get("id")).select() },
                "setDisabled" to {x: dynamic -> _isDisabled = x; update() },
                "isDisabled" to { _isDisabled },

                "setValue" to {x: dynamic -> async {
                    me.setValueExt(json("value" to x))
                }},

                "setValueExt" to {def: dynamic -> async {
                    val newValue: dynamic = def.value
                    val notify: dynamic = def.notify

                    value = newValue
                    update()
                    if (notify && onChange != null) {
                        await<dynamic>(onChange())
                    }
                }},

                "testSetValue" to {arg: dynamic -> async {
                    val value: dynamic = arg.value
                    if (art.testSpeed == "slow") {
                        me.setValue("")
                        for (len in 1 until value.length) {
                            await<dynamic>(Shitus.delay(50))
                            me.setValue(value.slice(0, len))
                        }
                    } else {
                        me.setValue(value)
                    }
                }},


                "testGetValue" to { me.getValue() },

                "setBlinking" to {b: dynamic -> async {
                    if (b) {
                        blinker = await(effects).blinkOn(Shitus.byid(me.elementID), BlinkOpts(widthCountMargin = false))
                    } else {
                        bang(blinker).unblink()
                        blinker = null
                    }
                }}
            )

            me.renderInRevelationPane = {
                val els = js("[]")
                Shitus.diva(json("style" to json()),
                    Shitus.hor2(json(),
                        Shitus.link(json(
                            "title" to "Capture primary click with only this entered",
                            "onClick" to onClick@{
                                hrss.closeControlRevealer()
                                val primaryButton = getControlByShame("button-primary")
                                if (!primaryButton) return@onClick console.warn("No primary control to click on")

                                var descriptiveName = me.effectiveShame
                                descriptiveName = descriptiveName.replace(aps.global.RegExp("^TextField-"), "")
                                descriptiveName = descriptiveName.replace(aps.global.RegExp("\\.Input\$"), "")
                                primaryButton.captureAction(json(
                                    "includeShames" to jsArrayOf(me.effectiveShame),
                                    "todoActionDescription" to "Entering ${descriptiveName}: TODO"))
                            }
                        ))
                    )
                )
            }

            me.componentDidMount = {
                if (key != null) {
                    instances[key] = this
                }
            }

            me.componentWillUnmount = {
                if (key != null) {
                    instances.remove(key)
                }
            }

            me.controlTypeName = "Input"
            legacy_implementControlShit(json("me" to me, "def" to def, "implementTestKeyDown" to json("onKeyDown" to onKeyDown)))
            elementID = me.elementID
            return@statefulElementCtor me
        })
    }

    val legacyShit = LegacyCtor()

    override fun toReactElement(): ReactElement {
        return asReactElement(legacyShit.element)
    }

    fun getValue(): String {
        return legacyShit.getValue()
    }

    fun setValue(value: String) {
        legacyShit.setValue(value)
        onValueChanged()
    }

    suspend fun testSetValue(newValue: String, handOpts: HandOpts = HandOpts()) {
        testSetValueAlgo(
            initialValue = "",
            subsequentValues = (0 until newValue.length).map {newValue.substring(0, it + 1)},
            finalValue = newValue,
            handOpts = handOpts)
    }

    suspend fun testPrependValue(prefix: String, handOpts: HandOpts = HandOpts()) {
        testSetValueAlgo(
            initialValue = value,
            subsequentValues = (1..prefix.length).map {prefix.substring(0, it) + value},
            finalValue = prefix + value,
            handOpts = handOpts)
    }

    suspend fun testAppendValue(suffix: String, handOpts: HandOpts = HandOpts()) {
        testSetValueAlgo(
            initialValue = value,
            subsequentValues = (1..suffix.length).map {value + suffix.substring(0, it)},
            finalValue = value + suffix,
            handOpts = handOpts)
    }

    private suspend fun testSetValueAlgo(
        initialValue: String,
        subsequentValues: List<String>,
        finalValue: String,
        handOpts: HandOpts
    ) {
        if (testOpts().animateUserActions) {
            await(TestUserActionAnimation.hand(
                legacyShit.elementID,
                handOpts.copy(direction = HandDirection.RIGHT),
                doWhileHandVisible = {
                    async {
                        setValue(initialValue)
                        for (newValue in subsequentValues) {
                            setValue(newValue)
                            await(delay(50))
                        }
                    }
                }
            ))
        } else {
            setValue(finalValue)
        }
    }

    fun isDisabled(): Boolean {
        return legacyShit.isDisabled()
    }

    fun setDisabled(b: Boolean) {
        legacyShit.setDisabled(b)
    }

    fun focus() {
        legacyShit.focus()
    }

    val value: String get() = getValue()

    override fun setBlinking(b: Boolean) {
        legacyShit.setBlinking(b)
    }

}

suspend fun inputSetValue(field: TestRef<TextFieldSpec>, value: String) {
    inputSetValue(FieldSpecToCtrlKey[field.it], value)
}

suspend fun inputSetValue(field: TestRef<IntFieldSpec>, value: String) {
    inputSetValue(FieldSpecToCtrlKey[field.it], value)
}

suspend fun inputSetValue(key: InputKey, value: String) {
    Input.instance(key).testSetValue(value)
}

suspend fun inputPrependValue(key: InputKey, value: String) {
    Input.instance(key).testPrependValue(value)
}

suspend fun inputPrependValue(field: TestRef<TextFieldSpec>, value: String) {
    inputPrependValue(FieldSpecToCtrlKey[field.it], value)
}

suspend fun inputAppendValue(key: InputKey, value: String) {
    Input.instance(key).testAppendValue(value)
}

//fun TestScenarioBuilder.inputAppendShitToExceedLength(key: String, maxLen: Int) {
//    act("Appending long shit to `$key`") {
//        val inst = Input.instance(key)
//        var newValue = inst.getValue()
//        var i = 0
//        while (newValue.length <= maxLen)
//            newValue += " SHIT ${++i}"
//        inst.setValue(newValue)
//    }
//}

//fun TestScenarioBuilder.inputPrependValue(key: String, value: String) {
//    act("Prepending to `$key`: ${markdownItalicVerbatim(value)}") {
//        val inst = Input.instance(key)
//        inst.setValue(value + inst.getValue())
//    }
//}

fun TestScenarioBuilder.inputPressEnter(key: String) {
    imf("reimplement inputPressEnter")
//    act("Pressing Enter in `$key`") {
//        Input.instance(key).keyDown(json(
//            "keyCode" to 13,
//            "preventDefault" to {},
//            "stopPropagation" to {}
//        ).asDynamic())
//    }
}













