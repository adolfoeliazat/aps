@file:Suppress("UnsafeCastFromDynamic")

package aps

import aps.front.*
import kotlin.browser.window
import kotlin.js.Json
import kotlin.js.json

@Front class IntField(container: RequestMatumba, val spec: IntFieldSpec): FormFieldFront(container, spec.name) {

    override var error: String? = null

    val input: IKillMeInput = Shitus.Input(
        json(
            "type" to "text",
            "kind" to "input",
            "volatileStyle" to {
                json(
                    "paddingRight" to isError().then {30},
                    "height" to inputHeightHackyShittyMicroAdjustment()
                )
            }
        ),
        key = FieldSpecToCtrlKey[spec]
    )

    private fun inputHeightHackyShittyMicroAdjustment(): String? {
        if (hasInputGroupAddon()) {
            return when (window.devicePixelRatio) {
                1.25 -> "34.5px"
                else -> null
            }
        }
        return null
    }

    fun setValue(value: Int) {
        check(include){"Attempt to write front IntField $name, which is not included    cbd9518d-041b-42d5-8475-3cfe879be75c"}
        input.setValue(IntFieldUtils.fromInternal(spec, value).toString())
        Globus.populatedFields += this
    }

    override var disabled: Boolean
        get() = input.isDisabled()
        set(value) { input.setDisabled(value) }

    override fun focus() = input.focus()

    override fun render(): ReactElement {
        return kdiv(className = "form-group",
                    marginBottom = isError().then {0}){o->
            o- label(spec.title)
            o- kdiv(position = "relative"){o->
                val input = oldShitAsToReactElementable(input.asDynamic().element)
                o- when (spec.type) {
                    is IntFieldType.Generic -> input

                    is IntFieldType.Money -> {
                        check(Globus.lang == Language.UA){"0385b8e7-5ae1-4d5e-8f13-23270532064b"}
                        check(!spec.type.fractions){"b3a66650-57dc-4cef-be34-6f62aa4ad762"}
                        renderInputGroup(input, ",00 грн.")
                    }

                    is IntFieldType.Duration -> {
                        renderInputGroup(input, "дн.")
                    }
                }
                if (isError()) {
                    o- kdiv(marginTop = 5, marginRight = 9, textAlign = "right", color = Color.RED_300){o->
                        o- error
                    }
                    o- kdiv(position = "absolute", backgroundColor = Color.RED_300,
                            width = 15, height = 15, borderRadius = 10,
                            right = 8, top = 10)
                }
            }
        }.toReactElement()
    }

    private fun renderInputGroup(input: ToReactElementable, addonText: String): ToReactElementable {
        return kdiv(className = "input-group"){o->
            o- input
            o- kspan(className = "input-group-addon",
                      paddingRight = isError().then {"28px"}){o->
                o- addonText
            }
        }
    }

    override fun populateRemote(json: Json): Promisoid<Unit> = async {
        json[name] = input.getValue()
    }

    private fun isError() = error != null

    private fun hasInputGroupAddon() = when (spec.type) {
        is IntFieldType.Money,
        is IntFieldType.Duration -> true
        else -> false
    }
}


