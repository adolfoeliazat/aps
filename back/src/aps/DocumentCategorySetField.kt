package aps

import aps.back.*
import into.kommon.*
import kotlin.properties.Delegates
import kotlin.properties.Delegates.notNull

@Back class DocumentCategorySetField(container: RequestMatumba, val spec: DocumentCategorySetFieldSpec)
    : FormFieldBack(container, spec.name)
{
    private var _value by notNull<DocumentCategorySetFieldValue>()

    val value: DocumentCategorySetFieldValue get() {
        check(include){"Attempt to read back DocumentCategorySetField $name, which is not included"}
        RequestGlobus.retrievedFields += this
        return _value
    }

    override fun loadOrBitch(input: Map<String, Any?>, fieldErrors: MutableList<FieldError>) {
        val s = (input[name] ?: bitch("Gimme $name, motherfucker")) as String
        _value = when {
            s == DocumentCategorySetFieldUtils.allValue -> DocumentCategorySetFieldValue.All()
            else -> {
                s
                imf()
            }
        }
//        val cat = uaDocumentCategoryRepo.findOne(stringID.toLong())
//        if (cat == null) {
//            fieldErrors += FieldError(name, t("TOTE", "Я не признаю такую категорию"))
//            return
//        }
//
//        _value = cat
    }
}


