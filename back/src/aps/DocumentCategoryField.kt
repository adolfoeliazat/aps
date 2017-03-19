package aps

import aps.back.*
import kotlin.properties.Delegates
import kotlin.properties.Delegates.notNull

@Back class DocumentCategoryField(container: RequestMatumba, val spec: DocumentCategoryFieldSpec)
    : FormFieldBack(container, spec.name)
{
    private var _value by notNull<UADocumentCategory>()

    val value: UADocumentCategory get() {
        check(include){"Attempt to read back DocumentCategoryField $name, which is not included"}
        RequestGlobus.retrievedFields += this
        return _value
    }

    override fun loadOrBitch(input: Map<String, Any?>, fieldErrors: MutableList<FieldError>) {
        val stringID = (input[name] ?: bitch("Gimme $name, motherfucker")) as String
        val cat = uaDocumentCategoryRepo.findOne(stringID.toLong())
        if (cat == null) {
            fieldErrors += FieldError(name, t("TOTE", "Я не признаю такую категорию"))
            return
        }

        _value = cat
    }
}


