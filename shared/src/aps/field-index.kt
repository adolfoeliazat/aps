package aps

object fields                                    : Fuckers<FieldSpec>(null) {
    object shebang                               : Fuckers<FieldSpec>(this) {
        val documentTitle                        by namedFucker {TextFieldSpec(it, t("TOTE", "Тема работы"), TextFieldType.STRING, minLen = 3, maxLen = 100)}; val documentTitle_testRef = TestRef(documentTitle)
        val anonymousCustomerName                by namedFucker {TextFieldSpec(it, t("TOTE", "Имя"), TextFieldType.STRING, minLen = 1, maxLen = 50)}; val anonymousCustomerName_testRef = TestRef(anonymousCustomerName)
        val firstName                            by namedFucker {TextFieldSpec(it, t("TOTE", "Имя"), TextFieldType.STRING, minLen = 1, maxLen = 50)}; val firstName_testRef = TestRef(firstName)
        val lastName                             by namedFucker {TextFieldSpec(it, t("TOTE", "Фамилия"), TextFieldType.STRING, minLen = 1, maxLen = 50)}; val lastName_testRef = TestRef(lastName)
        val password                             by namedFucker {TextFieldSpec(it, t("TOTE", "Пароль"), TextFieldType.PASSWORD, minLen = 6, maxLen = 50)}; val password_testRef = TestRef(password)
        val passwordInSignInForm                 by namedFucker {TextFieldSpec(it, t("TOTE", "Пароль"), TextFieldType.PASSWORD, minLen = 0, maxLen = Int.MAX_VALUE)}; val passwordInSignInForm_testRef = TestRef(passwordInSignInForm)
        val email                                by namedFucker {TextFieldSpec(it, t("TOTE", "Почта"), TextFieldType.EMAIL, minLen = 3, maxLen = 50)}; val email_testRef = TestRef(email)
        val anonymousCustomerEmail               by namedFucker {email.copy(name = it)}; val anonymousCustomerEmail_testRef = TestRef(anonymousCustomerEmail)
        val emailInSignInForm                    by namedFucker {TextFieldSpec(it, t("TOTE", "Почта"), TextFieldType.STRING, minLen = 0, maxLen = Int.MAX_VALUE)}; val emailInSignInForm_testRef = TestRef(emailInSignInForm)
        val profileRejectionReason               by namedFucker {TextFieldSpec(it, t("TOTE", "Причина отказа"), TextFieldType.TEXTAREA, 0, 5000)}; val profileRejectionReason_testRef = TestRef(profileRejectionReason)
        val banReason                            by namedFucker {TextFieldSpec(it, t("TOTE", "Причина бана"), TextFieldType.TEXTAREA, 0, 5000)}; val banReason_testRef = TestRef(banReason)
        val adminNotes                           by namedFucker {TextFieldSpec(it, t("TOTE", "Заметки админа"), TextFieldType.TEXTAREA, 0, 5000)}; val adminNotes_testRef = TestRef(adminNotes)
        val phone                                by namedFucker {TextFieldSpec(it, t("TOTE", "Телефон"), TextFieldType.PHONE, minLen = 6, maxLen = 50, minDigits = 6)}; val phone_testRef = TestRef(phone)
        val aboutMe                              by namedFucker {TextFieldSpec(it, t("TOTE", "Пара ласковых о себе"), TextFieldType.TEXTAREA, minLen = 1, maxLen = 300)}; val aboutMe_testRef = TestRef(aboutMe)
        val searchString                         by namedFucker {TextFieldSpec(it, "", TextFieldType.STRING, 0, 50)}; val searchString_testRef = TestRef(searchString)
        val orderDetails                         by namedFucker {TextFieldSpec(it, t("TOTE", "Детали работы (задание)"), TextFieldType.TEXTAREA, minLen = 3, maxLen = 2000)}; val orderDetails_testRef = TestRef(orderDetails)
        val agreeTerms                           by namedFucker {CheckboxFieldSpec(it, t("TOTE", "Title is not used here, but should"))}; val agreeTerms_testRef = TestRef(agreeTerms)
        val userState                            by namedFucker {SelectFieldSpec(it, t("TOTE", "Статус"), UserState.values())}; val userState_testRef = TestRef(userState)
        val numPages                             by namedFucker {IntFieldSpec(it, t("TOTE", "Количество страниц"), min = 1, max = 500)}; val numPages_testRef = TestRef(numPages)
        val numSources                           by namedFucker {IntFieldSpec(it, t("TOTE", "Количество источников"), min = 0, max = 50)}; val numSources_testRef = TestRef(numSources)
        val fileFile_create                      by namedFucker {FileFieldSpec(it, t("TOTE", "Файл"), allowedValueKinds = setOf(FileFieldValueKind.PROVIDED))}; val fileFile_create_testRef = TestRef(fileFile_create)
        val fileFile_update                      by namedFucker {fileFile_create.copy(name = it, allowedValueKinds = setOf(FileFieldValueKind.UNCHANGED, FileFieldValueKind.PROVIDED))}; val fileFile_update_testRef = TestRef(fileFile_update)
        val fileTitle                            by namedFucker {TextFieldSpec(it, t("TOTE", "Название"), TextFieldType.STRING, 3, 100)}; val fileTitle_testRef = TestRef(fileTitle)
        val fileDetails                          by namedFucker {TextFieldSpec(it, t("TOTE", "Детали"), TextFieldType.TEXTAREA, 3, 2000)}; val fileDetails_testRef = TestRef(fileDetails)

        object ua                                : Fuckers<FieldSpec>(this) {
            val documentType                     by namedFucker {SelectFieldSpec(it, t("TOTE", "Тип документа"), UADocumentType.values())}; val documentType_testRef = TestRef(documentType)
        }
    }
}

abstract class FieldSpec : Fucker() {
    abstract val name: String
}

data class TextFieldSpec(
    override val name: String,
    val title: String,
    val type: TextFieldType,
    val minLen: Int,
    val maxLen: Int,
    val minDigits: Int = -1
) : FieldSpec()

enum class TextFieldType {
    STRING, TEXTAREA, PASSWORD, PHONE, EMAIL
}

class CheckboxFieldSpec(
    override val name: String,
    val title: String
) : FieldSpec()

class SelectFieldSpec<T>(
    override val name: String,
    val title: String,
    val values: Array<T>
) : FieldSpec() where T : Enum<T>, T : Titled

class IntFieldSpec(
    override val name: String,
    val title: String,
    val min: Int,
    val max: Int
) : FieldSpec()

data class FileFieldSpec(
    override val name: String,
    val title: String,
    val allowedValueKinds: Set<FileFieldValueKind>
) : FieldSpec()

enum class FileFieldValueKind {
    NONE, UNCHANGED, PROVIDED
}







