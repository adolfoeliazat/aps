/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("LeakingThis")

package aps

import aps.Color.*
import aps.front.*
import into.kommon.*
import kotlin.properties.Delegates
import kotlin.properties.Delegates.notNull

class FieldError(val field: String, val error: String)

enum class UserRole {
    SUPPORT
}

enum class UserKind() {
    CUSTOMER, WRITER, ADMIN
}

enum class ClientKind {
    CUSTOMER, WRITER
}

enum class Language(val decimalPoint: String) {
    EN(decimalPoint = "."),
    UA(decimalPoint = ",")
}

interface Titled {
    val title: String
}

enum class UserState(override val title: String) : Titled {
    COOL(t("TOTE", "Прохладный")),
    PROFILE_PENDING(t("TOTE", "Без профиля")),
    PROFILE_APPROVAL_PENDING(t("TOTE", "Ждет аппрува профиля")),
    PROFILE_REJECTED(t("TOTE", "Профиль завернут")),
    BANNED(t("TOTE", "Забанен"));
}

class TimestampRTO(val value: String) {
}


enum class UAOrderState(override val title: String, val labelBackground: Color) : Titled {
    CREATED(t("TOTE", "Создан"), GRAY_300),
    LOOKING_FOR_WRITERS(t("TOTE", "Ищем писателей"), LIGHT_BLUE_100),
    WAITING_FOR_PAYMENT(t("TOTE", "Ждем оплаты"), GRAY_300),
    WRITER_ASSIGNED(t("TOTE", "Писатель назначен"), GRAY_300)
}

val SHITS = "--SHIT--"
val SHITB = false

open class Request {
    var token: String? = null
    var fields = mapOf<String, Any?>()
    lateinit var lang: Language
    lateinit var clientKind: ClientKind
}

open class GenericRequest : RequestMatumba()

class ImposeNextRequestTimestampRequest : RequestMatumba() {
    val stamp = StringHiddenField(this, "stamp")

    companion object {
        fun send(stamp: String): Promise<Unit> = callDangerousMatumba(ImposeNextRequestTimestampRequest().apply {
            this.stamp.value = stamp
        })
    }

//    fun rpc(): Promise<GenericResponse> = callRemoteProcedure(this)
}

//class ImposeNextRequestTimestampRequest(val stamp: String) : Request() {
//    constructor() : this(SHITS) // For fucking Jackson
//    fun rpc(): Promise<GenericResponse> = callRemoteProcedure(this)
//}

class ResetTestDatabaseRequest() : RequestMatumba()

class RecreateTestDatabaseSchemaRequest() : RequestMatumba()

class ResetTestDatabaseAlongWithTemplateRequest() : RequestMatumba() {
    val templateDB = StringHiddenField(this, "templateDB")
    val recreateTemplate = BooleanHiddenField(this, "recreateTemplate")

    companion object {
        fun send(templateDB: String, recreateTemplate: Boolean = false): Promise<GenericResponse> = callDangerousMatumba(ResetTestDatabaseAlongWithTemplateRequest().apply {
            this.templateDB.value = templateDB
            this.recreateTemplate.value = recreateTemplate
        })
    }

//    fun rpc(): Promise<GenericResponse> = callRemoteProcedure(this)
}

class GenericResponse : CommonResponseFieldsImpl()

sealed class ZimbabweResponse<T> {
    class Hunky<T>(val meat: T): ZimbabweResponse<T>()
    class Shitty<T>(val error: String, val fieldErrors: Iterable<FieldError>): ZimbabweResponse<T>()
}

sealed class FormResponse : CommonResponseFields {
    override lateinit var backendVersion: String

    class Hunky<Meat>(val meat: Meat): FormResponse()
    class Shitty(val error: String, val fieldErrors: List<FieldError>): FormResponse()
}

sealed class FormResponse2<Meat> {
    class Hunky<Meat>(val meat: Meat): FormResponse2<Meat>()
    class Shitty<Meat>(val error: String, val fieldErrors: List<FieldError>): FormResponse2<Meat>()
}

