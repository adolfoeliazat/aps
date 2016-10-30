/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps

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

enum class Language() {
    EN, UA
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

// TODO:vgrechka Model profile as separate class with non-nullable props (to avoid !!)    fece17b6-5816-4b63-b687-a68b42fb2304
class UserRTO(
    val id: String,
    val deleted: Boolean,
    val insertedAt: TimestampRTO,
    val updatedAt: TimestampRTO,
    val profileUpdatedAt: TimestampRTO?,
    val kind: UserKind,
    val lang: Language,
    val email: String,
    val state: UserState,
    val profileRejectionReason: String?,
    val banReason: String?,
    val adminNotes: String?,
    val firstName: String,
    val lastName: String,
    val phone: String?,
    val compactPhone: String?,
    val aboutMe: String?,
    val roles: Set<UserRole>
)

val SHITS = "--SHIT--"
val SHITB = false

open class Request {
    var token: String? = null
    var fields = mapOf<String, Any?>()
    lateinit var lang: Language
    lateinit var clientKind: ClientKind
}

open class GenericRequest : Request()

class ImposeNextRequestTimestampRequest : RequestMatumba() {
    val stamp = StringHiddenField(this, "stamp")

    companion object {
        fun send(stamp: String): Promise<GenericResponse> = callDangerousMatumba(ImposeNextRequestTimestampRequest().apply {
            this.stamp.value = stamp
        })
    }

//    fun rpc(): Promise<GenericResponse> = callRemoteProcedure(this)
}

//class ImposeNextRequestTimestampRequest(val stamp: String) : Request() {
//    constructor() : this(SHITS) // For fucking Jackson
//    fun rpc(): Promise<GenericResponse> = callRemoteProcedure(this)
//}

class ResetTestDatabaseRequest() : RequestMatumba() {
    val templateDB = StringHiddenField(this, "templateDB")
    val recreateTemplate = BooleanHiddenField(this, "recreateTemplate")

    companion object {
        fun send(templateDB: String, recreateTemplate: Boolean = false): Promise<GenericResponse> = callDangerousMatumba(ResetTestDatabaseRequest().apply {
            this.templateDB.value = templateDB
            this.recreateTemplate.value = recreateTemplate
        })
    }

//    fun rpc(): Promise<GenericResponse> = callRemoteProcedure(this)
}

class GenericResponse

sealed class ZimbabweResponse<T> {
    class Hunky<T>(val meat: T): ZimbabweResponse<T>()
    class Shitty<T>(val error: String, val fieldErrors: Iterable<FieldError>): ZimbabweResponse<T>()
}

sealed class FormResponse {
    class Hunky<Meat>(val meat: Meat): FormResponse()
    class Shitty(val error: String, val fieldErrors: Iterable<FieldError>): FormResponse()
}

enum class TextFieldType {
    STRING, TEXTAREA, PASSWORD, PHONE, EMAIL
}

fun passwordField(container: RequestMatumba) =
    TextField(container, "password", t("TOTE", "Пароль"), TextFieldType.PASSWORD, minLen = 6, maxLen = 30)

fun emailField(container: RequestMatumba) =
    TextField(container, "email", t("TOTE", "Почта"), TextFieldType.EMAIL, minLen = 3, maxLen = 50)

class SignInWithPasswordRequest : RequestMatumba() {
    val email = emailField(this)
    val password = passwordField(this)

    class Response(val token: String, val user: UserRTO)
}

class SignUpRequest : RequestMatumba() {
    val signUpFields = SignUpFields(this)
    val agreeTerms = CheckboxField(this, "agreeTerms")
}

class UpdateProfileRequest() : RequestMatumba() {
    class Response(val newUser: UserRTO)

    val profileFields = ProfileFields(this)
}

open class UpdateUserRequest() : RequestMatumba() {
    class Response(val newUser: UserRTO)

    val id = StringHiddenField(this, "id")
    val signUpFields = SignUpFields(this)
    val profileFields = ProfileFields(this)
    val state = SelectField(this, "state", t("TOTE", "Статус"), UserState.values())
    val profileRejectionReason = TextField(this, "profileRejectionReason", t("TOTE", "Причина отказа"), TextFieldType.TEXTAREA, 0, 5000)
    val banReason = TextField(this, "banReason", t("TOTE", "Причина бана"), TextFieldType.TEXTAREA, 0, 5000)
    val adminNotes = TextField(this, "adminNotes", t("TOTE", "Заметки админа"), TextFieldType.TEXTAREA, 0, 5000)
}

class AdminCreateUserRequest: UpdateUserRequest()

class SignUpFields(container: RequestMatumba) {
    val email = emailField(container)
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
    class Response(val ctime: String, val backendInstanceID: String)

    companion object {
        fun send(): Promise<Response> = callDangerousMatumba(GetSoftwareVersionRequest ())
    }
}

class GetSentEmailsRequest : RequestMatumba() {
    class Response(val emails: List<Email>)

    companion object {
        fun send(): Promise<Response> = callDangerousMatumba(GetSentEmailsRequest())
    }
}

class GetGeneratedShitRequest : RequestMatumba() {
    class Response(val code: String)

    companion object {
        fun send(): Promise<Response> = callDangerousMatumba(GetGeneratedShitRequest())
    }
}

class ClearSentEmailsRequest : RequestMatumba() {
    companion object {
        fun send(): Promise<GenericResponse> = callDangerousMatumba(ClearSentEmailsRequest())
    }

//    fun rpc(): Promise<GenericResponse> = callRemoteProcedure(this)
}

class ImposeNextGeneratedPasswordRequest() : RequestMatumba() {
    val password = StringHiddenField(this, "password")

    companion object {
        fun send(password: String): Promise<GenericResponse> = callDangerousMatumba(ImposeNextGeneratedPasswordRequest().apply {
            this.password.value = password
        })
    }

//    fun rpc(): Promise<GenericResponse> = callRemoteProcedure(this)
}

class GetLiveStatusRequest : RequestMatumba() {
    sealed class Response {
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

object GlobalMatumba {
    enum class Mode { DEBUG, PRODUCTION }

    val mode = Mode.DEBUG
}

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
    )

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

class EntityRequest() : RequestMatumba() {
    val id = StringHiddenField(this, "id")
}

class EntityResponse<Item> (
    val entity: Item
)

class ItemsRequest<Filter>(filterValues: Array<Filter>) : RequestMatumba()
where Filter: Enum<Filter>, Filter: Titled {
    val entityID = HiddenMaybeStringField(this, "entityID")
    val filter = EnumHiddenField(this, "filter", filterValues)
    val ordering = EnumHiddenField(this, "ordering", Ordering.values())
    val searchString = TextField(this, "searchString", "", TextFieldType.STRING, 0, 50)
    val fromID = HiddenMaybeStringField(this, "fromID")
}

class ItemsResponse<Item> (
    val items: List<Item>,
    val moreFromID: String?
)


enum class Ordering(override val title: String) : Titled {
    ASC(t("TOTE", "Сначала старые")),
    DESC(t("TOTE", "Сначала новые"))
}

fun <T : Any> T?.orDefault(default: () -> T): T = if (this != null) this else default()

class MapStackRequest : RequestMatumba() {
    class Response(val originalStack: String)

    val mangledStack = StringHiddenField(this, "mangledStack")

    companion object {
        fun send(mangledStack: String): Promise<Response> = callDangerousMatumba(MapStackRequest()-{o->
            o.mangledStack.value = mangledStack
        })
    }
}






















