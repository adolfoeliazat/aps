package aps.back

import aps.*
import into.kommon.*
import kotlin.reflect.KClass
import kotlin.reflect.full.cast

@Remote fun <T : HistoryItemRTOFields> reginaGetPairOfLastHistoryItems(type: KClass<T>, entityID: Long): PairOfLastHistoryItems<T> {
    when (type) {
        UserParamsHistoryItemRTO::class -> {
            val items = userParamsHistoryItemRepo.findTop2ByHistory_EntityIDOrderByIdDesc(entityID)
            check(items.size == 1 || items.size == 2){"a769314d-a2e6-4e04-9e20-36eaa311fce6"}
            return PairOfLastHistoryItems(
                lastItem = type.cast(items[0].toRTO(listOf())),
                prelastItem = type.cast((items.size == 2).then {items[1].toRTO(listOf())}))
        }
        else -> wtf("req.type = $type    69972903-9083-42ed-b623-be25265ca22e")
    }
}



