package aps

import aps.front.*
import into.kommon.*
import kotlin.js.Json

@Front class DocumentCategorySetField(container: RequestMatumba, val spec: DocumentCategorySetFieldSpec): FormFieldFront(container, spec.name) {
    override var disabled = false
    override fun focus() {}
    override var error: String? = null

    private var selena by notNullOnce<Selena>()

    fun setValue(value: DocumentCategorySetFieldValue) {
        check(include){"Attempt to write front DocumentCategorySetField $name, which is not included    1412eeea-fa39-4a15-a793-13bb8bb2c1a7"}
//        selena = Selena(initialValue = value, key = FieldSpecToCtrlKey[spec])
        Globus.populatedFields += this
    }

    override fun render() =
        kdiv(className = "form-group"){o->
            o- label(spec.title)
            o- "pizdature"
//            o- selena
        }.toReactElement()

    override fun populateRemote(json: Json): Promisoid<Unit> = async {
        json[name] = selena.getValue().id.toString()
    }
}


