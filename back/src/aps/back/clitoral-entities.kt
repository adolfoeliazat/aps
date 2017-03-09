package aps.back

import aps.*
import into.kommon.*
import org.hibernate.annotations.GenericGenerator
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentityGenerator
import org.springframework.data.repository.CrudRepository
import java.sql.Timestamp
import javax.persistence.*

private const val MAX_STRING = 10000
private const val MAX_BLOB = 10 * 1024 * 1024

val userRepo get() = springctx.getBean(UserRepository::class.java)!!
val userTokenRepo get() = springctx.getBean(UserTokenRepository::class.java)!!
val userParamsHistoryItemRepo get() = springctx.getBean(UserParamsHistoryItemRepository::class.java)!!
val uaOrderRepo get() = springctx.getBean(UAOrderRepository::class.java)!!
val uaOrderFileRepo get() = springctx.getBean(UAOrderFileRepository::class.java)!!
val uaDocumentCategoryRepo get() = springctx.getBean(UADocumentCategoryRepository::class.java)!!
val userTimesDocumentCategoryRepo get() = springctx.getBean(UserTimesDocumentCategoryRepository::class.java)!!
val userParamsHistoryItemTimesDocumentCategoryRepo get() = springctx.getBean(UserParamsHistoryItemTimesDocumentCategoryRepository::class.java)!!
val bidRepo get() = springctx.getBean(BidRepository::class.java)!!


private fun currentTimestampForEntity(): Timestamp {
    return when {
        isRequestThread() -> RequestGlobus.stamp
        else -> Timestamp(currentTimeMillis())
    }
}

@MappedSuperclass
abstract class ClitoralEntity0 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "IdentityIfNotSetGenerator")
    @GenericGenerator(name = "IdentityIfNotSetGenerator", strategy = "aps.back.IdentityIfNotSetGenerator")
    var id: Long? = null

    @Transient
    var imposedIDToGenerate: Long? = null

    @PreUpdate
    fun preFuckingUpdate() {
        if (isRequestThread() && !RequestGlobus.shitIsDangerous) {
            if (this is User) {
                saveUserParamsHistory(this)
            }
        }
    }
}

@Suppress("Unused")
class IdentityIfNotSetGenerator : IdentityGenerator() {
    override fun generate(s: SharedSessionContractImplementor?, obj: Any?): java.io.Serializable {
        val entity = obj as ClitoralEntity0
        val id = entity.id
        val imposedIDToGenerate = entity.imposedIDToGenerate
        return when {
            id != null -> id
            imposedIDToGenerate != null -> imposedIDToGenerate
            else -> super.generate(s, obj)
        }
    }
}

@MappedSuperclass
abstract class ClitoralEntity : ClitoralEntity0() {
    var createdAt: Timestamp = currentTimestampForEntity()
    var updatedAt: Timestamp = createdAt
    var deleted: Boolean = false

    fun touch() {
        updatedAt = RequestGlobus.stamp
    }
}

@Embeddable
data class CommonFields(
    var createdAt: Timestamp = currentTimestampForEntity(),
    var updatedAt: Timestamp = createdAt,
    var deleted: Boolean = false
) {
    fun touch() {
        updatedAt = RequestGlobus.stamp
    }
}


//============================== User ==============================


@Entity @Table(name = "users",
               indexes = arrayOf(Index(columnList = "user_email")))
class User(
    @Embedded var user: UserFields,
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = arrayOf(CascadeType.ALL), orphanRemoval = true) var documentCategorySubscriptions: MutableList<UserTimesDocumentCategory> = mutableListOf()
)
    : MeganItem<UserRTO>, ClitoralEntity0()
{
    override val idBang get() = id!!

    override fun toRTO(searchWords: List<String>): UserRTO {
        return userLikeToRTO(idBang, user, documentCategorySubscriptions.map {it.category}, searchWords)
    }
}

