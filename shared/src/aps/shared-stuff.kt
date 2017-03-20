/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("LeakingThis")

package aps

import aps.Color.*
import aps.front.*
import kotlin.properties.Delegates
import kotlin.properties.Delegates.notNull


class TimestampRTO(val value: String) {
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
    class Response : CommonResponseFieldsImpl()
    val stamp = StringHiddenField(this, "stamp")
}

class ImposeNextRequestErrorRequest : RequestMatumba() {
    val error = MaybeStringHiddenField(this, "error")
}

//class ImposeNextRequestTimestampRequest(val stamp: String) : Request() {
//    constructor() : this(SHITS) // For fucking Jackson
//    fun rpc(): Promise<GenericResponse> = callRemoteProcedure(this)
//}

class ResetTestDatabaseRequest() : RequestMatumba()


class ResetTestDatabaseAlongWithTemplateRequest() : RequestMatumba() {
    val templateDB = StringHiddenField(this, "templateDB")
    val recreateTemplate = BooleanHiddenField(this, "recreateTemplate")

//    companion object {
//        fun send(templateDB: String, recreateTemplate: Boolean = false): Promisoid<GenericResponse> = callDangerousMatumba(ResetTestDatabaseAlongWithTemplateRequest().apply {
//            this.templateDB.value = templateDB
//            this.recreateTemplate.value = recreateTemplate
//        })
//    }

//    fun rpc(): Promise<GenericResponse> = callRemoteProcedure(this)
}

sealed class ZimbabweResponse<T> {
    class Hunky<T>(val meat: T): ZimbabweResponse<T>()
    class Shitty<T>(val error: String, val fieldErrors: Iterable<FieldError>): ZimbabweResponse<T>()
}

sealed class FormResponse2<out Meat> {
    class Hunky<out Meat>(val meat: Meat): FormResponse2<Meat>()
    class Shitty<out Meat>(val error: String, val fieldErrors: List<FieldError>): FormResponse2<Meat>()
}

class SignInResponse(val token: String, val user: UserRTO) : CommonResponseFieldsImpl()

class SignInWithPasswordRequest : RequestMatumba() {
    val email = TextField(this, fields.signInEmail)
    val password = TextField(this, fields.signInPassword)
}

class SignInWithTokenRequest : RequestMatumba() {
}

class SignUpRequest : RequestMatumba() {
    val firstName = TextField(this, fields.signUpFirstName)
    val lastName = TextField(this, fields.signUpLastName)
    val email = TextField(this, fields.signUpEmail)
    val agreeTerms = CheckboxField(this, fields.agreeTerms)
    class Response : CommonResponseFieldsImpl()
}

class UpdateProfileRequest : RequestMatumba() {
    val firstName = TextField(this, fields.signUpFirstName)
    val lastName = TextField(this, fields.signUpLastName)
    val profilePhone = TextField(this, fields.profilePhone)
    val categorySubscriptions = DocumentCategorySetField(this, fields.writerDocumentCategories)
    val aboutMe = TextField(this, fields.aboutMe)

    class Response() : CommonResponseFieldsImpl()
}


class ProfileFields(container: RequestMatumba) {
    val phone = TextField(container, fields.orderCustomerPhone)
    val aboutMe = TextField(container, fields.aboutMe)
}


class WorldPointRequest() : RequestMatumba() {
    enum class Action { SAVE, RESTORE }

    val pointName = StringHiddenField(this, "pointName")
    val action = EnumHiddenField(this, "action", Action.values())

//    companion object {
//        fun send(pointName: String, action: Action): Promisoid<GenericResponse> = callDangerousMatumba(WorldPointRequest().apply {
//            this.pointName.value = pointName
//            this.action.value = action
//        })
//    }
}

class GetSoftwareVersionRequest : RequestMatumba() {
    class Response(val ctime: String, val backendInstanceID: String) : CommonResponseFieldsImpl()

    companion object {
        fun send(): Promisoid<Response> = callDangerousMatumba(GetSoftwareVersionRequest())
    }
}

class Email(val to: String, val subject: String, val html: String)

class GetSentEmailsRequest : RequestMatumba() {
    class Response(val emails: List<Email>) : CommonResponseFieldsImpl()

    companion object {
        fun send(): Promisoid<Response> = callDangerousMatumba(GetSentEmailsRequest())
    }
}

class GetGeneratedShitRequest : RequestMatumba() {
    class Response(val code: String) : CommonResponseFieldsImpl()

    companion object {
        fun send(): Promisoid<Response> = callDangerousMatumba(GetGeneratedShitRequest())
    }
}

class ClearSentEmailsRequest : RequestMatumba() {
    companion object {
        fun send(): Promisoid<Unit> = callDangerousMatumba(ClearSentEmailsRequest())
    }

//    fun rpc(): Promise<GenericResponse> = callRemoteProcedure(this)
}

class ImposeNextGeneratedPasswordRequest : RequestMatumba() {
    class Response : CommonResponseFieldsImpl()
    val password = StringHiddenField(this, "password")
}

class ImposeNextGeneratedConfirmationSecretRequest : RequestMatumba() {
    class Response : CommonResponseFieldsImpl()
    val secret = StringHiddenField(this, "secret")
}


class GetLiveStatusRequest : RequestMatumba() {
    sealed class Response : CommonResponseFieldsImpl() {
        class ForAdmin(val profilesToApprove: String, val suka: String) : Response()
        class ForWriter(val suka: String) : Response()
        class ForCustomer(val suka: String) : Response()
    }