enum class TextFieldType {
    STRING, TEXTAREA, PASSWORD, PHONE, EMAIL
}

fun passwordField(container: RequestMatumba) =
    TextField(container, "password", t("TOTE", "Пароль"), TextFieldType.PASSWORD, minLen = 6, maxLen = 50)

fun emailField(container: RequestMatumba) =
    TextField(container, "email", t("TOTE", "Почта"), TextFieldType.EMAIL, minLen = 3, maxLen = 50)

class SignInResponse(val token: String, val user: UserRTO) : CommonResponseFieldsImpl()

class SignInWithPasswordRequest : RequestMatumba() {
    val email = emailField(this)
    val password = passwordField(this)
}

class SignInWithTokenRequest : RequestMatumba() {
    companion object {
        fun send(token: String): Promise<SignInResponse> = callMatumba(SignInWithTokenRequest(), token)
    }
}

class SignUpRequest : RequestMatumba() {
    val immutableSignUpFields = ImmutableSignUpFields(this)
    val mutableSignUpFields = MutableSignUpFields(this)
    val agreeTerms = CheckboxField(this, "agreeTerms")
}

class UpdateProfileRequest() : RequestMatumba() {
    class Response(val newUser: UserRTO) : CommonResponseFieldsImpl()

    val mutableSignUpFields = MutableSignUpFields(this)
    val profileFields = ProfileFields(this)
}

open class UpdateUserRequest() : RequestMatumba() {
    class Response(val newUser: UserRTO) : CommonResponseFieldsImpl()

    val id = StringHiddenField(this, "id")
    val immutableSignUpFields = ImmutableSignUpFields(this)
    val mutableSignUpFields = MutableSignUpFields(this)
    val profileFields = ProfileFields(this)
    val state = SelectField(this, "state", t("TOTE", "Статус"), UserState.values())
    val profileRejectionReason = TextField(this, "profileRejectionReason", t("TOTE", "Причина отказа"), TextFieldType.TEXTAREA, 0, 5000)
    val banReason = TextField(this, "banReason", t("TOTE", "Причина бана"), TextFieldType.TEXTAREA, 0, 5000)
    val adminNotes = TextField(this, "adminNotes", t("TOTE", "Заметки админа"), TextFieldType.TEXTAREA, 0, 5000)
}

class AdminCreateUserRequest: UpdateUserRequest()

class ImmutableSignUpFields(container: RequestMatumba) {
    val email = emailField(container)
}

class MutableSignUpFields(container: RequestMatumba) {
    val firstName = TextField(container, "firstName", t("TOTE", "Имя"), TextFieldType.STRING, minLen = 1, maxLen = 50)
    val lastName = TextField(container, "lastName", t("TOTE", "Фамилия"), TextFieldType.STRING, minLen = 1, maxLen = 50)
}

class ProfileFields(container: RequestMatumba) {
    val phone = TextField(container, "phone", t("TOTE", "Телефон"), TextFieldType.PHONE, minLen = 6, maxLen = 20, minDigits = 6)
    val aboutMe = TextField(container, "aboutMe", t("TOTE", "Пара ласковых о себе"), TextFieldType.TEXTAREA, minLen = 1, maxLen = 300)
}


class WorldPointRequest() : RequestMatumba() {
    enum class Action { SAVE, RESTORE }

    val pointName = StringHiddenField(this, "pointName")
    val action = EnumHiddenField(this, "action", Action.values())

    companion object {
        fun send(pointName: String, action: Action): Promise<GenericResponse> = callDangerousMatumba(WorldPointRequest().apply {
            this.pointName.value = pointName
            this.action.value = action
        })
    }
}

class GetSoftwareVersionRequest : RequestMatumba() {
    class Response(val ctime: String, val backendInstanceID: String) : CommonResponseFieldsImpl()

    companion object {
        fun send(): Promise<Response> = callDangerousMatumba(GetSoftwareVersionRequest())
    }
}

class GetSentEmailsRequest : RequestMatumba() {
    class Response(val emails: List<Email>) : CommonResponseFieldsImpl()

