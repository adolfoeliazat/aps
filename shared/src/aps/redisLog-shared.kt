package aps

//import com.fasterxml.jackson.annotation.JsonTypeInfo
//import com.fasterxml.jackson.annotation.JsonTypeInfoAlias
//import kotlin.properties.Delegates.notNull
//
//@JsonTypeInfoAlias(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
//sealed class RedisLogMessage {
//    companion object {
//        val ROOT_ID = "logRoot"
//    }
//
//    lateinit var id: String
//    lateinit var parentID: String
//    var beginMillis: Long by notNull()
//    var endMillis: Long? = null
//    lateinit var stamp: String
//    lateinit var stack: String
//    lateinit var text: String
//    var shortDescription: String? = null
//
//    class Fuck : RedisLogMessage() {
//        enum class Stage {PENDING, SUCCESS, FAILURE}
//        lateinit var stage: Stage
//        var exceptionStack: String? = null
//    }
//
//    class Separator : RedisLogMessage() {
//        enum class Type {SEPARATOR, THICK_SEPARATOR, THICK_DASHED_SEPARATOR}
//        lateinit var type: Type
//    }
//
//    class SQL : RedisLogMessage() {
//        enum class Stage {PENDING, SUCCESS, FAILURE}
//        lateinit var stage: Stage
//        var exceptionStack: String? = null
//        var result: List<RecordRTO>? = null
//    }
//}


