/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.tables.pojos.JQUsers
import org.jooq.*
import kotlin.reflect.KClass

@RemoteProcedureFactory fun customerGetOrders() = customerProcedure(
    ItemsRequest(UserFilter.values()),
    runShit = {ctx, req ->
        val chunk = selectChunk(
            ctx.q, table = "users", pojoClass = JQUsers::class, loadItem = JQUsers::toRTO,
            fromID = req.fromID.value?.let {it.toLong()},
            ordering = req.ordering.value,
            appendToWhere = {qb ->
                run { // Search string
                    val ss = req.searchString.value
                    when {
                        ss.isBlank() -> Unit

                        Regex("^p ").matches(ss) -> {
                            val coph = compactPhone(ss)
                            dlog("Doing phone search for [$ss] compacted to [$coph]")
                            qb.text("and compact_phone = ").arg(coph)
                        }

                        Regex("^\\d+$").matches(ss) -> {
                            dlog("Doing ID search for [$ss]")
                            qb.text("and id = ").arg(ss)
                        }

                        else -> {
                            dlog("Ding full-text search for [$ss]")
                            val ftsQueryString =
                                if (Regex("&|!|<->|\\|").matches(ss)) ss
                                else ss.replace(Regex("\\(|\\)"), " ").trim().split(Regex("\\s+")).joinToString(" & ")

                            qb.text("and tsv @@ to_tsquery('russian', ").arg(ftsQueryString).text(")")
                        }
                    }
                }

                run { // Filter
                    val filter = req.filter.value
                    when (filter) {
                        UserFilter.ALL -> Unit
                        else -> qb.text("and state = '$filter'")
                    }
                }
            }
        )

        ItemsResponse(chunk.items, chunk.moreFromId)
    }
)
























