package aps

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfoAlias
import kotlin.properties.Delegates.notNull

@JsonTypeInfoAlias(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
sealed class RedisLogMessage {
    lateinit var id: String
    var beginMillis: Long by notNull()
    lateinit var stamp: String
    lateinit var stack: String
    lateinit var text: String
    var shortDescription: String? = null

    class Separator : RedisLogMessage() {
        enum class Type {SEPARATOR, THICK_SEPARATOR, THICK_DASHED_SEPARATOR}
        lateinit var type: Type
    }

    class SQL : RedisLogMessage() {
        enum class Stage {PENDING, SUCCESS, FAILURE}
        lateinit var stage: Stage
        var exceptionStack: String? = null
    }
}


