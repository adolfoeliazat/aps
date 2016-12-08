@file:Suppress("UnsafeCastFromDynamic")

package aps

import aps.front.*

@Front class TextField(
    container: RequestMatumba,
    name: String,
    val title: String,
    type: TextFieldType,
    val minLen: Int,
    val maxLen: Int,
    val minDigits: Int = -1
): FormFieldFront(container, name) {

    override var error: String? = null

    val input = Shitus.Input(json(
        "tamy" to true,
        "type" to when (type) {
            TextFieldType.PASSWORD -> "password"
            else -> "text"
        },
        "kind" to when (type) {
            TextFieldType.TEXTAREA -> "textarea"
            else -> "input"
        },
        "volatileStyle" to {
            if (error != null) json("paddingRight" to 30)
            else undefined
        }
    ))

    var value: String
        get() = input.getValue()
        set(value) { input.setValue(value) }

    override var disabled: Boolean
        get() = input.isDisabled()
        set(value) { input.setDisabled(value) }

    override fun focus() = input.focus()

    override fun render(): ReactElement {
        return Shitus.diva(json("controlTypeName" to "TextField", "tamy" to name, "className" to "form-group",
                                "style" to json(
                                    "marginBottom" to if (error != null) 0 else null
                                )),
                           if (title != null) Shitus.labela(json(), Shitus.spanc(json("tame" to "label", "content" to title))) else undefined,
                           Shitus.diva(json("style" to json("position" to "relative")),
                                       input,
                                       if (error != null) errorLabelOld(json("name" to name, "title" to error, "style" to json("marginTop" to 5, "marginRight" to 9, "textAlign" to "right"))) else undefined,
                                       if (error != null) Shitus.diva(json("style" to json("width" to 15, "height" to 15, "backgroundColor" to Color.RED_300, "borderRadius" to 10, "position" to "absolute", "right" to 8, "top" to 10))) else undefined))
    }

    override fun populateRemote(json: Json) {
        json[name] = value
    }
}


