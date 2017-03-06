package aps

import aps.back.*
import into.kommon.*
import org.springframework.data.repository.findOrDie
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
        val jsonValue = input[name] ?: bitch("Gimme $name, motherfucker")
        _value = when {
            jsonValue == DocumentCategorySetFieldUtils.allValue -> DocumentCategorySetFieldValue.All()
            else -> {
                val stringIDs: List<String> = cast(jsonValue)
                DocumentCategorySetFieldValue.Specific(stringIDs.map {
                    uaDocumentCategoryRepo.findOrDie(it.toLong()).toRTO()
                })
            }
        }
    }
}