@Embeddable
data class UserFields(
    @Embedded var common: CommonFields = CommonFields(),
    @Column(length = MAX_STRING) var firstName: String,
    @Column(length = MAX_STRING) var email: String,
    @Column(length = MAX_STRING) var lastName: String,
    @Column(length = MAX_STRING) var passwordHash: String,
    @Column(length = MAX_STRING) var profilePhone: String,
    @Enumerated(EnumType.STRING) var kind: UserKind,
    @Enumerated(EnumType.STRING) var state: UserState,
    override @Column(length = MAX_STRING) var adminNotes: String,
    var profileUpdatedAt: Timestamp? = null,
    @Column(length = MAX_STRING) var aboutMe: String = "",
    @Column(length = MAX_STRING) var profileRejectionReason: String? = null,
    @Column(length = MAX_STRING) var banReason: String? = null,
    var subscribedToAllCategories: Boolean
)
    : FieldsWithAdminNotes
{
}

fun userLikeToRTO(id: Long, uf: UserFields, documentCategories: List<UADocumentCategory>, searchWords: List<String>): UserRTO {
    val title = "${uf.firstName} ${uf.lastName}"
    return UserRTO(
        id = id,
        createdAt = uf.common.createdAt.time,
        updatedAt = uf.common.updatedAt.time,
        profileUpdatedAt = uf.profileUpdatedAt?.time,
        kind = uf.kind,
        lang = Language.UA,
        email = uf.email,
        state = uf.state,
        profileRejectionReason = uf.profileRejectionReason,
        banReason = uf.banReason,
        adminNotes = uf.adminNotes,
        adminNotesHighlightRanges = highlightRanges(uf.adminNotes, searchWords),
        firstName = uf.firstName,
        lastName = uf.lastName,
        aboutMe = uf.aboutMe,
        aboutMeHighlightRanges = highlightRanges(uf.aboutMe, searchWords),
        roles = setOf(),
        profilePhone = uf.profilePhone,
        editable = false,
        title = title,
        titleHighlightRanges = highlightRanges(title, searchWords),
        allDocumentCategories = uf.subscribedToAllCategories,
        documentCategories = documentCategories.map {it.toRTO()}
    )
}

interface UserRepository : CrudRepository<User, Long> {
    fun findByUser_Email(x: String): User?
    fun countByUser_KindAndUser_State(kind: UserKind, state: UserState): Long
}


//============================== UserDocumentCategorySubscription ==============================


@Entity @Table(name = "users__times__document_categories",
               indexes = arrayOf(Index(columnList = "user__id"),
                                 Index(columnList = "category__id")))
class UserTimesDocumentCategory(
    @Embedded var common: CommonFields = CommonFields(),
    @ManyToOne(fetch = FetchType.LAZY) var user: User,
    @ManyToOne(fetch = FetchType.LAZY) var category: UADocumentCategory
) : ClitoralEntity0()

interface UserTimesDocumentCategoryRepository : CrudRepository<UserTimesDocumentCategory, Long> {
}


//============================== UserParamsHistoryItemTimesDocumentCategory ==============================


@Entity @Table(name = "user_params_history_items__times__document_categories",
               indexes = arrayOf(Index(columnList = "historyItem__id"),
                                 Index(columnList = "category__id")))
class UserParamsHistoryItemTimesDocumentCategory(
    @Embedded var common: CommonFields = CommonFields(),
    @ManyToOne(fetch = FetchType.LAZY) var historyItem: UserParamsHistoryItem,
    @ManyToOne(fetch = FetchType.LAZY) var category: UADocumentCategory
) : ClitoralEntity0()

interface UserParamsHistoryItemTimesDocumentCategoryRepository : CrudRepository<UserParamsHistoryItemTimesDocumentCategory, Long> {
}








@Embeddable class HistoryFields(
    val entityID: Long,
    @Column(length = MAX_STRING) var descr: String,
    var createdAt: Timestamp = currentTimestampForEntity(),
    @ManyToOne(fetch = FetchType.LAZY) var requester: User,
    @Embedded var thenRequester: UserFields
)


//============================== UserParamsHistoryItem ==============================


@Entity @Table(name = "user_params_history_items",
               indexes = arrayOf(Index(columnList = "history_requester__id")))
