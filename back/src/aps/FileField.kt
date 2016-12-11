package aps

import into.kommon.*
import java.io.File
import java.util.*

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
            val testFileOnServerPath: String? = cast(value["testFileOnServerPath"])
            base64 =
                if (testFileOnServerPath != null)
                    Base64.getEncoder().encodeToString(File(testFileOnServerPath).readBytes())
                else
                    cast(value["base64"])
            null
        }?.let {error ->
            fieldErrors.add(FieldError(name, error))
        }
    }
}


