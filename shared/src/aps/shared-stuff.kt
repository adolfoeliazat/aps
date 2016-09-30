/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps

//sealed class TypeDeserializationStrategy {
//    class Simple: TypeDeserializationStrategy()
//    class Enum(val enumClassName: String): TypeDeserializationStrategy()
//    class Class(val className: String): TypeDeserializationStrategy()
//    class List(val itemStrategy: TypeDeserializationStrategy): TypeDeserializationStrategy()
//}

//class FieldDeserializationInfo(val fieldName: String, val strategy: TypeDeserializationStrategy)

//abstract class RemoteProcedureRequest {
//
//}

//abstract class RemoteProcedureResponse {
//    var error: String? = null
//}
//
//class HiRequest(var name: String? = null) : RemoteProcedureRequest()
//
//class HiResponse : RemoteProcedureResponse() {
//    var saying: String? = null
//    var backendInstance: String? = null
//
//    override fun toString(): String{
//        return "HiResponse(saying='$saying', backendInstance='$backendInstance')"
//    }
//}

class FieldError(val field: String, val error: String)

enum class UserRole {
    SUPPORT
}

enum class UserKind() {
    CUSTOMER, WRITER, ADMIN
}

enum class Language() {
    EN, UA
}

enum class UserState() {
    COOL, PROFILE_PENDING, PROFILE_APPROVAL_PENDING, PROFILE_REJECTED, BANNED
}


@Target(AnnotationTarget.CLASS)
annotation class RemoteTransferObject

@RemoteTransferObject
class TimestampRTO(val value: String) {
}

@RemoteTransferObject
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
    val aboutMe: String?
)

val SHITS = "--SHIT--"
val SHITB = false

class ImposeNextRequestTimestampRequest(val stamp: String) : Request() {
    constructor() : this(SHITS) // For fucking Jackson
}

class ResetTestDatabaseRequest(val templateDB: String, val recreateTemplate: Boolean = false) : Request() {
    constructor() : this(SHITS, SHITB) // For fucking Jackson
}

class GenericResponse

class ResetTestDatabaseResponse

sealed class FormResponse {
    class Hunky<Meat>(val meat: Meat): FormResponse()
    class Shitty(val error: String, val fieldErrors: Iterable<FieldError>): FormResponse()
}

class SignInWithPasswordResponse {
    lateinit var token: String
    lateinit var user: UserRTO
}

class UpdateProfileResponse {
    lateinit var newUser: UserRTO
}

interface ProfileFields {
    //    var phone: String
    var aboutMe: String
}

//class SignInWithPasswordRequest : Request() {
//    lateinit var email: String
//    lateinit var password: String
//}

open class Request {
    var token: String? = null
    var fields = mapOf<String, String?>()
}
























