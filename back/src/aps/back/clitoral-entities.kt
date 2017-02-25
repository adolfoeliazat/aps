package aps.back

import aps.*
import into.kommon.*
import org.springframework.data.repository.CrudRepository
import java.sql.Timestamp
import javax.persistence.*

private const val MAX_STRING = 10000
private const val MAX_BLOB = 10 * 1024 * 1024

val userRepo get() = springctx.getBean(UserRepository::class.java)!!
val userParamsHistoryItemRepo get() = springctx.getBean(UserParamsHistoryItemRepository::class.java)!!
val uaOrderRepo get() = springctx.getBean(UAOrderRepository::class.java)!!
val uaOrderFileRepo get() = springctx.getBean(UAOrderFileRepository::class.java)!!

private fun currentTimestampForEntity(): Timestamp {
    return when {
        isRequestThread() -> RequestGlobus.stamp
        else -> Timestamp(currentTimeMillis())
    }
}

@MappedSuperclass
abstract class ClitoralEntity0 {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @PreUpdate
    fun preFuckingUpdate() {
        if (this is User) {
            saveUserParamsHistory(this)
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
    @Column(length = MAX_STRING) var banReason: String? = null
) : FieldsWithAdminNotes


@Entity @Table(name = "users",
               indexes = arrayOf(Index(columnList = "email")))
class User(@Embedded var fields: UserFields)
    : MeganItem<UserRTO>, ClitoralEntity0()
{
    override val idBang get()= id!!

    override fun toRTO(searchWords: List<String>): UserRTO {
        val title = "${fields.firstName} ${fields.lastName}"
        return UserRTO(
            id = id!!,
            createdAt = fields.common.createdAt.time,
            updatedAt = fields.common.updatedAt.time,
            profileUpdatedAt = fields.profileUpdatedAt?.time,
            kind = fields.kind,
            lang = Language.UA,
            email = fields.email,
            state = fields.state,
            profileRejectionReason = fields.profileRejectionReason,
            banReason = fields.banReason,
            adminNotes = fields.adminNotes,
            adminNotesHighlightRanges = highlightRanges(fields.adminNotes, searchWords),
            firstName = fields.firstName,
            lastName = fields.lastName,
            aboutMe = fields.aboutMe,
            aboutMeHighlightRanges = highlightRanges(fields.aboutMe, searchWords),
            roles = setOf(),
            profilePhone = fields.profilePhone,
            editable = false,
            title = title,
            titleHighlightRanges = highlightRanges(title, searchWords)
        )
    }
}

interface UserRepository : CrudRepository<User, Long> {
    fun findByFields_Email(x: String): User?
    fun countByFields_KindAndFields_State(kind: UserKind, state: UserState): Long
}

@Embeddable class HistoryFields(
    val historyItem_entityID: Long,
    @Column(length = MAX_STRING) var historyItem_descr: String,
    var historyItem_createdAt: Timestamp = currentTimestampForEntity()
)

@Entity @Table(name = "user_params_history_items",
               indexes = arrayOf())
class UserParamsHistoryItem(
    @Embedded var historyFields: HistoryFields,
    @Embedded var entityFields: UserFields
)
    : ClitoralEntity0()
{
}

interface UserParamsHistoryItemRepository : CrudRepository<UserParamsHistoryItem, Long> {
}

fun saveUserToRepo(entity: User, entityRepo: UserRepository = userRepo, historyRepo: UserParamsHistoryItemRepository = userParamsHistoryItemRepo): User {
    val res = entityRepo.save(entity)
    saveUserParamsHistory(entity, historyRepo, descr = "Created shit")
    return res
}

fun saveUserParamsHistory(entity: User, historyRepo: UserParamsHistoryItemRepository = userParamsHistoryItemRepo, descr: String = "Updated shit") {
    historyRepo.save(
        UserParamsHistoryItem(
            historyFields = HistoryFields(
                historyItem_entityID = entity.idBang,
                historyItem_descr = descr
            ),
            entityFields = entity.fields.copy()
        )
    )
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

    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "customerID", nullable = true)
    var customer: User? // TODO:vgrechka Think about nullability of this shit. Order can be draft, before customer even confirmed herself
) : FieldsWithAdminNotes

@Entity @Table(name = "ua_orders",
               indexes = arrayOf(Index(columnList = "confirmationSecret")))
class UAOrder(@Embedded var fields: UAOrderFields)
    : ClitoralEntity0(), MeganItem<UAOrderRTO>
{
    override val idBang get()= id!!

    override fun toRTO(searchWords: List<String>): UAOrderRTO {
        return UAOrderRTO(
            id = id!!,
            title = fields.title,
            titleHighlightRanges = highlightRanges(fields.title, searchWords),
            detailsHighlightRanges = highlightRanges(fields.details, searchWords),
            editable = true, // TODO:vgrechka ...
            createdAt = fields.common.createdAt.time,
            updatedAt = fields.common.updatedAt.time,
            customer = fields.customer!!.toRTO(searchWords = listOf()),
            documentType = fields.documentType,
            price = -1,
            numPages = fields.numPages,
            numSources = fields.numSources,
            details = fields.details,
            adminNotes = fields.adminNotes,
            adminNotesHighlightRanges = highlightRanges(fields.adminNotes, searchWords),
            state = fields.state,
            customerPhone = fields.customerPhone,
            customerFirstName = fields.customerFirstName,
            customerLastName = fields.customerLastName,
            customerEmail = fields.customerEmail,
            whatShouldBeFixedByCustomer = fields.whatShouldBeFixedByCustomer
        )
    }
}

interface UAOrderRepository : CrudRepository<UAOrder, Long> {
    fun findByFields_ConfirmationSecret(x: String): UAOrder?
    fun countByFields_State(x: UAOrderState): Long
}

@Entity @Table(name = "user_tokens",
               indexes = arrayOf(Index(columnList = "token")))
class UserToken(
    @Column(length = MAX_STRING) var token: String,

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "userID", nullable = false)
    var user: User?
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

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "creatorID", nullable = false)
    var creator: User,

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "orderID", nullable = false)
    var order: UAOrder
) : FieldsWithAdminNotes


