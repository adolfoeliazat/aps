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

class UACustomer_OrderRequestFields1(cont: RequestMatumba) {
    val documentType = SelectField(cont, fieldSpecs.shebang.ua.documentType.ref)
    val documentTitle = TextField(cont, fieldSpecs.shebang.documentTitle.ref)
    val numPages = IntField(cont, fieldSpecs.shebang.numPages.ref)
    val numSources = IntField(cont, fieldSpecs.shebang.numSources.ref)
    val documentDetails = TextField(cont, fieldSpecs.shebang.documentDetails.ref)
}

class UACustomer_OrderRequestFields2(cont: RequestMatumba) {
    val phone = TextField(cont, fieldSpecs.shebang.phone.ref)
}

class UACustomerCreateOrderRequest(xlobal: Xlobal) : RequestMatumba() {
    class Response(val id: String) : CommonResponseFieldsImpl()

    val fields1 = UACustomer_OrderRequestFields1(this)
    var anonymousCustomerName by notNullOnce<TextField>()
        init {if (xlobal.user == null)
            anonymousCustomerName  = TextField(this, fieldSpecs.shebang.anonymousCustomerName.ref)}
    val fields2 = UACustomer_OrderRequestFields2(this)
    var anonymousCustomerEmail by notNullOnce<TextField>()
        init {if (xlobal.user == null)
            anonymousCustomerEmail = TextField(this, fieldSpecs.shebang.email.ref)}
}

class UACustomerUpdateOrderRequest : RequestMatumba() {
    class Response : CommonResponseFieldsImpl()
    val fields1 = UACustomer_OrderRequestFields1(this)
    val fields2 = UACustomer_OrderRequestFields2(this)
}

class TestSQLFiddleRequest : RequestMatumba() {
    class Response(val spew: String, val isError: Boolean) : CommonResponseFieldsImpl()
    val input by stringHiddenField()
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








