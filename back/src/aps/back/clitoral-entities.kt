package aps.back

import org.springframework.data.repository.CrudRepository
import javax.persistence.*
import javax.persistence.AccessType.PROPERTY
import kotlin.properties.Delegates.notNull

@MappedSuperclass @Access(PROPERTY)
abstract class ClitoralEntity {
    @get:Id @get:GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null
}

@Entity @Table(name = "ua_orders")
class UAOrder (
    var title: String) : ClitoralEntity()
{
//    constructor() : this("boobs")

    var pizda by notNull<String>()

    override fun toString() =
        "UAOrder(id='$id', title='$title', pizda='$pizda')"
}


//    deleted boolean not null,
//    inserted_at timestamp not null,
//    updated_at timestamp not null,
//    tsv tsvector not null,
//    creator_id bigint not null references users(id), -- Can be admin
//customer_id bigint not null references users(id),
//title text not null,
//document_type ua_document_type not null,
//deadline timestamp not null,
//price int /*maybe null*/,
//num_pages int not null,
//num_sources int not null,
//details text not null,
//admin_notes text not null,
//state ua_order_state not null,
//writer_id bigint /*maybe null*/ references users(id)

interface UAOrderRepository : CrudRepository<UAOrder, Long> {

}