    companion object {
        fun send(): Promise<Response> = callDangerousMatumba(GetSentEmailsRequest())
    }
}

class GetGeneratedShitRequest : RequestMatumba() {
    class Response(val code: String) : CommonResponseFieldsImpl()

    companion object {
        fun send(): Promise<Response> = callDangerousMatumba(GetGeneratedShitRequest())
    }
}

class ClearSentEmailsRequest : RequestMatumba() {
    companion object {
        fun send(): Promise<Unit> = callDangerousMatumba(ClearSentEmailsRequest())
    }

//    fun rpc(): Promise<GenericResponse> = callRemoteProcedure(this)
}

class ImposeNextGeneratedPasswordRequest() : RequestMatumba() {
    val password = StringHiddenField(this, "password")

    companion object {
        fun send(password: String): Promise<Unit> = callDangerousMatumba(ImposeNextGeneratedPasswordRequest().apply {
            this.password.value = password
        })
    }

//    fun rpc(): Promise<GenericResponse> = callRemoteProcedure(this)
}


class GetLiveStatusRequest : RequestMatumba() {
    sealed class Response : CommonResponseFieldsImpl() {
        class ForAdmin(val profilesToApprove: String, val suka: String) : Response()
        class ForWriter(val suka: String) : Response()
        class ForCustomer(val suka: String) : Response()
    }

    companion object {
        fun send(token: String): Promise<Response> = callMatumba(GetLiveStatusRequest(), token)
    }

//    fun rpc(ui: LegacyUIShit): Promise<Response> = callRemoteProcedure(this, ui)
}

class Email(val to: String, val subject: String, val html: String)


object ignore
infix operator fun Any?.div(erongi: ignore) = Unit

object ignora
infix operator fun Any?.div(arongi: ignora) = __asyncResult(Unit)
infix operator fun ignora.div(any: Any?) = __asyncResult(Unit)

//object a
//infix operator fun <T> a.div(x: T) = __asyncResult(x)

//object resulta
//infix operator fun <T> resulta.div(x: T) = __asyncResult(x)

class GetUserRequest() : RequestMatumba() {
    class Response (
        val user: UserRTO
    ) : CommonResponseFieldsImpl()

    val id = StringHiddenField(this, "id")

    companion object {
        fun send(token: String, id: String): Promise<ZimbabweResponse<Response>> = callZimbabwe(GetUserRequest()-{o->
            o.id.value = id
        }, token)
    }
}

enum class UserFilter(override val title: String): Titled {
    ALL(t("TOTE", "Все")),
    COOL(t("TOTE", "Прохладные")),
    PROFILE_APPROVAL_PENDING(t("TOTE", "Ждут аппрува")),
    PROFILE_REJECTED(t("TOTE", "Завернутые")),
    BANNED(t("TOTE", "Забаненые"))
}

enum class CustomerFileFilter(override val title: String): Titled {
    ALL(t("TOTE", "Все")),
    FROM_ME(t("TOTE", "Мои")),
    FROM_WRITER(t("TOTE", "От писателя")),
    FROM_SUPPORT(t("TOTE", "От саппорта"))
}

enum class WriterFileFilter(override val title: String): Titled {
    ALL(t("TOTE", "Все")),
    FROM_ME(t("TOTE", "Мои")),
    FROM_CUSTOMER(t("TOTE", "От заказчика")),
    FROM_SUPPORT(t("TOTE", "От саппорта"))
}

enum class AdminFileFilter(override val title: String): Titled {
    ALL(t("TOTE", "Все")),
    FROM_CUSTOMER(t("TOTE", "От заказчика")),
    FROM_WRITER(t("TOTE", "От писателя")),
    FROM_SUPPORT(t("TOTE", "От саппорта"))
}

enum class CustomerOrderFilter(override val title: String): Titled {
    ALL(t("TOTE", "Все"))
}

class EntityRequest() : RequestMatumba() {
    val id = StringHiddenField(this, "id")
}

class EntityResponse<Item> (
    val entity: Item
)

