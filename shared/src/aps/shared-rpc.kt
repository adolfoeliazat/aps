package aps

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
    val documentType = SelectField(cont, fields.shebang.ua.documentType)
    val documentTitle = TextField(cont, fields.shebang.documentTitle)
    val numPages = IntField(cont, fields.shebang.numPages)
    val numSources = IntField(cont, fields.shebang.numSources)
    val documentDetails = TextField(cont, fields.shebang.orderDetails)
}

class UACustomerOrderRequestFields2(cont: RequestMatumba) {
    val phone = TextField(cont, fields.shebang.phone)
}

class UACustomerCreateOrderRequest(xlobal: Xlobal) : RequestMatumba() {
    class Response(val id: String) : CommonResponseFieldsImpl()

    val fields1 = UACustomerOrderRequestFields1(this)
    var anonymousCustomerName by notNullOnce<TextField>()
        init {if (xlobal.user == null)
            anonymousCustomerName  = TextField(this, fields.shebang.anonymousCustomerName)}
    val fields2 = UACustomerOrderRequestFields2(this)
    var anonymousCustomerEmail by notNullOnce<TextField>()
        init {if (xlobal.user == null)
            anonymousCustomerEmail = TextField(this, fields.shebang.email)}
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
    val title = TextField(cont, fields.shebang.fileTitle)
    val details = TextField(cont, fields.shebang.fileDetails)
}

class UACreateOrderFileRequest : RequestMatumba() {
    class Response(val id: Long) : CommonResponseFieldsImpl()
    val orderID by longHiddenField()
    val file = FileField(this, fields.shebang.fileFile_create)
    val fields1 = FileFields1(this)
}

class UAUpdateOrderFileRequest : RequestMatumba() {
    class Response(val file: UAOrderFileRTO) : CommonResponseFieldsImpl()
    val fileID by longHiddenField()
    val file = FileField(this, fields.shebang.fileFile_update)
    val fields1 = FileFields1(this)
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