@Entity @Table(name = "ua_order_files",
               indexes = arrayOf())
class UAOrderFile(@Embedded var fields: UAOrderFileFields)
    : ClitoralEntity0(), MeganItem<UAOrderFileRTO>
{
    override val idBang get()= id!!

    override fun toRTO(searchWords: List<String>): UAOrderFileRTO {
        return UAOrderFileRTO(
            id = id!!,
            createdAt = fields.common.createdAt.time,
            updatedAt = fields.common.updatedAt.time,
            name = fields.name,
            title = fields.title,
            details = fields.details,
            sizeBytes = fields.sizeBytes,
            detailsHighlightRanges = highlightRanges(fields.details, searchWords),
            editable = run {
                val user = requestUser
                when (user.fields.kind) {
                    UserKind.ADMIN -> true
                    UserKind.CUSTOMER -> fields.order.fields.state in setOf(UAOrderState.CUSTOMER_DRAFT, UAOrderState.RETURNED_TO_CUSTOMER_FOR_FIXING)
                    UserKind.WRITER -> imf("UAOrderFileRTO.editable for writer")
                }
            },
            nameHighlightRanges = when {
                searchWords.isEmpty() -> listOf()
                else -> highlightRanges(fields.name.chopOffFileExtension(), searchWords)
            },
            seenAsFrom = when (requestUser.fields.kind) {
                UserKind.CUSTOMER -> fields.forCustomerSeenAsFrom
                UserKind.WRITER -> fields.forWriterSeenAsFrom
                UserKind.ADMIN -> fields.creator.fields.kind
            },
            titleHighlightRanges = when {
                searchWords.isEmpty() -> listOf()
                else -> highlightRanges(fields.title, searchWords)
            },
            adminNotes = fields.adminNotes,
            adminNotesHighlightRanges = when {
                searchWords.isEmpty() -> listOf()
                else -> highlightRanges(fields.adminNotes, searchWords)
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






