class ItemsRequest<Filter>(filterValues: Array<Filter>) : RequestMatumba()
where Filter: Enum<Filter>, Filter: Titled {
    val entityID = MaybeStringHiddenField(this, "entityID")
    val filter = EnumHiddenField(this, "filter", filterValues)
    val ordering = EnumHiddenField(this, "ordering", Ordering.values())
    val searchString = TextField(this, "searchString", "", TextFieldType.STRING, 0, 50)
    val fromID = MaybeStringHiddenField(this, "fromID")
}

class ItemsResponse<Item> (
    val items: List<Item>,
    val moreFromID: String?
) : CommonResponseFieldsImpl()


enum class Ordering(override val title: String) : Titled {
//    ASC(t("TOTE", "Сначала старые")),
//    DESC(t("TOTE", "Сначала новые"))
    ASC(t("TOTE", "Старые")),
    DESC(t("TOTE", "Новые"))
}

fun <T : Any> T?.orDefault(default: () -> T): T = if (this != null) this else default()

class MapStackRequest : RequestMatumba() {
    class Response(val originalStack: String) : CommonResponseFieldsImpl()

    val mangledStack = StringHiddenField(this, "mangledStack")

    companion object {
        fun send(mangledStack: String): Promise<Response> = callDangerousMatumba(MapStackRequest()-{o->
            o.mangledStack.value = mangledStack
        })
    }
}

class OpenSourceCodeRequest : RequestMatumba() {
    class Response(val error: String?) : CommonResponseFieldsImpl()

    val sourceLocation = StringHiddenField(this, "sourceLocation")

    companion object {
        fun send(sourceLocation: String): Promise<Response> = callDangerousMatumba(OpenSourceCodeRequest()-{o->
            o.sourceLocation.value = sourceLocation
        })
    }
}

class TestSetUserFieldsRequest() : RequestMatumba() {
    val email = StringHiddenField(this, "email")
    val state = EnumHiddenField(this, "state", UserState.values(), possiblyUnspecified = true)
    val profileRejectionReason = MaybeStringHiddenField(this, "profileRejectionReason", possiblyUnspecified = true)
    val phone = MaybeStringHiddenField(this, "phone", possiblyUnspecified = true)
    val aboutMe = MaybeStringHiddenField(this, "aboutMe", possiblyUnspecified = true)
    val banReason = MaybeStringHiddenField(this, "banReason", possiblyUnspecified = true)
    val profileUpdatedAt = StringHiddenField(this, "profileUpdatedAt ", possiblyUnspecified = true)
    val insertedAt = StringHiddenField(this, "insertedAt ", possiblyUnspecified = true)
}

fun send(req: TestSetUserFieldsRequest): Promise<GenericResponse> = callDangerousMatumba(req)

//class GetRedisLogMessagesRequest : RequestMatumba() {
//    class Response(val items: List<RedisLogMessage>)
//
//    fun send(): Promise<Response> = callDangerousMatumba(this)
//}

class SendRedisLogMessageRequest : RequestMatumba() {
    val type = EnumHiddenField(this, "type", RedisLogMessage.Separator.Type.values())
    val text = StringHiddenField(this, "text")
}
fun send(req: SendRedisLogMessageRequest): Promise<GenericResponse> = callDangerousMatumba(req)


class JSONResponse(val json: String) : CommonResponseFieldsImpl()

class PrivilegedRedisCommandRequest : RequestMatumba() {
    val json = StringHiddenField(this, "json")
}
fun send(req: PrivilegedRedisCommandRequest): Promise<JSONResponse> = callDangerousMatumba(req)

interface CommonRequestFields {
    var rootRedisLogMessageID: String?
    var databaseID: String?
    var fakeEmail: Boolean
    var clientURL: String
}

interface CommonResponseFields {
    var backendVersion: String
}

class FuckingRemoteProcedureRequest : RequestMatumba() {
    val json = StringHiddenField(this, "json")
}
fun send(req: FuckingRemoteProcedureRequest): Promise<JSONResponse> = callDangerousMatumba(req)

