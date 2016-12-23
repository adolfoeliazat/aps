package aps

import into.kommon.*
import java.io.File
import java.util.*
import kotlin.properties.Delegates.notNull

@Back class FileField(
    container: RequestMatumba,
    name: String,
    @Dummy val title: String,
    val shouldBeProvided: Boolean = true
): FormFieldBack(container, name) {
    var provided by notNull<Boolean>()
    lateinit var fileName: String
    lateinit var base64: String

    override fun loadOrBitch(input: Map<String, Any?>, fieldErrors: MutableList<FieldError>) {
        val value = (input[name] ?: bitch("Gimme $name, motherfucker")) as Map<*, *>

        run error@{
            provided = cast(value["provided"])
            if (shouldBeProvided && !provided) return@error t("TOTE", "Файл обязателен")

            if (provided) {
                fileName = cast(value["fileName"])
                val testFileOnServerPath: String? = cast(value["testFileOnServerPath"])
                base64 =
                    if (testFileOnServerPath != null)
                        Base64.getEncoder().encodeToString(File(testFileOnServerPath).readBytes())
                    else
                        cast(value["base64"])
            }
            null
        }?.let {error ->
            fieldErrors.add(FieldError(name, error))
        }
    }
}


