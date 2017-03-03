package aps.back

import aps.*
import into.kommon.*
import kotlin.reflect.full.cast

fun <T : HistoryItemRTOFields> ReginaGetPairOfLastHistoryItems<T>.serve(): ReginaGetPairOfLastHistoryItems<T>.Response {
    when (this.type) {
        UserParamsHistoryItemRTO::class -> {
            val items = userParamsHistoryItemRepo.findTop2ByHistory_EntityIDOrderByIdDesc(this.entityID)
            check(items.size == 1 || items.size == 2){"a769314d-a2e6-4e04-9e20-36eaa311fce6"}
            return this.Response(
                lastItem = this.type.cast(items[0].toRTO(listOf())),
                prelastItem = this.type.cast((items.size == 2).then {items[1].toRTO(listOf())}))
        }
        else -> wtf("req.type = ${this.type}    69972903-9083-42ed-b623-be25265ca22e")
    }
}



