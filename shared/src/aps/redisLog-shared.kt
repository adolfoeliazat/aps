package aps

class RedisLogMessage() {
    enum class Type {SQL, SEPARATOR, THICK_SEPARATOR}

    constructor(type: Type, text: String) : this() {
        this.type = type
        this.text = text
    }

    lateinit var stamp: String
    lateinit var type: Type
    lateinit var text: String

    override fun toString(): String {
        return "RedisLogMessage(stamp='$stamp', type=$type, text='$text')"
    }
}