class UserParamsHistoryItem(
    @Embedded var history: HistoryFields,
    @Embedded var entity: UserFields,
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "historyItem", cascade = arrayOf(CascadeType.ALL), orphanRemoval = true) var documentCategorySubscriptions: MutableList<UserParamsHistoryItemTimesDocumentCategory>
)
    : ClitoralEntity0(), MeganItem<UserParamsHistoryItemRTO>
{
    override val idBang get() = id!!

    override fun toRTO(searchWords: List<String>): UserParamsHistoryItemRTO {
        val entityID = history.entityID
        val title = history.descr

        val changer = history.requester
        return UserParamsHistoryItemRTO(
            descr = history.descr,
            entity = userLikeToRTO(entityID, entity, documentCategorySubscriptions.map {it.category}, searchWords),

            // HistoryItemRTOFields
            createdAt = history.createdAt.time,
            requester = changer.toRTO(searchWords = listOf()),
            thenRequester = userLikeToRTO(changer.id!!, history.thenRequester,
                                          documentCategories = listOf(), // TODO:vgrechka Need "then document categories" of "then requester"?
                                          searchWords = listOf()),

            // MelindaItemRTO
            id = idBang,
            title = title,
            editable = false,
            titleHighlightRanges = highlightRanges(title, searchWords)
        )
    }
}


interface UserParamsHistoryItemRepository : CrudRepository<UserParamsHistoryItem, Long> {
    fun findTop2ByHistory_EntityIDOrderByIdDesc(x: Long): List<UserParamsHistoryItem>
}

fun saveUserToRepo(entity: User): User {
    val res = userRepo.save(entity)
    saveUserParamsHistory(entity, descr = "Created shit")
    return res
}

fun saveUserParamsHistory(entity: User, descr: String = "Updated shit") {
    val historyItem = userParamsHistoryItemRepo.save(
        UserParamsHistoryItem(
            history = HistoryFields(
                entityID = entity.idBang,
                descr = descr,
                requester = RequestGlobus.requesterOrAnonymous,
                thenRequester = RequestGlobus.requesterOrAnonymousInitialFields
            ),
            entity = entity.user.copy(),
            documentCategorySubscriptions = mutableListOf()
        )
    )

    for (entityCategorySubscription in entity.documentCategorySubscriptions) {
        historyItem.documentCategorySubscriptions.add(
            userParamsHistoryItemTimesDocumentCategoryRepo.save(
                UserParamsHistoryItemTimesDocumentCategory(
                    historyItem = historyItem,
                    category = entityCategorySubscription.category
                )))
    }
}

interface FieldsWithAdminNotes {
    var adminNotes: String
}

@Embeddable
data class UAOrderFields(
    @Embedded var common: CommonFields = CommonFields(),
    @Column(length = MAX_STRING) var title: String,
    @Enumerated(EnumType.STRING) var documentType: UADocumentType,
    var numPages: Int,
    var numSources: Int,
    @Column(length = MAX_STRING) var details: String,
    @Enumerated(EnumType.STRING) var state: UAOrderState,
    @Column(length = MAX_STRING) var confirmationSecret: String,
    @Column(length = MAX_STRING) var customerFirstName: String,
    @Column(length = MAX_STRING) var customerLastName: String,
    @Column(length = MAX_STRING) var customerPhone: String,
    @Column(length = MAX_STRING) var customerEmail: String,
    @Column(length = MAX_STRING) var whatShouldBeFixedByCustomer : String? = null,
    override @Column(length = MAX_STRING) var adminNotes: String,
    var movedToStoreAt: Timestamp? = null,
    var minAllowedPriceOffer: Int,
    var maxAllowedPriceOffer: Int,
    var minAllowedDurationOffer: Int,
    var maxAllowedDurationOffer: Int,
    @ManyToOne(fetch = FetchType.EAGER) var customer: User?, // TODO:vgrechka Think about nullability of this shit. Order can be draft, before customer even confirmed herself
    @ManyToOne(fetch = FetchType.LAZY) var category: UADocumentCategory,
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order") var bids: MutableList<Bid> = mutableListOf()
) : FieldsWithAdminNotes

