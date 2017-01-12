package aps

class UserRTO(
    val id: String,
    val deleted: Boolean,
    val insertedAt: TimestampRTO,
    val updatedAt: TimestampRTO,
    val profileUpdatedAt: TimestampRTO?,
    val kind: UserKind,
    val lang: Language,
    val email: String,
    val state: UserState,
    val profileRejectionReason: String?,
    val banReason: String?,
    val adminNotes: String,
    val firstName: String,
    val lastName: String,
    val phone: String?,
    val compactPhone: String?,
    val aboutMe: String?,
    val roles: Set<UserRole>
)

class UAOrderRTO(
    val id: String,
    val title: String,
    val insertedAt: Long,
    val customer: UserRTO,
    val documentType: UADocumentType,
    val deadline: Long,
    val price: Int?,
    val numPages: Int,
    val numSource: Int,
    val details: String,
    val adminNotes: String,
    val state: UAOrderState
)

data class IntRangeRTO(
    val start: Int,
    val endInclusive: Int
)

class FileRTO(
    val id: String,
    val name: String,
    val nameHighlightRanges: List<IntRangeRTO>,
    val title: String,
    val titleHighlightRanges: List<IntRangeRTO>,
    val details: String,
    val detailsHighlightRanges: List<IntRangeRTO>,
    val sizeBytes: Int,
    val insertedAt: Long
)

class UAOrderFileRTO(
    val id: String,
    val file: FileRTO,
    val seenAsFrom: UserKind,
    val editable: Boolean
)















