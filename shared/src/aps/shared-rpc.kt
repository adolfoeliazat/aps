package aps

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfoAlias
import kotlin.properties.Delegates.notNull

class BrowserShot {
    var dataURL by notNull<String>()
    var windowScrollY by notNull<Double>()
    var windowScrollYPhysical by notNull<Int>()
}

class VisualShitCapturedRequest {
    class Response(prevCaptureExists: Boolean)
    var id by notNull<String>()
    var shots by notNull<List<BrowserShot>>()
    var devicePixelRatio by notNull<Double>()
    var headerHeight by notNull<Double>()
    var contentWidth by notNull<Double>()
    var contentLeft by notNull<Double>()
    var containerHeightPhysical by notNull<Int>()
    var modal by notNull<Boolean>()
}

class SaveCapturedVisualShitRequest {
    class Response
}

class CapturedVisualShitExistsRequest {
    class Response(val exists: Boolean)
    var id by notNull<String>()
}

class GetCapturedVisualShitRequest {
    class Response(val base64: String)
    var id by notNull<String>()
}

class GetCurrentCapturedVisualShitRequest {
    class Response(val base64: String)
}

class DiffCapturedVisualShitWithSavedRequest {
    class Response(val base64: String)
    var id by notNull<String>()
}

class ReturnMouseWhereItWasRequest {
    class Response
}

class MoveMouseAwayFromPageRequest {
    class Response
}

class HardenScreenHTMLRequest {
    class Response
    var assertionID by notNull<String>()
    var html by notNull<String>()
}

class TestCopyOrderFileToAreaRequest: RequestMatumba() {
    class Response(val orderFileID: String) : CommonResponseFieldsImpl()
    val orderFileID = StringHiddenField(this, "orderFileID")
    val areaName = StringHiddenField(this, "areaName")
    val permissionForUserID = StringHiddenField(this, "permissionForUserID")
}


class JsonProcedureRequest : RequestMatumba() {
    class Response(val json: String): CommonResponseFieldsImpl()
    val json = StringHiddenField(this, "json")
}

class FieldValueRTO(val fieldName: String, val value: Any?)

class RecordRTO(val fieldValues: List<FieldValueRTO>)

class TestTakeSnapshotRequest: RequestMatumba() {
    class Response : CommonResponseFieldsImpl()
    val name by stringHiddenField()
    val url by stringHiddenField()
}

class TestLoadSnapshotRequest: RequestMatumba() {
    class Response(val url: String) : CommonResponseFieldsImpl()
    val name by stringHiddenField()
}

interface Xlobal {
    val user: UserRTO?
}

class UACustomerOrderRequestFields1(cont: RequestMatumba) {
    val documentType = SelectField(cont, fields.uaDocumentType)
    val documentTitle = TextField(cont, fields.documentTitle)
    val numPages = IntField(cont, fields.numPages)
    val numSources = IntField(cont, fields.numSources)
    val documentDetails = TextField(cont, fields.orderDetails)
}

class UACustomerOrderRequestFields2(cont: RequestMatumba) {
    val phone = TextField(cont, fields.orderCustomerPhone)
}

class UACustomerCreateOrderRequest(xlobal: Xlobal) : RequestMatumba() {
    val fields1 = UACustomerOrderRequestFields1(this)

    var firstName by notNullOnce<TextField>()
    init { // TODO:vgrechka Simplify this shit
        if (xlobal.user == null)
            firstName = TextField(this, fields.orderCustomerFirstName)
    }

    val lastName = TextField(this, fields.orderCustomerLastName)

    val fields2 = UACustomerOrderRequestFields2(this)

    var email by notNullOnce<TextField>()
    init { // TODO:vgrechka Simplify this shit
        if (xlobal.user == null)
            email = TextField(this, fields.orderCustomerEmail)
    }


    class Response(val id: Long) : CommonResponseFieldsImpl()
}

class UACustomerUpdateOrderRequest : RequestMatumba() {
    class Response : CommonResponseFieldsImpl()
    val entityID by longHiddenField()
    val fields1 = UACustomerOrderRequestFields1(this)
    val fields2 = UACustomerOrderRequestFields2(this)
}

