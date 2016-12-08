@file:Suppress("UnsafeCastFromDynamic")

package aps

import aps.front.*

@Front class IntField(
    container: RequestMatumba,
    name: String,
    val title: String,
    val min: Int,
    val max: Int
): FormFieldFront(container, name) {

    override var error: String? = null

    val input: IKillMeInput = Shitus.Input(json(
        "tamy" to true,
        "type" to "text",
        "kind" to "input",
        "volatileStyle" to {
            if (error != null) json("paddingRight" to 30)
            else undefined
        }
    ))

//    var value: Int
//        get() = imf("IntField value prop") // input.getValue()
//        set(value) { input.setValue("" + value) }

    override var disabled: Boolean
        get() = input.isDisabled()
        set(value) { input.setDisabled(value) }

    override fun focus() = input.focus()

    override fun render(): ReactElement {
        return Shitus.diva(json("controlTypeName" to "IntField", "tamy" to name, "className" to "form-group",
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
        json[name] = input.getValue()
    }
}


