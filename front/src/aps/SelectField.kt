@file:Suppress("UnsafeCastFromDynamic")

package aps

import aps.front.*

@Front class SelectField<T>(
    container: RequestMatumba,
    name: String,
    val title: String,
    val values: Array<T>
) : FormFieldFront(container, name)
where T : Enum<T>, T : Titled {

    override var error: String? = null

    val select = Select(key = null, attrs = Attrs(), values = values, initialValue = null,
                        onChange = {
                            form.fieldChanged()
                        },
                        onFocus = {
                            form.fieldFocused(this)
                        },
                        onBlur = {
                            form.fieldBlurred(this)
                        }
    )

    override fun render(): ReactElement {
        return Shitus.diva(json("controlTypeName" to "SelectField", "tamy" to name, "className" to "form-group",
                                "style" to json(
                                    "marginBottom" to if (error != null) 0 else null
                                )),
            // Can it be null?
                           if (title != null) Shitus.labela(json(), Shitus.spanc(json("tame" to "label", "content" to title))) else null,
                           Shitus.diva(json("style" to json("position" to "relative")),
                                       select.toReactElement(),
                                       if (error != null) errorLabelOld(json("name" to name, "title" to error, "style" to json("marginTop" to 5, "marginRight" to 9, "textAlign" to "right"))) else null,
                                       if (error != null) Shitus.diva(json("style" to json("width" to 15, "height" to 15, "backgroundColor" to Color.RED_300, "borderRadius" to 10, "position" to "absolute", "right" to 8, "top" to 10))) else null))
    }

    var value: T
        get() = select.value
        set(value) { select.setValue(value) }

    override var disabled: Boolean
        get() = select.isDisabled()
        set(value) { select.setDisabled(value) }

    override fun focus() = select.focus()

    override fun populateRemote(json: Json): Promisoid<Unit> = async {
        json[name] = select.value.name
    }

}


