package aps

import aps.Color.*

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
    val price: Int?,
    val numPages: Int,
    val numSource: Int,
    val details: String,
    val adminNotes: String,
    val state: UAOrderState,
    val phone: String
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
    val insertedAt: Long,
    val updatedAt: Long
)

class UAOrderFileRTO(
    val id: String,
    val file: FileRTO,
    val seenAsFrom: UserKind,
    val editable: Boolean,
    val insertedAt: Long,
    val updatedAt: Long
)

enum class UADocumentType(override val title: String) : Titled {
    ABSTRACT(t("TOTE", "Реферат")),
    COURSE(t("TOTE", "Курсовая работа")),
    GRADUATION(t("TOTE", "Дипломная работа")),
    LAB(t("TOTE", "Лабораторная работа")),
    TEST(t("TOTE", "Контрольная работа")),
    RGR(t("TOTE", "РГР")),
    DRAWING(t("TOTE", "Чертеж")),
    DISSERTATION(t("TOTE", "Диссертация")),
    ESSAY(t("TOTE", "Эссе (сочинение)")),
    PRACTICE(t("TOTE", "Отчет по практике")),
    OTHER(t("TOTE", "Другое"))
}

enum class UAOrderState(override val title: String, val labelBackground: Color) : Titled {
    CREATED(t("TOTE", "Создан"), GRAY_300),
    CUSTOMER_DRAFT(t("TOTE", "Черновик"), GRAY_300),
    LOOKING_FOR_WRITERS(t("TOTE", "Ищем писателей"), LIGHT_BLUE_100),
    WAITING_FOR_PAYMENT(t("TOTE", "Ждем оплаты"), GRAY_300),
    WRITER_ASSIGNED(t("TOTE", "Писатель назначен"), GRAY_300),
    WAITING_EMAIL_CONFIRMATION(t("TOTE", "Ждем подтверждения имейла"), GRAY_300),
    WAITING_ADMIN_APPROVAL(t("TOTE", "Ждем одобрения админом"), GRAY_300)
}














