package aps.back

import org.springframework.data.repository.CrudRepository
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class UAOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null
    var title: String? = null
}

//@Entity
//data class UAOrder2 (
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    var id: Long? = null,
//    var title: String? = null
//) {
//}

interface UAOrderRepository : CrudRepository<UAOrder, Long> {

}

