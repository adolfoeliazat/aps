package aps.back

import aps.*
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.RepositoryDefinition
import java.sql.Timestamp
import javax.persistence.*

private const val MAX_STRING = 10000
private const val MAX_BLOB = 10 * 1024 * 1024

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
    @Column(length = MAX_STRING) var anonymousCustomerEmail: String?,
    @Column(length = MAX_STRING) var anonymousCustomerName: String?,
    @Column(length = MAX_STRING) var phone: String,

    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "customerId", nullable = true)
    var customer: User? // TODO:vgrechka Think about nullability of this shit. Order can be draft, before customer even confirmed herself
) : ClitoralEntity() {
    override fun toString() = "UAOrder(id=$id, title='$title', documentType=$documentType, numPages=$numPages, numSources=$numSources, details='$details', state=$state)"

    fun toRTO(): UAOrderRTO {
        return UAOrderRTO(
            id = id!!,
            title = title,
            insertedAt = createdAt.time,
            customer = customer!!.toRTO(),
            documentType = documentType,
            price = -1,
            numPages = numPages,
            numSources = numSources,
            details = details,
            adminNotes = "boobs",
            state = state,
            phone = phone
        )
    }
}

interface UAOrderRepository : CrudRepository<UAOrder, Long> {
    fun findByConfirmationSecret(x: String): UAOrder?
}

@Entity @Table(name = "user_tokens",
               indexes = arrayOf(
                   Index(columnList = "token")
               ))
class UserToken(
    @Column(length = MAX_STRING) var token: String,

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "userId", nullable = false)
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
    @Column(length = MAX_STRING) var adminNotes: String,
    @Column(length = MAX_STRING) var sha256: String,
    var sizeBytes: Int,
    @Column(length = MAX_BLOB) var content: ByteArray,
    // @Column(columnDefinition = "tsvector not null") var tsv: Any,

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "orderId", nullable = false)
    var order: UAOrder
) : ClitoralEntity() {

    fun toRTO(): UAOrderFileRTO {
        return UAOrderFileRTO(
            id = id!!,
            createdAt = createdAt.time,
            updatedAt = updatedAt.time,
            name = name,
            title = title,
            details = details,
            sizeBytes = sizeBytes,
            detailsHighlightRanges = listOf(),
            editable = true,
            nameHighlightRanges = listOf(),
            seenAsFrom = UserKind.CUSTOMER,
            titleHighlightRanges = listOf()
        )
    }
}

interface UAOrderFileRepository : CrudRepository<UAOrderFile, Long> {
}
