    companion object {
        fun send(token: String): Promisoid<Response> = callMatumba(GetLiveStatusRequest(), token)
    }

//    fun rpc(ui: LegacyUIShit): Promise<Response> = callRemoteProcedure(this, ui)
}



//object ignore
//infix operator fun Any?.div(erongi: ignore) = Unit


class GetUserRequest() : RequestMatumba() {
    class Response (
        val user: UserRTO
    ) : CommonResponseFieldsImpl()

    val id = StringHiddenField(this, "id")

//    companion object {
//        fun send(token: String, id: String): Promisoid<ZimbabweResponse<Response>> = callZimbabwe(GetUserRequest()-{o->
//            o.id.value = id
//        }, token)
//    }
}

enum class AdminUserFilter(override val title: String): Titled {
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

enum class UserParamsHistoryFilter(override val title: String): Titled {
    ALL(t("TOTE", "Все"))
}

enum class AdminOrderFilter(override val title: String): Titled {
    ALL(t("TOTE", "Все")),
    TO_APPROVE(t("TOTE", "Ждут аппрува"))
}

enum class WriterStoreFilter(override val title: String): Titled {
    ALL(t("TOTE", "Все")),
    MY_SPECIALIZATION(t("TOTE", "Моя специализация"))
}

enum class AdminBidFilter(override val title: String): Titled {
    ALL(t("TOTE", "Все")),
    TO_CONSIDER(t("TOTE", "К рассмотрению"))
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

//class EntityRequest() : RequestMatumba() {
//    val id = StringHiddenField(this, "id")
//}
//
//interface EntityResponse<out T> {
//    val entity: T
//}
//
//class SimpleEntityResponse<out T>(override val entity: T) : CommonResponseFieldsImpl(), EntityResponse<T>

class ItemsRequest() : RequestMatumba() {
    val parentEntityID by maybeLongHiddenField()
    val filter = StringHiddenField(this, "filter")
    val ordering = EnumHiddenField(this, "ordering", Ordering.values())
    val searchString = TextField(this, fields.searchString)
    val fromID by maybeLongHiddenField()
}

class ItemsResponse<Item> (
    val items: List<Item>,
    val moreFromID: Long?
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

//    companion object {
//        fun send(mangledStack: String): Promisoid<Response> = callDangerousMatumba(MapStackRequest()-{o->
//            o.mangledStack.value = mangledStack
//        })
//    }
}

class OpenSourceCodeRequest : RequestMatumba() {
    class Response(val error: String?) : CommonResponseFieldsImpl()

    val sourceLocation = StringHiddenField(this, "sourceLocation")

//    companion object {
//        fun send(sourceLocation: String): Promisoid<Response> = callDangerousMatumba(OpenSourceCodeRequest()-{o->
//            o.sourceLocation.value = sourceLocation
//        })
//    }
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

fun send(req: TestSetUserFieldsRequest): Promisoid<GenericResponse> = callDangerousMatumba(req)

//class GetRedisLogMessagesRequest : RequestMatumba() {
//    class Response(val items: List<RedisLogMessage>)
//
//    fun send(): Promise<Response> = callDangerousMatumba(this)
//}

//class SendRedisLogMessageRequest : RequestMatumba() {
//    val type = EnumHiddenField(this, "type", RedisLogMessage.Separator.Type.values())
//    val text = StringHiddenField(this, "text")
//}
//fun send(req: SendRedisLogMessageRequest): Promisoid<GenericResponse> = callDangerousMatumba(req)


class JSONResponse(val json: String) : CommonResponseFieldsImpl()

//class PrivilegedRedisCommandRequest : RequestMatumba() {
//    val json = StringHiddenField(this, "json")
//}
//fun send(req: PrivilegedRedisCommandRequest): Promisoid<JSONResponse> = callDangerousMatumba(req)

class FuckingRemoteProcedureRequest : RequestMatumba() {
    val json = StringHiddenField(this, "json")
}
fun send(req: FuckingRemoteProcedureRequest): Promisoid<JSONResponse> = callDangerousMatumba(req)



abstract class DeleteRequest : RequestMatumba() {
    class Response : CommonResponseFieldsImpl()
    val id by longHiddenField()
}

class UADeleteOrderFileRequest : DeleteRequest()
class UADeleteOrderRequest : DeleteRequest()
class DeleteUserRequest : DeleteRequest()

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
        UADocumentType.ABSTRACT -> 123
        UADocumentType.LAB -> 123
        UADocumentType.TEST -> 123
        UADocumentType.RGR -> 123
        UADocumentType.DRAWING -> 123
        UADocumentType.DISSERTATION -> 123
        UADocumentType.PRACTICE -> 123
        UADocumentType.OTHER -> 123
    }

abstract class CommonResponseFieldsImpl : CommonResponseFields {
    override lateinit var backendVersion: String
}

//class LoadUAOrderRequest : RequestMatumba() {
//    val id by longHiddenField()
//    class Response(override val entity: UAOrderRTO) : CommonResponseFieldsImpl(), EntityResponse<UAOrderRTO>
//}


class PingRequest : RequestMatumba()

class LuceneParseToken {
    var type: String by notNull()
    var startOffset: Int by notNull()
    var endOffset: Int by notNull()
    var text: String by notNull()

    override fun toString() =
        "LuceneParseToken(type='$type', startOffset=$startOffset, endOffset=$endOffset, text='$text')"
}














//    val deadline = DateTimeField(this, "deadline", t("Deadline", "Срок"))











