package aps

object fieldSpecs {
    val firstName = TextFieldSpec("firstName", t("TOTE", "Имя"), TextFieldType.STRING, minLen = 1, maxLen = 50); val firstName_testRef = firstName
    val lastName = TextFieldSpec("lastName", t("TOTE", "Фамилия"), TextFieldType.STRING, minLen = 1, maxLen = 50); val lastName_testRef = lastName
    val password = TextFieldSpec("password", t("TOTE", "Пароль"), TextFieldType.PASSWORD, minLen = 6, maxLen = 50); val password_testRef = password
    val email = TextFieldSpec("email", t("TOTE", "Почта"), TextFieldType.EMAIL, minLen = 3, maxLen = 50); val email_testRef = email
    val profileRejectionReason = TextFieldSpec("profileRejectionReason", t("TOTE", "Причина отказа"), TextFieldType.TEXTAREA, 0, 5000); val profileRejectionReason_testRef = profileRejectionReason
    val banReason = TextFieldSpec("banReason", t("TOTE", "Причина бана"), TextFieldType.TEXTAREA, 0, 5000); val banReason_testRef = banReason
    val adminNotes = TextFieldSpec("adminNotes", t("TOTE", "Заметки админа"), TextFieldType.TEXTAREA, 0, 5000); val adminNotes_testRef = adminNotes
    val phone = TextFieldSpec("phone", t("TOTE", "Телефон"), TextFieldType.PHONE, minLen = 6, maxLen = 20, minDigits = 6); val phone_testRef = phone
    val aboutMe = TextFieldSpec("aboutMe", t("TOTE", "Пара ласковых о себе"), TextFieldType.TEXTAREA, minLen = 1, maxLen = 300); val aboutMe_testRef = aboutMe
    val searchString = TextFieldSpec("searchString", "", TextFieldType.STRING, 0, 50); val searchString_testRef = searchString
    val title = TextFieldSpec("title", t("TOTE", "Название"), TextFieldType.STRING, const.order.minTitleLen, const.order.maxTitleLen); val title_testRef = title
    val details = TextFieldSpec("details", t("TOTE", "Детали"), TextFieldType.TEXTAREA, const.order.minDetailsLen, const.order.maxDetailsLen); val details_testRef = details

    val agreeTerms = CheckboxFieldSpec("agreeTerms"); val agreeTerms_testRef = agreeTerms
}

data class TextFieldSpec(
    val name: String,
    val title: String,
    val type: TextFieldType,
    val minLen: Int,
    val maxLen: Int,
    val minDigits: Int = -1
)

enum class TextFieldType {
    STRING, TEXTAREA, PASSWORD, PHONE, EMAIL
}

data class CheckboxFieldSpec(
    val name: String
)












