/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.*
import aps.back.generated.jooq.enums.*
import aps.back.generated.jooq.tables.pojos.*
import org.jooq.*
import kotlin.reflect.KClass

@RemoteProcedureFactory fun customerGetUAOrderFiles() = customerProcedure(
    ItemsRequest(CustomerFileFilter.values()),
    runShit = fun(ctx, req): ItemsResponse<UAOrderFileRTO> {
        val chunk = selectChunk(
            ctx.q, table = Tables.UA_ORDER_FILES.name,
            pojoClass = JQUaOrderFiles::class, loadItem = JQUaOrderFiles::toRTO,
            fromID = req.fromID.value?.let {it.toLong()},
            ordering = req.ordering.value,
            appendToWhere = {qb ->
                run { // Search string
                    val ss = req.searchString.value
                }

                run { // Filter
                    val filter = req.filter.value
                    exhaustive/when (filter) {
                        CustomerFileFilter.ALL -> Unit
                        CustomerFileFilter.FROM_ME -> {
                            qb.text("and ${Tables.UA_ORDER_FILES.SEEN_AS_FROM.name} = '${JQUserKind.CUSTOMER.literal}'::${Tables.UA_ORDER_FILES.SEEN_AS_FROM.dataType.typeName}")
                        }
                        CustomerFileFilter.FROM_WRITER -> {
                            qb.text("and ${Tables.UA_ORDER_FILES.SEEN_AS_FROM.name} = '${JQUserKind.WRITER.literal}'::${Tables.UA_ORDER_FILES.SEEN_AS_FROM.dataType.typeName}")
                        }
                        CustomerFileFilter.FROM_SUPPORT -> {
                            qb.text("and ${Tables.UA_ORDER_FILES.SEEN_AS_FROM.name} = '${JQUserKind.ADMIN.literal}'::${Tables.UA_ORDER_FILES.SEEN_AS_FROM.dataType.typeName}")
                        }
                    }
                }
            }
        )

        return ItemsResponse(chunk.items, chunk.moreFromId)
    }
)
























