package aps.back

import aps.*
import into.kommon.*

fun <T : HistoryItemRTOFields> serveReginaGetPairOfLastHistoryItems(req: ReginaGetPairOfLastHistoryItems<T>): ReginaGetPairOfLastHistoryItems.Response<*> {
    when (req.type) {
        UserParamsHistoryItemRTO::class -> {
            val items = userParamsHistoryItemRepo.findTop2ByHistory_EntityIDOrderByIdDesc(req.entityID)
            check(items.size == 1 || items.size == 2){"a769314d-a2e6-4e04-9e20-36eaa311fce6"}
            return ReginaGetPairOfLastHistoryItems.Response(
                lastItem = items[0].toRTO(listOf()),
                prelastItem = (items.size == 2).then {items[1].toRTO(listOf())})
        }
        else -> wtf("req.type = ${req.type}    69972903-9083-42ed-b623-be25265ca22e")
    }
}



