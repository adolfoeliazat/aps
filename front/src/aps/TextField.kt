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
    ), name)

    var value: String
        get() = input.getValue()
        set(value) { input.setValue(value) }

    override var disabled: Boolean
        get() = input.isDisabled()
        set(value) { input.setDisabled(value) }

    override fun focus() = input.focus()

    override fun render(): ReactElement {
        return kdiv(className = "form-group", marginBottom = if (error != null) 0 else null){o->
            o- klabel {it-title}
            o- kdiv(position = "relative"){o->
                o- oldShitAsToReactElementable(input.element)
                if (error != null) {
                    o- oldShitAsToReactElementable(errorLabelOld(json("name" to name, "title" to error, "style" to json("marginTop" to 5, "marginRight" to 9, "textAlign" to "right"))))
                    o- oldShitAsToReactElementable(Shitus.diva(json("style" to json("width" to 15, "height" to 15, "backgroundColor" to Color.RED_300, "borderRadius" to 10, "position" to "absolute", "right" to 8, "top" to 10))))
                }
            }

        }.toReactElement()

//        return Shitus.diva(json("controlTypeName" to "TextField", "className" to "form-group",
//                                "style" to json(
//                                    "marginBottom" to if (error != null) 0 else null
//                                )),
//                           if (title != null) Shitus.labela(json(), Shitus.spanc(json("content" to title))) else undefined,
//                           Shitus.diva(json("style" to json("position" to "relative")),
//                                       input,
//                                       if (error != null) errorLabelOld(json("name" to name, "title" to error, "style" to json("marginTop" to 5, "marginRight" to 9, "textAlign" to "right"))) else undefined,
//                                       if (error != null) Shitus.diva(json("style" to json("width" to 15, "height" to 15, "backgroundColor" to Color.RED_300, "borderRadius" to 10, "position" to "absolute", "right" to 8, "top" to 10))) else undefined))
    }

    override fun populateRemote(json: Json): Promise<Unit> = async {
        json[name] = value
    }
}


