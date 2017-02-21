package aps.back

import aps.*
import into.kommon.*
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.RepositoryDefinition
import java.sql.Timestamp
import javax.persistence.*

private const val MAX_STRING = 10000
private const val MAX_BLOB = 10 * 1024 * 1024

val uaOrderRepo get() = springctx.getBean(UAOrderRepository::class.java)!!
val uaOrderFileRepo get() = springctx.getBean(UAOrderFileRepository::class.java)!!

@MappedSuperclass
abstract class ClitoralEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var createdAt = when {
        isRequestThread() -> RequestGlobus.stamp
        else -> Timestamp(currentTimeMillis())
    }
    var updatedAt = createdAt
    var deleted = false

    fun touch() {
        updatedAt = RequestGlobus.stamp
    }

}

@Entity @Table(name = "users",
               indexes = arrayOf(
                   Index(columnList = "email")
               ))
class User(
    @Column(length = MAX_STRING) var email: String,
    @Column(length = MAX_STRING) var firstName: String?,
    @Column(length = MAX_STRING) var lastName: String?,
    @Column(length = MAX_STRING) var passwordHash: String,
    @Column(length = MAX_STRING) var phone: String,
    @Enumerated(EnumType.STRING) var kind: UserKind,
    @Enumerated(EnumType.STRING) var state: UserState
) : ClitoralEntity() {
    fun toRTO(): UserRTO {
        return UserRTO(
            id = id.toString(),
            deleted = deleted,
            insertedAt = createdAt.toPortable(),
            updatedAt = updatedAt.toPortable(),
            profileUpdatedAt = null,
            kind = kind,
            lang = Language.UA,
            email = email,
            state = state,
            profileRejectionReason = null,
            banReason = null,
            adminNotes = "",
            firstName = firstName ?: "fuck",
            lastName = lastName ?: "shit",
            phone = phone,
            compactPhone = phone,
            aboutMe = "",
            roles = setOf()
        )
    }
}

interface UserRepository : CrudRepository<User, Long> {
    fun findByEmail(x: String): User?
}

interface EntityWithAdminNotes {
    var adminNotes: String
}

@Entity @Table(name = "ua_orders",
               indexes = arrayOf(
                   Index(columnList = "confirmationSecret")
               ))
class UAOrder(
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
    override @Column(length = MAX_STRING) var adminNotes : String = "",

    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "customerID", nullable = true)
    var customer: User? // TODO:vgrechka Think about nullability of this shit. Order can be draft, before customer even confirmed herself
)
    : ClitoralEntity(), MeganItem<UAOrderRTO>, EntityWithAdminNotes
{
    override fun toString() = "UAOrder(id=$id, title='$title', documentType=$documentType, numPages=$numPages, numSources=$numSources, details='$details', state=$state)"

    override val idBang get()= id!!

    override fun toRTO(searchWords: List<String>): UAOrderRTO {
        return UAOrderRTO(
            id = id!!,
            title = title,
            titleHighlightRanges = highlightRanges(title, searchWords),
            detailsHighlightRanges = highlightRanges(details, searchWords),
            editable = true, // TODO:vgrechka ...
            createdAt = createdAt.time,
            updatedAt = updatedAt.time,
            customer = customer!!.toRTO(),
            documentType = documentType,
            price = -1,
            numPages = numPages,
            numSources = numSources,
            details = details,
            adminNotes = adminNotes,
            adminNotesHighlightRanges = highlightRanges(adminNotes, searchWords),
            state = state,
            customerPhone = customerPhone,
            customerFirstName = customerFirstName,
            customerLastName = customerLastName,
            customerEmail = customerEmail,
            whatShouldBeFixedByCustomer = whatShouldBeFixedByCustomer
        )
    }
}

interface UAOrderRepository : CrudRepository<UAOrder, Long> {
    fun findByConfirmationSecret(x: String): UAOrder?
    fun countByState(x: UAOrderState): Long
}

@Entity @Table(name = "user_tokens",
               indexes = arrayOf(
                   Index(columnList = "token")
               ))
class UserToken(
    @Column(length = MAX_STRING) var token: String,

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "userID", nullable = false)
    var user: User?
) : ClitoralEntity() {
}

interface UserTokenRepository : CrudRepository<UserToken, Long> {
    fun findByToken(x: String): UserToken?
}

@Entity @Table(name = "ua_order_files",
               indexes = arrayOf(
               ))
class UAOrderFile(
    @Column(length = MAX_STRING) var name: String,
    @Column(length = MAX_STRING) var title: String,
    @Column(length = MAX_STRING) var mime: String,
    @Column(length = MAX_STRING) var details: String,
    override @Column(length = MAX_STRING) var adminNotes: String,
    @Column(length = MAX_STRING) var sha256: String,
    var sizeBytes: Int,
    @Column(length = MAX_BLOB) var content: ByteArray,
    @Enumerated(EnumType.STRING) var forCustomerSeenAsFrom: UserKind,
    @Enumerated(EnumType.STRING) var forWriterSeenAsFrom: UserKind,

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "creatorID", nullable = false)
    var creator: User,

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "orderID", nullable = false)
    var order: UAOrder
)
    : ClitoralEntity(), MeganItem<UAOrderFileRTO>, EntityWithAdminNotes
{
    override val idBang get()= id!!

    override fun toRTO(searchWords: List<String>): UAOrderFileRTO {
        return UAOrderFileRTO(
            id = id!!,
            createdAt = createdAt.time,
            updatedAt = updatedAt.time,
            name = name,
            title = title,
            details = details,
            sizeBytes = sizeBytes,
            detailsHighlightRanges = highlightRanges(details, searchWords),
            editable = run {
                val user = requestUser
                when (user.kind) {
                    UserKind.ADMIN -> true
                    UserKind.CUSTOMER -> order.state in setOf(UAOrderState.CUSTOMER_DRAFT, UAOrderState.RETURNED_TO_CUSTOMER_FOR_FIXING)
                    UserKind.WRITER -> imf("UAOrderFileRTO.editable for writer")
                }
            },
            nameHighlightRanges = when {
                searchWords.isEmpty() -> listOf()
                else -> highlightRanges(name.chopOffFileExtension(), searchWords)
            },
            seenAsFrom = when (requestUser.kind) {
                UserKind.CUSTOMER -> forCustomerSeenAsFrom
                UserKind.WRITER -> forWriterSeenAsFrom
                UserKind.ADMIN -> creator.kind
            },
            titleHighlightRanges = when {
                searchWords.isEmpty() -> listOf()
                else -> highlightRanges(title, searchWords)
            },
            adminNotes = adminNotes,
            adminNotesHighlightRanges = when {
                searchWords.isEmpty() -> listOf()
                else -> highlightRanges(adminNotes, searchWords)
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






















