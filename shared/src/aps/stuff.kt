/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps

sealed class TypeDeserializationStrategy {
    class Simple: TypeDeserializationStrategy()
    class Enum(val enumClassName: String): TypeDeserializationStrategy()
    class Class(val className: String): TypeDeserializationStrategy()
    class List(val itemStrategy: TypeDeserializationStrategy): TypeDeserializationStrategy()
}

class FieldDeserializationInfo(val fieldName: String, val strategy: TypeDeserializationStrategy)

abstract class RemoteProcedureRequest {

}

abstract class RemoteProcedureResponse {
    var error: String? = null
}

class HiRequest(var name: String? = null) : RemoteProcedureRequest()

class HiResponse : RemoteProcedureResponse() {
    var saying: String? = null
    var backendInstance: String? = null

    override fun toString(): String{
        return "HiResponse(saying='$saying', backendInstance='$backendInstance')"
    }
}

class SignInWithPasswordRequest(
    var email: String? = null,
    val password: String? = null) : RemoteProcedureRequest()

class FieldError(val field: String, val error: String)

abstract class FormProcedureResponse : RemoteProcedureResponse() {
    var fieldErrors = mutableListOf<FieldError>()
}

class SignInWithPasswordResponse : FormProcedureResponse() {
    var token: String? = null
    var user: UserTO? = null
}

enum class UserKind(val inDB: String) {
    CUSTOMER("customer"), WRITER("writer"), ADMIN("admin");
    override fun toString() = inDB
}

enum class Language(val inDB: String) {
    EN("en"), UA("ua");
    override fun toString() = inDB
}

class PortableTimestamp(val value: String) {
}

enum class UserState(val inDB: String) {
    COOL("cool"), PROFILE_APPROVAL_PENDING("profile_approval_pending"), PROFILE_REJECTED("profile_rejected"), BANNED("banned");
    override fun toString() = inDB
}

class UserTO(
    val id: String,
    val deleted: Boolean,
    val insertedAt: PortableTimestamp,
    val updatedAt: PortableTimestamp,
    val profileUpdatedAt: PortableTimestamp?,
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