class CustomerCreateUAOrderRequest : RequestMatumba() {
    class Response(val id: String) : CommonResponseFieldsImpl()

    val title = TextField(this, "title", t("TOTE", "Название"), TextFieldType.STRING, const.order.minTitleLen, const.order.maxTitleLen)
    val documentType = SelectField(this, "documentType", t("TOTE", "Тип документа"), UADocumentType.values())
    val deadline = DateTimeField(this, "deadline", t("TOTE", "Срок"))
    val numPages = IntField(this, "numPages", t("TOTE", "Страниц"), const.order.minPages, const.order.maxPages)
    val numSources = IntField(this, "numSources", t("TOTE", "Источников"), const.order.minSources, const.order.maxSources)
    val details = TextField(this, "details", t("TOTE", "Детали"), TextFieldType.TEXTAREA, const.order.minDetailsLen, const.order.maxDetailsLen)
}

fun fileField(container: RequestMatumba, shouldBeProvided: Boolean = true) = FileField(container, "file", t("TOTE", "Файл"), shouldBeProvided = shouldBeProvided)
fun fileTitleField(container: RequestMatumba) = TextField(container, "title", t("TOTE", "Название"), TextFieldType.STRING, const.file.minTitleLen, const.file.maxTitleLen)
fun fileDetailsField(container: RequestMatumba) = TextField(container, "details", t("TOTE", "Детали"), TextFieldType.TEXTAREA, const.file.minDetailsLen, const.file.maxDetailsLen)

abstract class AddUAOrderFileRequestBase : RequestMatumba() {
    class Response(val id: String) : CommonResponseFieldsImpl()

    val orderID = StringHiddenField(this, "orderID")
    val file = fileField(this)
    val title = fileTitleField(this)
    val details = fileDetailsField(this)
}

class CustomerAddUAOrderFileRequest : AddUAOrderFileRequestBase()
class WriterAddUAOrderFileRequest : AddUAOrderFileRequestBase()

abstract class EditUAOrderFileRequestBase : RequestMatumba() {
    class Response(val updatedOrderFile: UAOrderFileRTO) : CommonResponseFieldsImpl()

    val orderFileID = StringHiddenField(this, "orderFileID")
    val file = fileField(this, shouldBeProvided = false)
    val title = fileTitleField(this)
    val details = fileDetailsField(this)
}

class CustomerEditUAOrderFileRequest : EditUAOrderFileRequestBase()
class WriterEditUAOrderFileRequest : EditUAOrderFileRequestBase()

class DeleteUAOrderFileRequest : RequestMatumba() {
    class Response : CommonResponseFieldsImpl()
    val orderFileID = StringHiddenField(this, "orderFileID")
}

enum class UADocumentType(override val title: String) : Titled {
    ESSAY(t("TOTE", "Реферат")),
    COURSE(t("TOTE", "Курсовая работа")),
    GRADUATION(t("TOTE", "Дипломная работа"))
}

enum class DocumentUrgency(override val title: String) : Titled {
    H12(t("TOTE", "12 часов")),
    H24(t("TOTE", "24 часа")),
    D3(t("TOTE", "2-3 дня")),
    D5(t("TOTE", "4-5 дней")),
    D7(t("TOTE", "6-7 дней")),
    D8(t("TOTE", "8+ дней"))
}

enum class UAAcademicLevel(override val title: String) : Titled {
    SCHOOL(t("TOTE", "Школота")),
    INSTITUTE(t("TOTE", "Студень"))
}


fun uaPageCost(type: UADocumentType, urgency: DocumentUrgency): Int =
    when (type) {
        UADocumentType.ESSAY -> when (urgency) {
            DocumentUrgency.D8 ->  1099
            DocumentUrgency.D7 ->  1299
            DocumentUrgency.D5 ->  1599
            DocumentUrgency.D3 ->  2099
            DocumentUrgency.H24 -> 2599
            DocumentUrgency.H12 -> 3599
        }
        UADocumentType.COURSE -> when (urgency) {
            DocumentUrgency.D8 -> 1599
            DocumentUrgency.D7 -> 1799
            DocumentUrgency.D5 -> 2099
            DocumentUrgency.D3 -> 2599
            DocumentUrgency.H24 -> 3099
            DocumentUrgency.H12 -> 4099
        }
        UADocumentType.GRADUATION -> when (urgency) {
            DocumentUrgency.D8 -> 599
            DocumentUrgency.D7 -> 799
            DocumentUrgency.D5 -> 1099
            DocumentUrgency.D3 -> 1599
            DocumentUrgency.H24 -> 2099
            DocumentUrgency.H12 -> 3099
        }
    }

