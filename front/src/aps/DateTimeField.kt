package aps

import aps.front.*
import aps.front.Color.*

@Front class DateTimeField(
    container: RequestMatumba,
    name: String,
    val title: String
) : FormFieldFront(container, name) {

    override var error: String? = null

    override fun render(): ReactElement {
        return kdiv(className = "form-group"){o->
            o- klabel {it-title}
            o- kdiv(position = "relative"){o->
                o- "fucking calendar here"
                o. maybeFieldError(error)
            }
        }.toReactElement()
    }

//    var value: T
//        get() = select.value
//        set(value) { select.setValue(value) }

    override var disabled: Boolean
        get() = false
        set(value) {}

    override fun focus() {}

    override fun populateRemote(json: Json) {
        json[name] = "pizda"
    }

}


