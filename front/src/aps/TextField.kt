@file:Suppress("UnsafeCastFromDynamic")

package aps

import aps.front.*
import kotlin.js.Json
import kotlin.js.json

@Front class TextField(
    container: RequestMatumba,
    spec: TextFieldSpec
): FormFieldFront(container, spec.name) {
    val _spec = spec

    override var error: String? = null

    private val c = css.textField
    private fun String.div(block: (ElementBuilder) -> Unit) = kdiv(Attrs(className = this), Style(), block)

    val input: Input by lazy {Input(
        json(
            "type" to when (spec.type) {
                TextFieldType.PASSWORD -> "password"
                else -> "text"
            },
            "kind" to when (spec.type) {
                TextFieldType.TEXTAREA -> "textarea"
                else -> "input"
            },
            "volatileStyle" to {
                if (error != null) json("paddingRight" to 30)
                else undefined
            }
        ),
        key = name + container.fieldInstanceKeySuffix,
        onValueChanged = {
            testHint.update()
        })}

    var value: String
        get() = input.getValue()
        set(value) { input.setValue(value) }

    override var disabled: Boolean
        get() = input.isDisabled()
        set(value) { input.setDisabled(value) }

    override fun focus() = input.focus()

    override fun render(): ReactElement {
        return kdiv(className = "form-group", marginBottom = if (error != null) 0 else null){o->
            o- c.labelContainer.div{o->
                o- testHint
                o- klabel {it-_spec.title}
            }
            o- kdiv(position = "relative"){o->
                o- input
                if (error != null) {
                    o- oldShitAsToReactElementable(errorLabelOld(json(
                        "name" to name,
                        "title" to error,
                        "style" to json(
                            "marginTop" to 5,
                            "marginRight" to 9,
                            "textAlign" to "right"))))
                    o- oldShitAsToReactElementable(Shitus.diva(json(
                        "style" to json(
                            "width" to 15,
                            "height" to 15,
                            "backgroundColor" to Color.RED_300,
                            "borderRadius" to 10,
                            "position" to "absolute",
                            "right" to 8 + ifOrZero(byid0ForSure(input.elementID).hasScrollbar()) {fconst.scrollbarWidth},
                            "top" to 10))))
                }
            }

        }.toReactElement()
    }

    private val testHint = Control2.from {when{
        isTest() && _spec.type == TextFieldType.PASSWORD ->
            c.labelContainerTestHint.div{o->
                o- input.value
            }
        else -> NOTRE
    }}

    override fun populateRemote(json: Json): Promisoid<Unit> = async {
        json[name] = value
    }
}


