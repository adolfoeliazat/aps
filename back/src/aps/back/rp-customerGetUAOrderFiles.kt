/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.tables.pojos.*
import org.jooq.*
import kotlin.reflect.KClass

@RemoteProcedureFactory fun customerGetUAOrderFiles() = customerProcedure(
    ItemsRequest(FileFilter.values()),
    runShit = fun(ctx, req): ItemsResponse<UAOrderFileRTO> {
        val chunk = selectChunk(
            ctx.q, table = "ua_order_files",
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
                        FileFilter.ALL -> Unit
                    }
                }
            }
        )

        return ItemsResponse(chunk.items, chunk.moreFromId)
    }
)
























