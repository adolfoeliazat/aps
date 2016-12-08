package aps

import aps.front.*
import aps.front.Color.*

@Front class DateTimeField(
    container: RequestMatumba,
    name: String,
    val title: String
) : FormFieldFront(container, name) {

    override var error: String? = null

    val picker = object:Control2(Attrs()) {
        val pickerID = puid()

        override fun render(): ToReactElementable {
            return kdiv(className = "form-group"){o->
                o- kdiv(id = pickerID, className = "input-group date"){o->
                    o- React.createElement("input", json("type" to "text", "className" to "form-control"))
                    o- kspan(className = "input-group-addon"){o->
                        o- kspan(className = "glyphicon glyphicon-calendar")
                    }
                }
            }
        }

        override fun componentDidMount() {
            byid(pickerID).asDynamic().datetimepicker()
        }
    }

    override fun render(): ReactElement {
        return kdiv(className = "form-group"){o->
            o- klabel {it-title}
            o- kdiv(position = "relative"){o->
                o- picker
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


