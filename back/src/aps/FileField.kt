package aps

import into.kommon.*

@Back class FileField(
    container: RequestMatumba,
    name: String,
    @Dummy val title: String
): FormFieldBack(container, name) {

    lateinit var value: String

    override fun loadOrBitch(input: Map<String, Any?>, fieldErrors: MutableList<FieldError>) {
        imf()
//        value = (input[name] ?: bitch("Gimme $name, motherfucker")) as String
//        value = value.trim()
//
//        run error@{
//            if (value.length < minLen)
//                return@error if (value.length == 0) t("TOTE", "Поле обязательно")
//                else t("TOTE", "Не менее $minLen символов")
//            if (value.length > maxLen) return@error t("TOTE", "Не более $maxLen символов")
//
//            if (type == TextFieldType.PHONE) {
//                var digitCount = 0
//                for (c in value.toCharArray()) {
//                    if (!Regex("(\\d| |-|\\+|\\(|\\))+").matches("$c")) return@error t("TOTE", "Странный телефон какой-то")
//                    if (Regex("\\d").matches("$c")) ++digitCount
//                }
//
//                if (digitCount < minDigits) return@error t("TOTE", "Не менее $minDigits цифр")
//            }
//
//            null
//        }?.let {error ->
//            return fieldErrors.add(FieldError(name, error)) /ignore
//        }
    }

}


