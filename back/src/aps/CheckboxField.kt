package aps

@Back class CheckboxField(
    container: RequestMatumba,
    name: String
) : FormFieldBack(container, name) {
    lateinit var _yes: java.lang.Boolean

    val yes: Boolean get() = _yes.booleanValue()
    val no: Boolean get() = !yes

    override fun loadOrBitch(input: Map<String, Any?>, fieldErrors: MutableList<FieldError>) {
        _yes = input[name] as java.lang.Boolean
    }
}


