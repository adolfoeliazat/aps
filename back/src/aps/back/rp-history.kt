package aps.back

import aps.*
import into.kommon.*
import kotlin.reflect.KClass

fun <T : HistoryItemRTOFields> serveReginaGetPairOfLastHistoryItems(p: ReginaGetPairOfLastHistoryItems<T>): ReginaGetPairOfLastHistoryItems.Response<*> {
    when (p.type) {
        UserParamsHistoryItemRTO::class -> {
            val items = userParamsHistoryItemRepo.findTop2ByHistory_EntityIDOrderByIdDesc(p.entityID)
            check(items.size == 1 || items.size == 2){"a769314d-a2e6-4e04-9e20-36eaa311fce6"}
            val x = items[0].toRTO(listOf())
            return ReginaGetPairOfLastHistoryItems.Response(
                lastItem = items[0].toRTO(listOf()),
                prelastItem = (items.size == 2).then {items[1].toRTO(listOf())})
        }
        else -> wtf("req.type = ${p.type}    69972903-9083-42ed-b623-be25265ca22e")
    }
}