class TestSQLFiddleRequest : RequestMatumba() {
    class Response(val spew: String, val isError: Boolean) : CommonResponseFieldsImpl()
    val input by stringHiddenField()
}

class TestCodeFiddleRequest : RequestMatumba() {
    class Response : CommonResponseFieldsImpl()
    val what by stringHiddenField()
}

class UserSignedInAsPartOfMakingOrder(
    val user: UserRTO,
    val token: String
)

class ConfirmOrderRequest : RequestMatumba() {
    class Response(
        val orderId: String,
        val userSignedInAsPartOfMakingOrder: UserSignedInAsPartOfMakingOrder?
    ) : CommonResponseFieldsImpl()

    val secret by stringHiddenField()
}

class TestTakeTestPointSnapshotRequest : RequestMatumba() {
    class Response : CommonResponseFieldsImpl()
    val snapshotName by stringHiddenField()
    val browseroidName by stringHiddenField()
    val href by stringHiddenField()
    val token by maybeStringHiddenField()
    val nextRequestTimestampIndex by intHiddenField()
}

class TestRestoreTestPointSnapshotRequest : RequestMatumba() {
    class Response(
        val browseroidName: String,
        val href: String,
        val token: String?,
        val nextRequestTimestampIndex: Int
    ) : CommonResponseFieldsImpl()

    val snapshotName by stringHiddenField()
}

class TestGetFileUploadDataRequest : RequestMatumba() {
    class Response(val name: String, val size: Int, val base64: String) : CommonResponseFieldsImpl()
    val fileName by stringHiddenField()
}

class FileFields1(cont: RequestMatumba) {
    val title = TextField(cont, fields.fileTitle)
    val details = TextField(cont, fields.fileDetails)
}

class UACreateOrderFileRequest : RequestMatumba() {
    val orderID by longHiddenField()
    val file = FileField(this, fields.fileFile_create)
    val fields1 = FileFields1(this)
    class Response(val id: Long) : CommonResponseFieldsImpl()
}

class UAUpdateOrderFileRequest : RequestMatumba() {
    val fileID by longHiddenField()
    val file = FileField(this, fields.fileFile_update)
    val fields1 = FileFields1(this)
    class Response(val updatedFile: UAOrderFileRTO) : CommonResponseFieldsImpl()
}

class UACreateOrderRequest : RequestMatumba() {
    class Response(val id: Long) : CommonResponseFieldsImpl()
}

class UAUpdateOrderRequest : RequestMatumba() {
    class Response(val updatedOrder: UAOrderRTO) : CommonResponseFieldsImpl()
}

class DownloadFileResponse(
    val fileName: String,
    val base64: String,
    val sha256: String) : CommonResponseFieldsImpl()

class UADownloadOrderFileRequest : RequestMatumba() {
    val fileID by longHiddenField()
}

class UACustomerSendOrderDraftForApprovalRequest : RequestMatumba() {
    class Response : CommonResponseFieldsImpl()
    val orderID by longHiddenField()
}

class UAAdminGetStuffToDoRequest : RequestMatumba() {
    class Response(val ordersToApprove: Long) : CommonResponseFieldsImpl()
}

class ReturnOrderToCustomerForFixingRequest : RequestMatumba() {
    val orderID by longHiddenField()
    val rejectionReason = TextField(this, fields.rejectionReason)
    class Response : CommonResponseFieldsImpl()
}


class MirandaRequest : RequestMatumba() {
    val params = ObjectHiddenField<MirandaParams>(this, "params")
    class Response : CommonResponseFieldsImpl()
}

annotation class NoArgCtor
annotation class AllOpen
annotation class Ser

// XXX `hack` param is necessary to actually generate no-arg constructor here
@Ser sealed class MirandaParams(hack: Unit = Unit)
@Ser class MirandaTestImposeNextGeneratedUserToken(val token: String) : MirandaParams()


class ReginaRequest : RequestMatumba() {
    val params = ObjectHiddenField<ReginaParams>(this, "params")
    class Response : CommonResponseFieldsImpl()
}

// XXX `hack` param is necessary to actually generate no-arg constructor here
@Ser sealed class ReginaParams(hack: Unit = Unit)
@Ser class ReginaCustomerSendOrderForApprovalAfterFixing(val orderID: Long) : ReginaParams()