@Entity @Table(name = "ua_orders",
               indexes = arrayOf(Index(columnList = "order_confirmationSecret"),
                                 Index(columnList = "order_category__id"),
                                 Index(columnList = "order_customer__id")))
class UAOrder(@Embedded var order: UAOrderFields)
    : ClitoralEntity0(), MeganItem<UAOrderRTO>
{
    override val idBang get()= id!!

    override fun toRTO(searchWords: List<String>): UAOrderRTO {
        return UAOrderRTO(
            id = id!!,
            title = order.title,
            titleHighlightRanges = highlightRanges(order.title, searchWords),
            detailsHighlightRanges = highlightRanges(order.details, searchWords),
            editable = true, // TODO:vgrechka ...
            createdAt = order.common.createdAt.time,
            updatedAt = order.common.updatedAt.time,
            customer = order.customer!!.toRTO(searchWords = listOf()),
            documentType = order.documentType,
            price = -1,
            numPages = order.numPages,
            numSources = order.numSources,
            details = order.details,
            adminNotes = order.adminNotes,
            adminNotesHighlightRanges = highlightRanges(order.adminNotes, searchWords),
            state = order.state,
            customerPhone = order.customerPhone,
            customerFirstName = order.customerFirstName,
            customerLastName = order.customerLastName,
            customerEmail = order.customerEmail,
            whatShouldBeFixedByCustomer = order.whatShouldBeFixedByCustomer,
            movedToStoreAt = order.movedToStoreAt?.time,
            minAllowedPriceOffer = order.minAllowedPriceOffer,
            maxAllowedPriceOffer = order.maxAllowedPriceOffer,
            minAllowedDurationOffer = order.minAllowedDurationOffer,
            maxAllowedDurationOffer = order.maxAllowedDurationOffer,
            documentCategory = order.category.toRTO(),
            myBid = ifInStoreAndWriterLooking(this) {bidRepo.findByOrderAndBidder(this, requestUserEntity)?.toRTO(searchWords = listOf())},
            bidsSummary = ifInStoreAndWriterOrAdminLooking(this) {
                val bids = bidRepo.findByOrder(this)
                if (bids.isEmpty())
                    null
                else {
                    var firstBidAt = ValueAndWhetherMineRTO(Long.MAX_VALUE, false)
                    var lastBidAt = ValueAndWhetherMineRTO(Long.MIN_VALUE, false)
                    var minPriceOffer = ValueAndWhetherMineRTO(Int.MAX_VALUE, false)
                    var maxPriceOffer = ValueAndWhetherMineRTO(Int.MIN_VALUE, false)
                    var minDurationOffer = ValueAndWhetherMineRTO(Int.MAX_VALUE, false)
                    var maxDurationOffer = ValueAndWhetherMineRTO(Int.MIN_VALUE, false)
                    for (bid in bids) {
                        val mine = bid.bidder.id == requestUserEntity.id
                        if (bid.common.createdAt.time < firstBidAt.value) firstBidAt = ValueAndWhetherMineRTO(bid.common.createdAt.time, mine)
                        if (bid.common.createdAt.time > lastBidAt.value) lastBidAt = ValueAndWhetherMineRTO(bid.common.createdAt.time, mine)
                        if (bid.priceOffer < minPriceOffer.value) minPriceOffer = ValueAndWhetherMineRTO(bid.priceOffer, mine)
                        if (bid.priceOffer > maxPriceOffer.value) maxPriceOffer = ValueAndWhetherMineRTO(bid.priceOffer, mine)
                        if (bid.durationOffer < minDurationOffer.value) minDurationOffer = ValueAndWhetherMineRTO(bid.durationOffer, mine)
                        if (bid.durationOffer > maxDurationOffer.value) maxDurationOffer = ValueAndWhetherMineRTO(bid.durationOffer, mine)
                    }
                    BidsSummaryRTO(
                        numParticipants = bids.size,
                        minPriceOffer = minPriceOffer,
                        maxPriceOffer = maxPriceOffer,
                        minDurationOffer = minDurationOffer,
                        maxDurationOffer = maxDurationOffer,
                        firstBidAt = firstBidAt,
                        lastBidAt = lastBidAt
                    )
                }
            }
        )
    }
}

