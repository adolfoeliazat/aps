package aps.back

import aps.*
import into.kommon.*
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.RepositoryDefinition
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
abstract class ClitoralEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var createdAt = currentTimestampForEntity()
    var updatedAt = createdAt
    var deleted = false

    fun touch() {
        updatedAt = RequestGlobus.stamp
    }

}

@Embeddable @AllOpen @NoArgCtor
data class UserFields(
    @Column(length = MAX_STRING) var firstName: String,
    @Column(length = MAX_STRING) var email: String,
    @Column(length = MAX_STRING) var lastName: String,
    @Column(length = MAX_STRING) var passwordHash: String,
    @Column(length = MAX_STRING) var profilePhone: String,
    @Enumerated(EnumType.STRING) var kind: UserKind,
    @Enumerated(EnumType.STRING) var state: UserState,
    override @Column(length = MAX_STRING) var adminNotes: String = "",
    var profileUpdatedAt: Timestamp? = null,
    @Column(length = MAX_STRING) var aboutMe: String = "",
    @Column(length = MAX_STRING) var profileRejectionReason: String? = null,
    @Column(length = MAX_STRING) var banReason: String? = null
) : EntityWithAdminNotes


@Entity @Table(name = "users",
               indexes = arrayOf(
                   Index(columnList = "email")
               ))
class User(
    @Embedded var fields: UserFields
)
    : MeganItem<UserRTO>, ClitoralEntity()
{
    override val idBang get()= id!!

    override fun toRTO(searchWords: List<String>): UserRTO {
        val title = "${fields.firstName} ${fields.lastName}"
        return UserRTO(
            id = id!!,
            createdAt = createdAt.time,
            updatedAt = updatedAt.time,
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


@Entity @Table(name = "user_params_history_items",
               indexes = arrayOf(
               ))
class UserParamsHistoryItem(
    val historyItem_entityID: Long,
    @Column(length = MAX_STRING) var historyItem_descr: String,
    @Embedded var fields: UserFields
)
    : ClitoralEntity()
{
    var historyItem_createdAt: Timestamp = currentTimestampForEntity()
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
            historyItem_entityID = entity.idBang,
            historyItem_descr = descr,
            fields = entity.fields.copy()
        )-{o->
            o.createdAt = entity.createdAt
            o.updatedAt = entity.updatedAt
            o.deleted = entity.deleted
        }
    )
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
            customer = customer!!.toRTO(searchWords = listOf()),
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
                when (user.fields.kind) {
                    UserKind.ADMIN -> true
                    UserKind.CUSTOMER -> order.state in setOf(UAOrderState.CUSTOMER_DRAFT, UAOrderState.RETURNED_TO_CUSTOMER_FOR_FIXING)
                    UserKind.WRITER -> imf("UAOrderFileRTO.editable for writer")
                }
            },
            nameHighlightRanges = when {
                searchWords.isEmpty() -> listOf()
                else -> highlightRanges(name.chopOffFileExtension(), searchWords)
            },
            seenAsFrom = when (requestUser.fields.kind) {
                UserKind.CUSTOMER -> forCustomerSeenAsFrom
                UserKind.WRITER -> forWriterSeenAsFrom
                UserKind.ADMIN -> creator.fields.kind
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



@Entity @Table(name = "history_items",
               indexes = arrayOf(
               ))
class HistoryItem<Entity, EntityRTO>(
    val entityID: Long,
    @Column(length = MAX_STRING) var entityClassName: String,
    @Column(length = MAX_STRING) var descr: String
)
    : ClitoralEntity(), MeganItem<HistoryItemRTO<EntityRTO>>
{
    override val idBang get()= id!!

    override fun toRTO(searchWords: List<String>): HistoryItemRTO<EntityRTO> {
        val emf = springctx.getBean(EntityManagerFactory::class.java)
        val em = emf.createEntityManager()
        em.transaction.begin()
        try {
            val entityClass = Class.forName("aps.back.$entityClassName")
            val q = em.createQuery("select x from $entityClassName x where x.id = :entityID", entityClass)
            q.setParameter("entityID", entityID)
            val entity: ToRtoable<EntityRTO> = cast(q.singleResult)
            val title = "TODO e9d41cce-0e1b-4421-970c-e8e8acf7d2b4"
            return HistoryItemRTO(
                id = idBang,
                title = title,
                titleHighlightRanges = highlightRanges(title, searchWords),
                editable = false,
                value = entity.toRTO(searchWords),
                descr = descr
            )
        } finally {
            em.transaction.rollback()
            em.close()
        }
    }
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






















