package aps

import aps.front.*

object fieldSpecs {
    val name = TextFieldSpec("name", t("TOTE", "Имя"), TextFieldType.STRING, minLen = 1, maxLen = 50); val name_testRef = TestRef(name)
    val firstName = TextFieldSpec("firstName", t("TOTE", "Имя"), TextFieldType.STRING, minLen = 1, maxLen = 50); val firstName_testRef = TestRef(firstName)
    val lastName = TextFieldSpec("lastName", t("TOTE", "Фамилия"), TextFieldType.STRING, minLen = 1, maxLen = 50); val lastName_testRef = TestRef(lastName)
    val password = TextFieldSpec("password", t("TOTE", "Пароль"), TextFieldType.PASSWORD, minLen = 6, maxLen = 50); val password_testRef = TestRef(password)
    val passwordInSignInForm = TextFieldSpec("passwordInSignInForm", t("TOTE", "Пароль"), TextFieldType.PASSWORD, minLen = 0, maxLen = Int.MAX_VALUE); val passwordInSignInForm_testRef = TestRef(passwordInSignInForm)
    val email = TextFieldSpec("email", t("TOTE", "Почта"), TextFieldType.EMAIL, minLen = 3, maxLen = 50); val email_testRef = TestRef(email)
    val emailInSignInForm = TextFieldSpec("emailInSignInForm", t("TOTE", "Почта"), TextFieldType.STRING, minLen = 0, maxLen = Int.MAX_VALUE); val emailInSignInForm_testRef = TestRef(emailInSignInForm)
    val profileRejectionReason = TextFieldSpec("profileRejectionReason", t("TOTE", "Причина отказа"), TextFieldType.TEXTAREA, 0, 5000); val profileRejectionReason_testRef = TestRef(profileRejectionReason)
    val banReason = TextFieldSpec("banReason", t("TOTE", "Причина бана"), TextFieldType.TEXTAREA, 0, 5000); val banReason_testRef = TestRef(banReason)
    val adminNotes = TextFieldSpec("adminNotes", t("TOTE", "Заметки админа"), TextFieldType.TEXTAREA, 0, 5000); val adminNotes_testRef = TestRef(adminNotes)
    val phone = TextFieldSpec("phone", t("TOTE", "Телефон"), TextFieldType.PHONE, minLen = 6, maxLen = 50, minDigits = 6); val phone_testRef = TestRef(phone)
    val aboutMe = TextFieldSpec("aboutMe", t("TOTE", "Пара ласковых о себе"), TextFieldType.TEXTAREA, minLen = 1, maxLen = 300); val aboutMe_testRef = TestRef(aboutMe)
    val searchString = TextFieldSpec("searchString", "", TextFieldType.STRING, 0, 50); val searchString_testRef = TestRef(searchString)
    val title = TextFieldSpec("title", t("TOTE", "Название"), TextFieldType.STRING, const.order.minTitleLen, const.order.maxTitleLen); val title_testRef = TestRef(title)
    val documentTitle = TextFieldSpec("documentTitle", t("TOTE", "Тема работы"), TextFieldType.STRING, minLen = 5, maxLen = 100); val documentTitle_testRef = TestRef(documentTitle)
    val details = TextFieldSpec("details", t("TOTE", "Детали"), TextFieldType.TEXTAREA, const.order.minDetailsLen, const.order.maxDetailsLen); val details_testRef = TestRef(details)
    val documentDetails = TextFieldSpec("documentDetails", t("TOTE", "Детали работы (задание)"), TextFieldType.TEXTAREA, minLen = 5, maxLen = 2000); val documentDetails_testRef = TestRef(documentDetails)
    val agreeTerms = CheckboxFieldSpec("agreeTerms"); val agreeTerms_testRef = TestRef(agreeTerms)
    val userState = SelectFieldSpec("userState", t("TOTE", "Статус"), UserState.values()); val userState_testRef = TestRef(userState)
    val numPages = IntFieldSpec("numPages", t("TOTE", "Количество страниц"), min = 1, max = 500); val numPages_testRef = TestRef(numPages)
    val numSources = IntFieldSpec("numSources", t("TOTE", "Количество источников"), min = 0, max = 50); val numSources_testRef = TestRef(numSources)

    object ua {
        val documentType = SelectFieldSpec("documentType", t("TOTE", "Тип документа"), UADocumentType.values()); val documentType_testRef = TestRef(documentType)
    }
}

class TextFieldSpec(
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

class SelectFieldSpec<T>(
    val name: String,
    val title: String,
    val values: Array<T>
) where T : Enum<T>, T : Titled

class IntFieldSpec(
    val name: String,
    val title: String,
    val min: Int,
    val max: Int
)