private fun <T> ifInStoreAndWriterLooking(order: UAOrder, block: () -> T): T? =
    ifInStore(order, {requestUser.kind in setOf(UserKind.WRITER)}, block)

private fun <T> ifInStoreAndWriterOrAdminLooking(order: UAOrder, block: () -> T): T? =
    ifInStore(order, {requestUser.kind in setOf(UserKind.WRITER, UserKind.ADMIN)}, block)

private fun <T> ifInStore(order: UAOrder, isAppropriateUser: () -> Boolean, block: () -> T): T? {
    if (!isAppropriateUser()) return null
    if (order.order.state != UAOrderState.IN_STORE) return null
    return block()
}


interface UAOrderRepository : CrudRepository<UAOrder, Long> {
    fun findByOrder_ConfirmationSecret(x: String): UAOrder?
    fun countByOrder_State(x: UAOrderState): Long
}

@Entity @Table(name = "user_tokens",
               indexes = arrayOf(Index(columnList = "user__id"),
                                 Index(columnList = "token")))
class UserToken(
    @Column(length = MAX_STRING) var token: String,
    @ManyToOne(fetch = FetchType.LAZY) var user: User?
) : ClitoralEntity()

interface UserTokenRepository : CrudRepository<UserToken, Long> {
    fun findByToken(x: String): UserToken?
}


@Embeddable
data class UAOrderFileFields(
    @Embedded var common: CommonFields = CommonFields(),
    @Column(length = MAX_STRING) var name: String,
    @Column(length = MAX_STRING) var title: String,
    @Column(length = MAX_STRING) var mime: String,
    @Column(length = MAX_STRING) var details: String,
    override @Column(length = MAX_STRING) var adminNotes: String,
    @Column(length = MAX_STRING) var sha256: String,
    var sizeBytes: Int,
    @Suppress("ArrayInDataClass") @Column(length = MAX_BLOB) var content: ByteArray,
    @Enumerated(EnumType.STRING) var forCustomerSeenAsFrom: UserKind,
    @Enumerated(EnumType.STRING) var forWriterSeenAsFrom: UserKind,
    @ManyToOne(fetch = FetchType.LAZY) var creator: User,
    @ManyToOne(fetch = FetchType.LAZY) var order: UAOrder
) : FieldsWithAdminNotes


@Entity @Table(name = "ua_order_files",
               indexes = arrayOf(Index(columnList = "orderfile_creator__id"),
                                 Index(columnList = "orderfile_order__id")))
class UAOrderFile(@Embedded var orderFile: UAOrderFileFields)
    : ClitoralEntity0(), MeganItem<UAOrderFileRTO>
{
    override val idBang get()= id!!

    override fun toRTO(searchWords: List<String>): UAOrderFileRTO {
        return UAOrderFileRTO(
            id = id!!,
            createdAt = orderFile.common.createdAt.time,
            updatedAt = orderFile.common.updatedAt.time,
            name = orderFile.name,
            title = orderFile.title,
            details = orderFile.details,
            sizeBytes = orderFile.sizeBytes,
            detailsHighlightRanges = highlightRanges(orderFile.details, searchWords),
            editable = run {
                val user = requestUserEntity
                when (user.user.kind) {
                    UserKind.ADMIN -> true
                    UserKind.CUSTOMER -> orderFile.order.order.state in setOf(UAOrderState.CUSTOMER_DRAFT, UAOrderState.RETURNED_TO_CUSTOMER_FOR_FIXING)
                    UserKind.WRITER -> imf("UAOrderFileRTO.editable for writer")
                }
            },
            nameHighlightRanges = when {
                searchWords.isEmpty() -> listOf()
                else -> highlightRanges(orderFile.name.chopOffFileExtension(), searchWords)
            },
            seenAsFrom = when (requestUserEntity.user.kind) {
                UserKind.CUSTOMER -> orderFile.forCustomerSeenAsFrom
                UserKind.WRITER -> orderFile.forWriterSeenAsFrom
                UserKind.ADMIN -> orderFile.creator.user.kind
            },
            titleHighlightRanges = when {
                searchWords.isEmpty() -> listOf()
                else -> highlightRanges(orderFile.title, searchWords)
            },
            adminNotes = orderFile.adminNotes,
            adminNotesHighlightRanges = when {
                searchWords.isEmpty() -> listOf()
                else -> highlightRanges(orderFile.adminNotes, searchWords)
            }
        )
    }
}

