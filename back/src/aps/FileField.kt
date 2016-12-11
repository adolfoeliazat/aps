package aps

import into.kommon.*

@Back class FileField(
    container: RequestMatumba,
    name: String,
    @Dummy val title: String
): FormFieldBack(container, name) {
    lateinit var fileName: String
    lateinit var base64: String

    override fun loadOrBitch(input: Map<String, Any?>, fieldErrors: MutableList<FieldError>) {
        val value = (input[name] ?: bitch("Gimme $name, motherfucker")) as Map<*, *>?

        run error@{
            if (value == null) return@error t("TOTE", "Файл обязателен")
            fileName = cast(value["fileName"])
            base64 = cast(value["base64"])
            null
        }?.let {error ->
            fieldErrors.add(FieldError(name, error))
        }
    }
}


