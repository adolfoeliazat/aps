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

enum class UserState() {
    COOL, PROFILE_PENDING, PROFILE_APPROVAL_PENDING, PROFILE_REJECTED, BANNED
}

class TimestampRTO(val value: String) {
}

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

class ImposeNextRequestTimestampRequest(val stamp: String) : Request() {
    constructor() : this(SHITS) // For fucking Jackson
    fun rpc(): Promise<GenericResponse> = callRemoteProcedure(this)
}

class ResetTestDatabaseRequest(val templateDB: String, val recreateTemplate: Boolean = false) : Request() {
    constructor() : this(SHITS, SHITB) // For fucking Jackson
    fun rpc(): Promise<GenericResponse> = callRemoteProcedure(this)
}

class GenericResponse

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

    class Response {
        lateinit var token: String
        lateinit var user: UserRTO
    }
}

class SignUpFields(container: RequestMatumba) {
    val email = emailField(container)
    val firstName = TextField(container, "firstName", t("TOTE", "Имя"), TextFieldType.STRING, minLen = 1, maxLen = 50)
    val lastName = TextField(container, "lastName", t("TOTE", "Фамилия"), TextFieldType.STRING, minLen = 1, maxLen = 50)
}

class SignUpRequest : RequestMatumba() {
    val signUpFields = SignUpFields(this)
    val agreeTerms = CheckboxField(this, "agreeTerms")
}

class UpdateProfileRequest() : RequestMatumba() {
    class Response {
        lateinit var newUser: UserRTO
    }

    companion object

    val profileFields = ProfileFields(this)
}

class ProfileFields(container: RequestMatumba) {
    val phone = TextField(container, "phone", t("TOTE", "Телефон"), TextFieldType.PHONE, minLen = 6, maxLen = 20, minDigits = 6)
    val aboutMe = TextField(container, "aboutMe", t("TOTE", "Пара ласковых о себе"), TextFieldType.TEXTAREA, minLen = 1, maxLen = 300)
}


class WorldPointRequest(val pointName: String, val action: Action) : Request() {
    enum class Action { SAVE, RESTORE }
    constructor() : this(SHITS, Action.SAVE) // For fucking Jackson

    fun rpc(): Promise<GenericResponse> = callRemoteProcedure(this)
}

class GetSentEmailsRequest : Request() {
    class Response {
        lateinit var emails: List<Email>
    }

    fun rpc(): Promise<Response> = callRemoteProcedure(this)
}

class ClearSentEmailsRequest : Request() {
    fun rpc(): Promise<GenericResponse> = callRemoteProcedure(this)
}

class ImposeNextGeneratedPasswordRequest(val password: String) : Request() {
    constructor() : this(SHITS) // For fucking Jackson
    fun rpc(): Promise<GenericResponse> = callRemoteProcedure(this)
}

class GetLiveStatusRequest : RequestMatumba() {
    class Response {
        lateinit var case: Case
        sealed class Case {
            class ForAdmin(val profilesToApprove: String, val suka: String) : Case()
            class ForWriter(val suka: String) : Case()
            class ForCustomer(val suka: String) : Case()
        }
    }

    fun rpc(ui: LegacyUIShit): Promise<Response> = callRemoteProcedure(this, ui)
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




















