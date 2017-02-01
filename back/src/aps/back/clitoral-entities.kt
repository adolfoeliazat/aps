package aps.back

import aps.*
import org.springframework.data.repository.CrudRepository
import java.sql.Timestamp
import javax.persistence.*
import javax.persistence.AccessType.PROPERTY

@MappedSuperclass @Access(PROPERTY)
abstract class ClitoralEntity {
    @get:Id @get:GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null

    var createdAt = Timestamp(System.currentTimeMillis())
    var updatedAt = createdAt
    var deleted = false
}

@Entity @Table(name = "ua_orders")
open class UAOrder (
    var title: String,
    var documentType: UADocumentType,
    var numPages: Int,
    var numSources: Int,
    var details: String,
    var state: UAOrderState
) : ClitoralEntity() {
    override fun toString() =
        "UAOrder(id='$id', title='$title')"
}

interface UAOrderRepository : CrudRepository<UAOrder, Long> {

}







