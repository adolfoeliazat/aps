@file:Suppress("UnsafeCastFromDynamic")

package aps

import aps.front.*
import aps.Color.*
import into.kommon.*

@native interface Moment {
    fun valueOf(): Int
    fun format(fmt: String): String
}

@native interface EonasdanPicker {
    fun destroy()
    fun date(): Moment?
    fun date(moment: Moment?)
    fun date(s: String)
    fun viewDate(s: String)
    fun locale(name: String)
}

class DateTimePicker(val key: String? = null) : Control2(Attrs()) {
    companion object {
        val instances = mutableMapOf<String, DateTimePicker>()

        fun instance(key: String): DateTimePicker {
            return instances[key] ?: bitch("No DateTimePicker keyed `$key`")
        }
    }

    val pickerID = puid()
    lateinit var eonasdan: EonasdanPicker
    var dateBeforeRemountOrUpdate: Moment? = null

    fun setValue(s: String) {
        eonasdan.date(s)
        eonasdan.viewDate(s)
    }

    override fun render(): ToReactElementable {
        return kdiv(id = pickerID, className = "input-group date"){o->
            o- React.createElement("input", json("type" to "text", "className" to "form-control"))
            o- kspan(className = "input-group-addon"){o->
                o- kspan(className = "glyphicon glyphicon-calendar")
            }
        }
    }

    override fun componentDidUpdate() {
        initEonasdan()
    }

    override fun componentWillUpdate() {
        uninitEonasdan()
    }

    override fun componentDidMount() {
        initEonasdan()
        if (key != null) {
            instances[key] = this
        }
    }

    override fun componentWillUnmount() {
        uninitEonasdan()
        if (key != null) {
            instances.remove(key)
        }
    }

    private fun initEonasdan() {
        byid(pickerID).asDynamic().datetimepicker()
        eonasdan = byid(pickerID).asDynamic().data("DateTimePicker")
        gloshit.eonasdan = eonasdan
        eonasdan.locale(when (Globus.lang) {
                            Language.EN -> "en"
                            Language.UA -> "ru"
                        })
        eonasdan.date(dateBeforeRemountOrUpdate)
    }

    private fun uninitEonasdan() {
        dateBeforeRemountOrUpdate = eonasdan.date()
        eonasdan.destroy()
    }

}

@Front class DateTimeField(
    container: RequestMatumba,
    name: String,
    val title: String,
    key: String = name
) : FormFieldFront(container, name) {

    override var error: String? = null
    val picker = DateTimePicker(key)

    override fun render(): ReactElement {
        gloshit.popo = {populateRemote(json())}
        gloshit.updatePicker = {picker.update()}
        return kdiv(className = "form-group", marginBottom = if (error != null) 0 else null){o->
            o- klabel {it-title}
            o- kdiv(position = "relative"){o->
                o- picker
                o. maybeFieldError(error, circleRight = 48)
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
        val moment = picker.eonasdan.date()
        json[name] =
            if (moment == null) {
                null
            } else {
                json[name] = moment.valueOf()
            }
    }

}


