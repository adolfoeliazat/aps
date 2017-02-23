package aps

import aps.back.*

@Back class SelectField<T>(
    container: RequestMatumba,
    val spec: SelectFieldSpec<T>
) : FormFieldBack(container, spec.name)
where T : Enum<T>, T : Titled {
    private lateinit var _value: T

    val value: T get() {
        check(include){"Attempt to read back SelectField $name, which is not included"}
        RequestGlobus.retrievedFields += this
        return _value
    }

    override fun loadOrBitch(input: Map<String, Any?>, fieldErrors: MutableList<FieldError>) {
        _value = spec.values.find{it.name == input[name] as String}!!
    }
}


