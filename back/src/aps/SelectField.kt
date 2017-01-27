package aps

@Back class SelectField<T>(
    container: RequestMatumba,
    val spec: SelectFieldSpec<T>
) : FormFieldBack(container, spec.name)
where T : Enum<T>, T : Titled {
    lateinit var value: T

    override fun loadOrBitch(input: Map<String, Any?>, fieldErrors: MutableList<FieldError>) {
        value = spec.values.find{it.name == input[name] as String}!!
    }
}


