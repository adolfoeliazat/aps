package aps

import kotlin.properties.Delegates.notNull

class BrowserShot {
    var dataURL by notNull<String>()
    var windowScrollY by notNull<Double>()
}

class VisualShitCapturedRequest {

    class Reponse

    var id by notNull<String>()
    var shots by notNull<List<BrowserShot>>()
}

class JsonProcedureRequest : RequestMatumba() {
    class Response(val json: String): CommonResponseFieldsImpl()
    val json = StringHiddenField(this, "json")
}













//        override fun equals(other: Any?): Boolean {
//            if (this === other) return true
//            if (other !is Shot) return false
//            if (dataURL != other.dataURL) return false
//            if (windowScrollY != other.windowScrollY) return false
//            return true
//        }
//
//        override fun hashCode(): Int {
//            var result = dataURL.hashCode()
//            result = 31 * result + windowScrollY.hashCode()
//            return result
//        }
//
//        override fun toString(): String {
//            return "Shot(windowScrollY=$windowScrollY, dataURL='$dataURL')"
//        }

//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (other !is VisualShitCapturedRequest) return false
//        if (id != other.id) return false
//        if (shots != other.shots) return false
//        return true
//    }
//
//    override fun hashCode(): Int {
//        var result = id.hashCode()
//        result = 31 * result + shots.hashCode()
//        return result
//    }
//
//    override fun toString(): String {
//        return "VisualShitCapturedRequest(id='$id', shots=$shots)"
//    }

