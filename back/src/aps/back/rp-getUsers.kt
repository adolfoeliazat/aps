/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.tables.pojos.JQUsers
import into.kommon.*
import org.jooq.*
import kotlin.reflect.KClass

@RemoteProcedureFactory fun getUsers() = adminProcedure(
    {ItemsRequest(UserFilter.values())},
    runShit = fun(ctx, req): ItemsResponse<UserRTO> {
        imf("Reimplement RP getUsers()")
//        val chunk = selectChunk(
//            ctx.q, table = "users", pojoClass = JQUsers::class, loadItem = JQUsers::toRTO,
//            fromID = req.fromID.value?.let {it.toLong()},
//            ordering = req.ordering.value,
//            appendToWhere = {qb ->
//                run { // Search string
//                    val ss = req.searchString.value
//                    exhaustive/when {
//                        ss.isBlank() -> Unit
//
//                        Regex("^p ").matches(ss) -> {
//                            val coph = compactPhone(ss)
//                            dlog("Doing phone search for [$ss] compacted to [$coph]")
//                            qb.text("and compact_phone = ").arg(coph)
//                        }
//
//                        Regex("^\\d+$").matches(ss) -> {
//                            dlog("Doing ID search for [$ss]")
//                            qb.text("and id = ").arg(ss)
//                        }
//
//                        else -> {
//                            dlog("Ding full-text search for [$ss]")
//                            val ftsQueryString =
//                                if (Regex("&|!|<->|\\|").matches(ss)) ss
//                                else ss.replace(Regex("\\(|\\)"), " ").trim().split(Regex("\\s+")).joinToString(" & ")
//
//                            qb.text("and tsv @@ to_tsquery('russian', ").arg(ftsQueryString).text(")")
//                        }
//                    }
//                }
//
//                run { // Filter
//                    val filter = req.filter.value
//                    exhaustive/when (filter) {
//                        UserFilter.ALL -> Unit
//                        else -> qb.text("and state = '$filter'")
//                    }
//                }
//            }
//        )

//        return ItemsResponse(chunk.items, chunk.moreFromId)
    }
)
























