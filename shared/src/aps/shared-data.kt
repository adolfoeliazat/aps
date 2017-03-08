package aps

import aps.Color.*

interface TabithaEntityRTO {
    val id: Long
}

//class HistoryItemRTO<RTO>(
//    val value: RTO,
//    val descr: String,
//    override var id: Long,
//    override var title: String,
//    override var editable: Boolean,
//    override var titleHighlightRanges: List<IntRangeRTO>
//) : MelindaItemRTO

class UserRTO(
    override var id: Long,
    override var title: String,
    override var titleHighlightRanges: List<IntRangeRTO>,
    val createdAt: Long,
    val updatedAt: Long,
    val profileUpdatedAt: Long?,
    val kind: UserKind,
    val lang: Language,
    val email: String,
    val state: UserState,
    val profileRejectionReason: String?,
    val banReason: String?,
    override var adminNotes: String,
    override var adminNotesHighlightRanges: List<IntRangeRTO>,
    val firstName: String,
    val lastName: String,
    val profilePhone: String,
    val aboutMe: String,
    val aboutMeHighlightRanges: List<IntRangeRTO>,
    val roles: Set<UserRole>,
    override var editable: Boolean,
    val allDocumentCategories: Boolean,
    val documentCategories: List<UADocumentCategoryRTO>
) : RTOWithAdminNotes, MelindaItemRTO, TabithaEntityRTO

class UAOrderRTO(
    override var id: Long,
    override var title: String,
    override var editable: Boolean,
    override var titleHighlightRanges: List<IntRangeRTO>,
    val createdAt: Long,
    val updatedAt: Long,
    val customer: UserRTO,
    val documentType: UADocumentType,
    val price: Int?,
    val numPages: Int,
    val numSources: Int,
    val details: String,
    val detailsHighlightRanges: List<IntRangeRTO>,
    override var adminNotes: String,
    override var adminNotesHighlightRanges: List<IntRangeRTO>,
    val state: UAOrderState,
    val customerPhone: String,
    val customerFirstName: String,
    val customerLastName: String,
    val whatShouldBeFixedByCustomer: String?,
    val customerEmail: String,
    var movedToStoreAt: Long?,
    var minAllowedPriceOffer: Int,
    var maxAllowedPriceOffer: Int,
    var minAllowedDurationOffer: Int,
    var maxAllowedDurationOffer: Int,
    val documentCategory: UADocumentCategoryRTO
) : MelindaItemRTO, RTOWithAdminNotes, TabithaEntityRTO

data class IntRangeRTO(
    val start: Int,
    val endInclusive: Int
)

interface MelindaItemRTO {
    val id: Long
    val title: String
    val editable: Boolean
    val titleHighlightRanges: List<IntRangeRTO>
}

interface RTOWithAdminNotes {
    val adminNotes: String
    val adminNotesHighlightRanges: List<IntRangeRTO>
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
    override var titleHighlightRanges: List<IntRangeRTO>,
    val details: String,
    val detailsHighlightRanges: List<IntRangeRTO>,
    override var adminNotes: String,
    override var adminNotesHighlightRanges: List<IntRangeRTO>,
    val sizeBytes: Int
) : MelindaItemRTO, RTOWithAdminNotes

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

enum class UAOrderState(override val title: String, val icon: XIcon? = null, val style: String = "") : Titled {
    CREATED(t("TOTE", "Создан"),                                            null, "background-color: green;"),
    CUSTOMER_DRAFT(t("TOTE", "Черновик"),                                   null, "background-color: green;"),
    LOOKING_WRITERS(t("TOTE", "Ищем писателей"),                            null, "background-color: green;"),
    WAITING_PAYMENT(t("TOTE", "Ждем оплаты"),                               null, "background-color: green;"),
    WRITER_ASSIGNED(t("TOTE", "Писатель назначен"),                         null, "background-color: green;"),
    WAITING_EMAIL_CONFIRMATION(t("TOTE", "Ждем подтверждения имейла"),      null, "background-color: green;"),
    WAITING_ADMIN_APPROVAL(t("TOTE", "Ждем одобрения админом"),             null, "background-color: $AMBER_100;"),
    RETURNED_TO_CUSTOMER_FOR_FIXING(t("TOTE", "Заказчик фиксит заявку"),    null, "background-color: $RED_50;"),
    IN_STORE(t("TOTE", "Ищем писателей"),                                   null, "background-color: $BLUE_100;")
}

interface HistoryItemRTOFields {
    val createdAt: Long
    val requester: UserRTO
    val thenRequester: UserRTO
}

class UserParamsHistoryItemRTO(
    val entity: UserRTO,
    val descr: String,

    // HistoryItemRTOFields
    override val createdAt: Long,
    override val requester: UserRTO,
    override val thenRequester: UserRTO,

    // MelindaItemRTO
    override val id: Long,
    override val title: String,
    override val editable: Boolean,
    override val titleHighlightRanges: List<IntRangeRTO>
) : MelindaItemRTO, HistoryItemRTOFields

class UADocumentCategoryRTO(
    val id: Long,
    val title: String,
    val pathTitle: String,
    val children: List<UADocumentCategoryRTO>
)

class BidRTO(
    override var id: Long,
    override var title: String,
    override var editable: Boolean,
    override var titleHighlightRanges: List<IntRangeRTO>,
    val createdAt: Long,
    val updatedAt: Long,
    override var adminNotes: String,
    override var adminNotesHighlightRanges: List<IntRangeRTO>
) : MelindaItemRTO, RTOWithAdminNotes










