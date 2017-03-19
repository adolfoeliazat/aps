@file:Suppress("UnsafeCastFromDynamic")

package aps

import aps.front.*
import kotlin.js.Json
import kotlin.js.json

@Front class CheckboxField(container: RequestMatumba, spec: CheckboxFieldSpec) : FormFieldFront(container, spec.name) {
    override var error: String? = null

    val checkbox = jsFacing_Checkbox(json("tamy" to true), key = FieldSpecToCtrlKey[spec])

    override fun render(): ReactElement {
        return Shitus.diva(json("controlTypeName" to "AgreeTermsField", "tame" to "AgreeTermsField", "className" to "form-group",
                                "style" to json(
                                    "marginBottom" to if (error != null) 0 else null
                                )),
                           Shitus.diva(json("style" to json("display" to "flex")),
                                       checkbox,
                                       Shitus.diva(json("style" to json("width" to 5))),
                                       Shitus.diva(json(),
                                                   Shitus.spanc(json("tame" to "prose", "content" to t("I’ve read and agreed with ", "Я прочитал и принял "))),
                                                   Shitus.link(json("tamy" to true, "title" to t("terms and conditions", "соглашение"), "onClick" to {this.popupTerms()}))
                                       ),
                                       if (error != null) Shitus.diva(json("style" to json("width" to 15, "height" to 15, "borderRadius" to 10, "marginTop" to 3, "marginRight" to 9, "marginLeft" to "auto", "backgroundColor" to Color.RED_300))) else null
                           ),
                           if (error != null) errorLabelOld(json("name" to "agreeTerms", "title" to error, "style" to json("marginTop" to 5, "marginRight" to 9, "textAlign" to "right"))) else null
        )
    }

    var value: Boolean
        get() {
            check(include){"Attempt to read front CheckboxField $name, which is not included    b9dbbbad-e7b9-4ede-99ee-55c8cf0ff792"}
            return checkbox.getValue()
        }
        set(value) {
            check(include){"Attempt to write front CheckboxField $name, which is not included    9de83885-b46a-40bf-88e8-423fd6c3e723"}
            checkbox.setValue(value)
            Globus.populatedFields += this
        }

    override var disabled: Boolean
        get() = checkbox.isDisabled()
        set(value) { checkbox.setDisabled(value) }

    override fun focus() = dwarn("Need focus() for CheckboxField?")

    fun popupTerms() {
        aps.global.alert("No fucking terms. You can go crazy with this shit...")
    }

    override fun populateRemote(json: Json): Promisoid<Unit> = async {
        json[name] = value
    }
}






