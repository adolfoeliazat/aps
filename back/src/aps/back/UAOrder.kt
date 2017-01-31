package aps.back

import org.springframework.data.repository.CrudRepository
import javax.persistence.*

//@Entity
//@Table(name = "ua_orders")
//class UAOrder {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    var id: Long? = null
//    var title: String? = null
//}

@Entity
@Table(name = "ua_orders")
class UAOrder (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,
    var title: String
) {
    constructor() : this(null, "")
}


interface UAOrderRepository : CrudRepository<UAOrder, Long> {

}

