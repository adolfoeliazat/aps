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
    override var id: Long,
    override var title: String,
    override var editable: Boolean,
    val insertedAt: Long,
    val customer: UserRTO,
    val documentType: UADocumentType,
    val price: Int?,
    val numPages: Int,
    val numSources: Int,
    val details: String,
    val adminNotes: String,
    val state: UAOrderState,
    val phone: String
) : MelindaItemRTO

data class IntRangeRTO(
    val start: Int,
    val endInclusive: Int
)

interface MelindaItemRTO {
    // XXX `var`s are needed because of KJS "reflection" hack...
    var id: Long
    var title: String
    var editable: Boolean
}

class UAOrderFileRTO(
    override var id: Long,
    val seenAsFrom: UserKind,
    override var editable: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val name: String,
    val nameHighlightRanges: List<IntRangeRTO>,
    override var title: String,
    val titleHighlightRanges: List<IntRangeRTO>,
    val details: String,
    val detailsHighlightRanges: List<IntRangeRTO>,
    val sizeBytes: Int
) : MelindaItemRTO

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














