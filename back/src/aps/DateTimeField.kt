package aps

@Back class DateTimeField(
    container: RequestMatumba,
    name: String,
    val title: String
) : FormFieldBack(container, name) {
//    lateinit var value: T

    override fun loadOrBitch(input: Map<String, Any?>, fieldErrors: MutableList<FieldError>) {
//        value = values.find{it.name == input[name] as String}!!
    }
}
