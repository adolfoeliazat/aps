package aps

@Back class SelectField<T>(
    container: RequestMatumba,
    name: String,
    val title: String,
    val values: Array<T>
) : FormFieldBack(container, name) where T : Enum<T> {
    lateinit var value: T

    override fun loadOrBitch(input: Map<String, Any?>, fieldErrors: MutableList<FieldError>) {
        value = values.find{it.name == input[name] as String}!!
    }
}