interface UAOrderFileRepository : CrudRepository<UAOrderFile, Long> {
}




private fun highlightRanges(text: String, searchWords: List<String>): List<IntRangeRTO> {
    val lang = Language.UA
    val analyzer = when (lang) {
        Language.UA -> russianAnalyzer
        else -> imf("Support analyzing for $lang")
    }
    return when {
        searchWords.isEmpty() -> listOf()
        else -> luceneHighlightRanges(text, searchWords, analyzer)
    }
}


//============================== UADocumentCategory ==============================

// TODO:vgrechka Ditch UADocumentCategoryFields?

@Entity @Table(name = "ua_document_categories",
               indexes = arrayOf(Index(columnList = "category_parent__id")))
class UADocumentCategory(@Embedded var category: UADocumentCategoryFields)
    : ClitoralEntity0()
{

    fun toRTO(loadChildren: Boolean = false): UADocumentCategoryRTO {
        var pathTitle = category.title
        var parent = category.parent
        while (parent != null && parent.id != const.uaDocumentCategoryID.root) {
            pathTitle = parent.category.title + const.text.symbols.rightDoubleAngleQuotationSpaced + pathTitle
            parent = parent.category.parent
        }
        return UADocumentCategoryRTO(
            id = id!!,
            title = category.title,
            pathTitle = pathTitle,
            children = when {
                loadChildren -> category.children.map {it.toRTO(loadChildren = true)}
                else -> listOf()
            }
        )
    }
}

@Embeddable
data class UADocumentCategoryFields(
    @Embedded var common: CommonFields = CommonFields(),
    @Column(length = MAX_STRING) var title: String,
    @ManyToOne(fetch = FetchType.LAZY) var parent: UADocumentCategory?,
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category.parent") var children: MutableList<UADocumentCategory>
)

interface UADocumentCategoryRepository : CrudRepository<UADocumentCategory, Long> {
}


//============================== Bid ==============================


@Entity @Table(name = "bids",
               indexes = arrayOf(Index(columnList = "order__id")))
class Bid(
    @Embedded var common: CommonFields = CommonFields(),
    var priceOffer: Int,
    var durationOffer: Int,
    @Column(length = MAX_STRING) var comment: String,
    override @Column(length = MAX_STRING) var adminNotes: String,
    @ManyToOne(fetch = FetchType.LAZY) var order: UAOrder,
    @ManyToOne(fetch = FetchType.LAZY) var bidder: User
)
    : ClitoralEntity0(), FieldsWithAdminNotes
{
    fun toRTO(searchWords: List<String>): BidRTO {
        val title = "pizda"
        return BidRTO(
            id = id!!,
            title = title,
            editable = false,
            titleHighlightRanges = highlightRanges(title, searchWords),
            createdAt = common.createdAt.time,
            updatedAt = common.updatedAt.time,
            adminNotes = adminNotes,
            adminNotesHighlightRanges = highlightRanges(adminNotes, searchWords),
            priceOffer = priceOffer,
            durationOffer = durationOffer,
            comment = comment
        )
    }
}

interface BidRepository : CrudRepository<Bid, Long> {
    fun findByOrderAndBidder(order: UAOrder, bidder: User): Bid?
    fun findByOrder(order: UAOrder): List<Bid>
}

















