package aps

import aps.back.*
import kotlin.properties.Delegates.notNull

@Back class CheckboxField(
    container: RequestMatumba,
    val spec: CheckboxFieldSpec
) : FormFieldBack(container, spec.name) {
    private var _value by notNull<Boolean>()

    val value: Boolean get() {
        check(include){"Attempt to read back CheckboxField $name, which is not included"}
        backPlatform.requestGlobus.retrievedFields += this
        return _value
    }

    val yes get()= value
    val no get()= !value

    override fun loadOrBitch(input: Map<String, Any?>, fieldErrors: MutableList<FieldError>) {
        _value = input[name] as Boolean
        spec.mandatoryYesError?.let {
            if (no)
                fieldErrors += FieldError(name, it)
        }
    }
}


