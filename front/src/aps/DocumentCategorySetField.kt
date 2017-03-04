package aps

import aps.front.*
import into.kommon.*
import kotlin.js.Json
import kotlin.properties.Delegates.notNull

@Front class DocumentCategorySetField(container: RequestMatumba, val spec: DocumentCategorySetFieldSpec): FormFieldFront(container, spec.name) {
    override var disabled = false
    override fun focus() {}
    override var error: String? = null

    private var value by notNull<DocumentCategorySetFieldValue>()
    private val fuckingPlace = Placeholder()
    private var selena by notNullOnce<Selena>()
    private val allCheck = Checkbox(
        key = checkboxes.allCategories,
        titleControl = hor3{o->
            o- t("TOTE", "В любых, я умный шописец")
            o- span(t("TOTE", "(Будет приходить куча спама, по всем заказам)"), Style(fontStyle = "italic"))
        },
        onChange = {
            fuck()
        })

    private fun fuck() {
        fuckingPlace.setContent(
            when {
                allCheck.getValue() -> NOTRE
                else -> span("pizda")
            })
    }

    fun setValue(value: DocumentCategorySetFieldValue) {
        check(include){"Attempt to write front DocumentCategorySetField $name, which is not included    1412eeea-fa39-4a15-a793-13bb8bb2c1a7"}
        this.value = value
        allCheck.setValue(value is DocumentCategorySetFieldValue.All)
        fuck()
        Globus.populatedFields += this
    }

    override fun render() =
        kdiv(className = "form-group"){o->
            o- label(spec.title)
            o- kdiv{o->
                o- allCheck
            }
            o- fuckingPlace
        }.toReactElement()

    override fun populateRemote(json: Json): Promisoid<Unit> = async {
        imf("389f87fd-36d3-4ad9-b130-02936b633341")
//        json[name] = selena.getValue().id.toString()
    }
}


