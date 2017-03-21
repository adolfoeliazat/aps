package aps

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfoAlias
import kotlin.properties.Delegates.notNull
import kotlin.reflect.KClass

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

class UserParamsRequest(isUpdate: Boolean) : RequestMatumba(), RequestWithAdminNotes {
    val userID by longHiddenField(include = isUpdate)
    val firstName = TextField(this, fields.signUpFirstName)
    val lastName = TextField(this, fields.signUpLastName)
    val email = TextField(this, fields.signUpEmail)
    val phone = TextField(this, fields.profilePhone)
    override val adminNotes = TextField(this, fields.adminNotes)
}

class CreateUserResponse(val userID: Long) : CommonResponseFieldsImpl()
class UpdateUserResponse : CommonResponseFieldsImpl()


class UAOrderParamsRequest(isAdmin: Boolean, isUpdate: Boolean) : RequestMatumba(), RequestWithAdminNotes {
    val orderID by longHiddenField(include = isUpdate)
    val documentType = SelectField(this, fields.uaDocumentType)
    val documentTitle = TextField(this, fields.documentTitle)
    val numPages = IntField(this, fields.numPages)
    val numSources = IntField(this, fields.numSources)
    val documentDetails = TextField(this, fields.orderDetails)
    val firstName = TextField(this, fields.orderCustomerFirstName)
    val lastName = TextField(this, fields.orderCustomerLastName)
    val email = TextField(this, fields.orderCustomerEmail)
    val phone = TextField(this, fields.orderCustomerPhone)
    override val adminNotes = TextField(this, fields.adminNotes, include = isAdmin)
}

class UACreateOrderResponse(val orderID: Long) : CommonResponseFieldsImpl()
class UAUpdateOrderResponse : CommonResponseFieldsImpl()

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

interface RequestWithAdminNotes {
    val adminNotes: TextField
}

class UAOrderFileParamsRequest(isAdmin: Boolean, isUpdate: Boolean) : RequestMatumba(), RequestWithAdminNotes {
    val orderID by longHiddenField(include = !isUpdate)
    val fileID by longHiddenField(include = isUpdate)
    val file = FileField(this, if (isUpdate) fields.fileFile_update else fields.fileFile_create)
    val title = TextField(this, fields.fileTitle)
    val details = TextField(this, fields.fileDetails)
    override val adminNotes = TextField(this, fields.adminNotes, include = isAdmin)
}

class UACreateOrderFileResponse(val fileID: Long) : CommonResponseFieldsImpl()
class UAUpdateOrderFileResponse(val updatedFile: UAOrderFileRTO) : CommonResponseFieldsImpl()


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
    class Response(
        val ordersToApprove: Long,
        val writerProfilesToApprove: Long,
        val bidsToConsider: Long
    ) : CommonResponseFieldsImpl()
}


abstract class RejectingRequest : RequestMatumba() {
    val entityID by longHiddenField()
    val rejectionReason = TextField(this, fields.rejectionReason)
}

class ReturnOrderToCustomerForFixingRequest : RejectingRequest()

class RejectProfileRequest : RejectingRequest()


class UAOrderStoreParamsRequest : RequestMatumba() {
    val orderID by longHiddenField()

    init {beginHorizontal()}
    val minAllowedPriceOffer = IntField(this, fields.minAllowedPriceOffer)
    val maxAllowedPriceOffer = IntField(this, fields.maxAllowedPriceOffer)
    init {endHorizontal()}

    init {beginHorizontal()}
    val minAllowedDurationOffer = IntField(this, fields.minAllowedDurationOffer)
    val maxAllowedDurationOffer = IntField(this, fields.maxAllowedDurationOffer)
    init {endHorizontal()}

    val uaDocumentCategory = DocumentCategoryField(this, fields.uaDocumentCategory)
}

class BidRequest(isAdmin: Boolean, isUpdate: Boolean) : RequestMatumba(), RequestWithAdminNotes {
    val orderID by longHiddenField()
    val bidPriceOffer = IntField(this, fields.bidPriceOffer)
    val bidDurationOffer = IntField(this, fields.bidDurationOffer)
    val bidComment = TextField(this, fields.bidComment)
    override val adminNotes = TextField(this, fields.adminNotes, include = isAdmin)
}

class ObjectRequest : RequestMatumba() {
    val params = ObjectHiddenField<Any>(this, "params")
}



class PairOfLastHistoryItems<out HistoryItemRTO : HistoryItemRTOFields>(
    val lastItem: HistoryItemRTO,
    val prelastItem: HistoryItemRTO?
)

class MirandaGetSoftwareVersionResult(val ctime: String, val backendInstanceID: String)













