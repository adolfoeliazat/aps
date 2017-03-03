package aps

abstract class FieldSpec : Fucker() {
    abstract val name: String
}

data class TextFieldSpec(
    override val name: String,
    val title: String,
    val type: TextFieldType,
    val minLen: Int,
    val maxLen: Int,
    val minDigits: Int = -1,
    val optionalHint: Boolean = false
) : FieldSpec()

enum class TextFieldType {
    STRING, TEXTAREA, PASSWORD, PHONE, EMAIL
}

class CheckboxFieldSpec(
    override val name: String,
    val title: String,
    val mandatoryYesError: String? = null
) : FieldSpec()

class SelectFieldSpec<T>(
    override val name: String,
    val title: String,
    val values: Array<T>
) : FieldSpec() where T : Enum<T>, T : Titled

sealed class IntFieldType {
    class Generic : IntFieldType()
    class Money(val fractions: Boolean = false) : IntFieldType()
    class Duration : IntFieldType()
}

class IntFieldSpec(
    override val name: String,
    val title: String,
    val type: IntFieldType = IntFieldType.Generic(),
    val min: Int,
    val max: Int
) : FieldSpec()

data class FileFieldSpec(
    override val name: String,
    val title: String,
    val allowedValueKinds: Set<FileFieldValueKind>
) : FieldSpec()

enum class FileFieldValueKind {
    NONE, UNCHANGED, PROVIDED
}

class DocumentCategoryFieldSpec(
    override val name: String,
    val title: String
) : FieldSpec()



object IntFieldUtils {
    fun toInternal(spec: IntFieldSpec, x: Int): Int = when (spec.type) {
        is IntFieldType.Generic -> x
        is IntFieldType.Money -> {
            check(!spec.type.fractions){"758d27c0-b19d-4816-9bc6-165e3c17a081"}
            x * 100
        }
        is IntFieldType.Duration -> x * 24
    }

    fun fromInternal(spec: IntFieldSpec, x: Int): Int = when (spec.type) {
        is IntFieldType.Generic -> x
        is IntFieldType.Money -> {
            check(!spec.type.fractions){"f1ef17e2-5b1e-4ea3-868f-3160588d23e2"}
            x / 100
        }
        is IntFieldType.Duration -> x / 24
    }
}