abstract class CommonResponseFieldsImpl : CommonResponseFields {
    override lateinit var backendVersion: String
}

class LoadUAOrderRequest : RequestMatumba() {
    class Response(val order: UAOrderRTO) : CommonResponseFieldsImpl()
    val id = StringHiddenField(this, "id")
}

enum class Color(val string: String) {
    // https://www.google.com/design/spec/style/color.html#color-color-palette
    BLACK("#000000"), BLACK_BOOT("#333333"), WHITE("#ffffff"),
    RED_50("#ffebee"), RED_100("#ffcdd2"), RED_200("#ef9a9a"), RED_300("#e57373"), RED_400("#ef5350"), RED_500("#f44336"), RED_600("#e53935"), RED_700("#d32f2f"), RED_800("#c62828"), RED_900("#b71c1c"), RED_A100("#ff8a80"), RED_A200("#ff5252"), RED_A400("#ff1744"), RED_A700("#d50000"),
    PINK_50("#fce4ec"), PINK_100("#f8bbd0"), PINK_200("#f48fb1"), PINK_300("#f06292"), PINK_400("#ec407a"), PINK_500("#e91e63"), PINK_600("#d81b60"), PINK_700("#c2185b"), PINK_800("#ad1457"), PINK_900("#880e4f"), PINK_A100("#ff80ab"), PINK_A200("#ff4081"), PINK_A400("#f50057"), PINK_A700("#c51162"),
    PURPLE_50("#f3e5f5"), PURPLE_100("#e1bee7"), PURPLE_200("#ce93d8"), PURPLE_300("#ba68c8"), PURPLE_400("#ab47bc"), PURPLE_500("#9c27b0"), PURPLE_600("#8e24aa"), PURPLE_700("#7b1fa2"), PURPLE_800("#6a1b9a"), PURPLE_900("#4a148c"), PURPLE_A100("#ea80fc"), PURPLE_A200("#e040fb"), PURPLE_A400("#d500f9"), PURPLE_A700("#aa00ff"),
    DEEP_PURPLE_50("#ede7f6"), DEEP_PURPLE_100("#d1c4e9"), DEEP_PURPLE_200("#b39ddb"), DEEP_PURPLE_300("#9575cd"), DEEP_PURPLE_400("#7e57c2"), DEEP_PURPLE_500("#673ab7"), DEEP_PURPLE_600("#5e35b1"), DEEP_PURPLE_700("#512da8"), DEEP_PURPLE_800("#4527a0"), DEEP_PURPLE_900("#311b92"), DEEP_PURPLE_A100("#b388ff"), DEEP_PURPLE_A200("#7c4dff"), DEEP_PURPLE_A400("#651fff"), DEEP_PURPLE_A700("#6200ea"),
    INDIGO_50("#e8eaf6"), INDIGO_100("#c5cae9"), INDIGO_200("#9fa8da"), INDIGO_300("#7986cb"), INDIGO_400("#5c6bc0"), INDIGO_500("#3f51b5"), INDIGO_600("#3949ab"), INDIGO_700("#303f9f"), INDIGO_800("#283593"), INDIGO_900("#1a237e"), INDIGO_A100("#8c9eff"), INDIGO_A200("#536dfe"), INDIGO_A400("#3d5afe"), INDIGO_A700("#304ffe"),
    BLUE_50("#e3f2fd"), BLUE_100("#bbdefb"), BLUE_200("#90caf9"), BLUE_300("#64b5f6"), BLUE_400("#42a5f5"), BLUE_500("#2196f3"), BLUE_600("#1e88e5"), BLUE_700("#1976d2"), BLUE_800("#1565c0"), BLUE_900("#0d47a1"), BLUE_A100("#82b1ff"), BLUE_A200("#448aff"), BLUE_A400("#2979ff"), BLUE_A700("#2962ff"),
    LIGHT_BLUE_50("#e1f5fe"), LIGHT_BLUE_100("#b3e5fc"), LIGHT_BLUE_200("#81d4fa"), LIGHT_BLUE_300("#4fc3f7"), LIGHT_BLUE_400("#29b6f6"), LIGHT_BLUE_500("#03a9f4"), LIGHT_BLUE_600("#039be5"), LIGHT_BLUE_700("#0288d1"), LIGHT_BLUE_800("#0277bd"), LIGHT_BLUE_900("#01579b"), LIGHT_BLUE_A100("#80d8ff"), LIGHT_BLUE_A200("#40c4ff"), LIGHT_BLUE_A400("#00b0ff"), LIGHT_BLUE_A700("#0091ea"),
    CYAN_50("#e0f7fa"), CYAN_100("#b2ebf2"), CYAN_200("#80deea"), CYAN_300("#4dd0e1"), CYAN_400("#26c6da"), CYAN_500("#00bcd4"), CYAN_600("#00acc1"), CYAN_700("#0097a7"), CYAN_800("#00838f"), CYAN_900("#006064"), CYAN_A100("#84ffff"), CYAN_A200("#18ffff"), CYAN_A400("#00e5ff"), CYAN_A700("#00b8d4"),
    TEAL_50("#e0f2f1"), TEAL_100("#b2dfdb"), TEAL_200("#80cbc4"), TEAL_300("#4db6ac"), TEAL_400("#26a69a"), TEAL_500("#009688"), TEAL_600("#00897b"), TEAL_700("#00796b"), TEAL_800("#00695c"), TEAL_900("#004d40"), TEAL_A100("#a7ffeb"), TEAL_A200("#64ffda"), TEAL_A400("#1de9b6"), TEAL_A700("#00bfa5"),
    GREEN_50("#e8f5e9"), GREEN_100("#c8e6c9"), GREEN_200("#a5d6a7"), GREEN_300("#81c784"), GREEN_400("#66bb6a"), GREEN_500("#4caf50"), GREEN_600("#43a047"), GREEN_700("#388e3c"), GREEN_800("#2e7d32"), GREEN_900("#1b5e20"), GREEN_A100("#b9f6ca"), GREEN_A200("#69f0ae"), GREEN_A400("#00e676"), GREEN_A700("#00c853"),
    LIGHT_GREEN_50("#f1f8e9"), LIGHT_GREEN_100("#dcedc8"), LIGHT_GREEN_200("#c5e1a5"), LIGHT_GREEN_300("#aed581"), LIGHT_GREEN_400("#9ccc65"), LIGHT_GREEN_500("#8bc34a"), LIGHT_GREEN_600("#7cb342"), LIGHT_GREEN_700("#689f38"), LIGHT_GREEN_800("#558b2f"), LIGHT_GREEN_900("#33691e"), LIGHT_GREEN_A100("#ccff90"), LIGHT_GREEN_A200("#b2ff59"), LIGHT_GREEN_A400("#76ff03"), LIGHT_GREEN_A700("#64dd17"),
    LIME_50("#f9fbe7"), LIME_100("#f0f4c3"), LIME_200("#e6ee9c"), LIME_300("#dce775"), LIME_400("#d4e157"), LIME_500("#cddc39"), LIME_600("#c0ca33"), LIME_700("#afb42b"), LIME_800("#9e9d24"), LIME_900("#827717"), LIME_A100("#f4ff81"), LIME_A200("#eeff41"), LIME_A400("#c6ff00"), LIME_A700("#aeea00"),
    YELLOW_50("#fffde7"), YELLOW_100("#fff9c4"), YELLOW_200("#fff59d"), YELLOW_300("#fff176"), YELLOW_400("#ffee58"), YELLOW_500("#ffeb3b"), YELLOW_600("#fdd835"), YELLOW_700("#fbc02d"), YELLOW_800("#f9a825"), YELLOW_900("#f57f17"), YELLOW_A100("#ffff8d"), YELLOW_A200("#ffff00"), YELLOW_A400("#ffea00"), YELLOW_A700("#ffd600"),
    AMBER_50("#fff8e1"), AMBER_100("#ffecb3"), AMBER_200("#ffe082"), AMBER_300("#ffd54f"), AMBER_400("#ffca28"), AMBER_500("#ffc107"), AMBER_600("#ffb300"), AMBER_700("#ffa000"), AMBER_800("#ff8f00"), AMBER_900("#ff6f00"), AMBER_A100("#ffe57f"), AMBER_A200("#ffd740"), AMBER_A400("#ffc400"), AMBER_A700("#ffab00"),
    ORANGE_50("#fff3e0"), ORANGE_100("#ffe0b2"), ORANGE_200("#ffcc80"), ORANGE_300("#ffb74d"), ORANGE_400("#ffa726"), ORANGE_500("#ff9800"), ORANGE_600("#fb8c00"), ORANGE_700("#f57c00"), ORANGE_800("#ef6c00"), ORANGE_900("#e65100"), ORANGE_A100("#ffd180"), ORANGE_A200("#ffab40"), ORANGE_A400("#ff9100"), ORANGE_A700("#ff6d00"),
    DEEP_ORANGE_50("#fbe9e7"), DEEP_ORANGE_100("#ffccbc"), DEEP_ORANGE_200("#ffab91"), DEEP_ORANGE_300("#ff8a65"), DEEP_ORANGE_400("#ff7043"), DEEP_ORANGE_500("#ff5722"), DEEP_ORANGE_600("#f4511e"), DEEP_ORANGE_700("#e64a19"), DEEP_ORANGE_800("#d84315"), DEEP_ORANGE_900("#bf360c"), DEEP_ORANGE_A100("#ff9e80"), DEEP_ORANGE_A200("#ff6e40"), DEEP_ORANGE_A400("#ff3d00"), DEEP_ORANGE_A700("#dd2c00"),
    BROWN_50("#efebe9"), BROWN_100("#d7ccc8"), BROWN_200("#bcaaa4"), BROWN_300("#a1887f"), BROWN_400("#8d6e63"), BROWN_500("#795548"), BROWN_600("#6d4c41"), BROWN_700("#5d4037"), BROWN_800("#4e342e"), BROWN_900("#3e2723"),
    GRAY_50("#fafafa"), GRAY_100("#f5f5f5"), GRAY_200("#eeeeee"), GRAY_300("#e0e0e0"), GRAY_400("#bdbdbd"), GRAY_500("#9e9e9e"), GRAY_600("#757575"), GRAY_700("#616161"), GRAY_800("#424242"), GRAY_900("#212121"),
    BLUE_GRAY_50("#eceff1"), BLUE_GRAY_100("#cfd8dc"), BLUE_GRAY_200("#b0bec5"), BLUE_GRAY_300("#90a4ae"), BLUE_GRAY_400("#78909c"), BLUE_GRAY_500("#607d8b"), BLUE_GRAY_600("#546e7a"), BLUE_GRAY_700("#455a64"), BLUE_GRAY_800("#37474f"), BLUE_GRAY_900("#263238"),
    RED("red"), GREEN("green"), BLUE("blue"), ROSYBROWN("rosybrown"),;

    override fun toString() = string
}

class PingRequest : RequestMatumba()

data class PieceOfShitDownload(
    val id: Long,
    val name: String,
    val forbidden: Boolean,
    val sha1: String
)

class LuceneParseToken {
    var type: String by notNull()
    var startOffset: Int by notNull()
    var endOffset: Int by notNull()
    var text: String by notNull()

    override fun toString() =
        "LuceneParseToken(type='$type', startOffset=$startOffset, endOffset=$endOffset, text='$text')"
}







