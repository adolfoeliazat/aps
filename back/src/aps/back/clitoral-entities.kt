package aps.back

import aps.*
import org.springframework.data.repository.CrudRepository
import java.sql.Timestamp
import javax.persistence.*
import javax.persistence.AccessType.PROPERTY

private const val MAX_STRING = 10000

@MappedSuperclass // @Access(PROPERTY)
abstract class ClitoralEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var createdAt = Timestamp(System.currentTimeMillis())
    var updatedAt = createdAt
    var deleted = false
}

@Entity @Table(name = "ua_orders")
open class UAOrder (
    @Column(length = MAX_STRING)
    var title: String,

    @Enumerated(EnumType.STRING)
    var documentType: UADocumentType,

    var numPages: Int,
    var numSources: Int,

    @Column(length = MAX_STRING)
    var details: String,

    @Enumerated(EnumType.STRING)
    var state: UAOrderState
) : ClitoralEntity() {
    override fun toString(): String {
        return "UAOrder(id=$id, title='$title', documentType=$documentType, numPages=$numPages, numSources=$numSources, details='$details', state=$state)"
    }
}

interface UAOrderRepository : CrudRepository<UAOrder, Long> {

}







