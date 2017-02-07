package aps

import aps.front.*

object fieldSpecs {
    object shebang                               : NamedGroup<NamedItem>(null) {
        object documentTitle                     : Refs<NamedItem>(this) {val ref = TextFieldSpec(name, t("TOTE", "Тема работы"), TextFieldType.STRING, minLen = 5, maxLen = 100); val testRef = TestRef(ref)}
        object anonymousCustomerName             : Refs<NamedItem>(this) {val ref = TextFieldSpec(name, t("TOTE", "Имя"), TextFieldType.STRING, minLen = 1, maxLen = 50); val testRef = TestRef(ref)}
        object firstName                         : Refs<NamedItem>(this) {val ref = TextFieldSpec(name, t("TOTE", "Имя"), TextFieldType.STRING, minLen = 1, maxLen = 50); val testRef = TestRef(ref)}
        object lastName                          : Refs<NamedItem>(this) {val ref = TextFieldSpec(name, t("TOTE", "Фамилия"), TextFieldType.STRING, minLen = 1, maxLen = 50); val testRef = TestRef(ref)}
        object password                          : Refs<NamedItem>(this) {val ref = TextFieldSpec(name, t("TOTE", "Пароль"), TextFieldType.PASSWORD, minLen = 6, maxLen = 50); val testRef = TestRef(ref)}
        object passwordInSignInForm              : Refs<NamedItem>(this) {val ref = TextFieldSpec(name, t("TOTE", "Пароль"), TextFieldType.PASSWORD, minLen = 0, maxLen = Int.MAX_VALUE); val testRef = TestRef(ref)}
        object email                             : Refs<NamedItem>(this) {val ref = TextFieldSpec(name, t("TOTE", "Почта"), TextFieldType.EMAIL, minLen = 3, maxLen = 50); val testRef = TestRef(ref)}
        object anonymousCustomerEmail            : Refs<NamedItem>(this) {val ref = email.ref.copy(name = name); val testRef = TestRef(ref)}
        object emailInSignInForm                 : Refs<NamedItem>(this) {val ref = TextFieldSpec(name, t("TOTE", "Почта"), TextFieldType.STRING, minLen = 0, maxLen = Int.MAX_VALUE); val testRef = TestRef(ref)}
        object profileRejectionReason            : Refs<NamedItem>(this) {val ref = TextFieldSpec(name, t("TOTE", "Причина отказа"), TextFieldType.TEXTAREA, 0, 5000); val testRef = TestRef(ref)}
        object banReason                         : Refs<NamedItem>(this) {val ref = TextFieldSpec(name, t("TOTE", "Причина бана"), TextFieldType.TEXTAREA, 0, 5000); val testRef = TestRef(ref)}
        object adminNotes                        : Refs<NamedItem>(this) {val ref = TextFieldSpec(name, t("TOTE", "Заметки админа"), TextFieldType.TEXTAREA, 0, 5000); val testRef = TestRef(ref)}
        object phone                             : Refs<NamedItem>(this) {val ref = TextFieldSpec(name, t("TOTE", "Телефон"), TextFieldType.PHONE, minLen = 6, maxLen = 50, minDigits = 6); val testRef = TestRef(ref)}
        object aboutMe                           : Refs<NamedItem>(this) {val ref = TextFieldSpec(name, t("TOTE", "Пара ласковых о себе"), TextFieldType.TEXTAREA, minLen = 1, maxLen = 300); val testRef = TestRef(ref)}
        object searchString                      : Refs<NamedItem>(this) {val ref = TextFieldSpec(name, "", TextFieldType.STRING, 0, 50); val testRef = TestRef(ref)}
        object title                             : Refs<NamedItem>(this) {val ref = TextFieldSpec(name, t("TOTE", "Название"), TextFieldType.STRING, const.order.minTitleLen, const.order.maxTitleLen); val testRef = TestRef(ref)}
        object details                           : Refs<NamedItem>(this) {val ref = TextFieldSpec(name, t("TOTE", "Детали"), TextFieldType.TEXTAREA, const.order.minDetailsLen, const.order.maxDetailsLen); val testRef = TestRef(ref)}
        object documentDetails                   : Refs<NamedItem>(this) {val ref = TextFieldSpec(name, t("TOTE", "Детали работы (задание)"), TextFieldType.TEXTAREA, minLen = 5, maxLen = 2000); val testRef = TestRef(ref)}
        object agreeTerms                        : Refs<NamedItem>(this) {val ref  = CheckboxFieldSpec(name); val testRef = TestRef(ref)}
        object userState                         : Refs<NamedItem>(this) {val ref = SelectFieldSpec(name, t("TOTE", "Статус"), UserState.values()); val testRef = TestRef(ref)}
        object numPages                          : Refs<NamedItem>(this) {val ref = IntFieldSpec(name, t("TOTE", "Количество страниц"), min = 1, max = 500); val testRef = TestRef(ref)}
        object numSources                        : Refs<NamedItem>(this) {val ref = IntFieldSpec(name, t("TOTE", "Количество источников"), min = 0, max = 50); val testRef = TestRef(ref)}

        object ua                                : NamedGroup<NamedItem>(this) {
            object documentType                  : Refs<NamedItem>(this) {val ref = SelectFieldSpec(name, t("TOTE", "Тип документа"), UADocumentType.values()); val testRef = TestRef(ref)}
        }
    }
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








