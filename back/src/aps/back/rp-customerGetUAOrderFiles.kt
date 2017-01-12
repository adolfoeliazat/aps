/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.*
import aps.back.generated.jooq.enums.*
import aps.back.generated.jooq.tables.*
import aps.back.generated.jooq.tables.JQFiles.*
import aps.back.generated.jooq.tables.JQUaOrderFiles.*
import aps.back.generated.jooq.tables.records.*
import org.apache.lucene.analysis.ru.RussianAnalyzer
import org.jooq.*
import org.jooq.impl.DSL
import kotlin.reflect.KClass

@RemoteProcedureFactory fun serveCustomerGetUAOrderFiles() = customerProcedure(
    ItemsRequest(CustomerFileFilter.values()),
    runShit = fun(ctx, req): ItemsResponse<UAOrderFileRTO> {
        val ss = req.searchString.value
        val searchWords = ss
            .split(Regex("\\s+"))
            .filter {it.contains(Regex("[a-zA-Zа-яА-Я0-9]"))}
            .map {it.replace(Regex("[^a-zA-Zа-яА-Я0-9]"), "")}

        val areaID = Tables.UA_ORDER_AREAS.let {t->
            tracingSQL("Select area") {ctx.q
                .selectFrom(t)
                .where(t.UA_ORDER_ID.eq(req.entityID.value!!.toLong()))
                .and(t.NAME.eq(userKindToAreaName(ctx.user.kind)))
                .fetchOne().id
            }
        }

        val fromID = req.fromID.value?.toLong()
        val ordering = req.ordering.value

        val chunk = run {
            val chunkSize = 10
            val theFromID = fromID?.let {it} ?: if (ordering == Ordering.ASC) 0L else Long.MAX_VALUE
            var records: List<JQUaOrderFilesRecord> = tracingSQL("Select chunk") {
                var step = ctx.q
                    .selectFrom(UA_ORDER_FILES.join(FILES).on(UA_ORDER_FILES.FILE_ID.eq(FILES.ID)))
                    .where(UA_ORDER_FILES.UA_ORDER_AREA_ID.eq(areaID))

                if (searchWords.isNotEmpty()) {
                    val query = searchWords.joinToString(" & ")
                    var cond = DSL.condition("tsv @@ to_tsquery('russian', ?)", query)
                    for (word in searchWords) {
                        try {
                            val long = word.toLong()
                            cond = cond.or(UA_ORDER_FILES.ID.eq(long))
                        } catch (e: NumberFormatException) {}
                    }

                    step = step.and(cond)
                }

                val filter = req.filter.value
                exhaustive/when (filter) {
                    CustomerFileFilter.ALL -> Unit
                    // TODO:vgrechka Generalize below for writer and support
                    CustomerFileFilter.FROM_ME -> {
                        step = step.and(UA_ORDER_FILES.SEEN_AS_FROM.eq(JQUserKind.CUSTOMER))
                    }
                    CustomerFileFilter.FROM_WRITER -> {
                        step = step.and(UA_ORDER_FILES.SEEN_AS_FROM.eq(JQUserKind.WRITER))
                    }
                    CustomerFileFilter.FROM_SUPPORT -> {
                        step = step.and(UA_ORDER_FILES.SEEN_AS_FROM.eq(JQUserKind.ADMIN))
                    }
                }

                step
                    .and(if (ordering == Ordering.ASC) UA_ORDER_FILES.ID.greaterOrEqual(theFromID)
                         else UA_ORDER_FILES.ID.lessOrEqual(theFromID))
                    .orderBy(UA_ORDER_FILES.ID.sort(
                        when (ordering) {
                            Ordering.ASC -> SortOrder.ASC
                            Ordering.DESC -> SortOrder.DESC
                        }))
                    .limit(chunkSize + 1)
                    .fetch().into(UA_ORDER_FILES)
            }

            var moreFromId: String? = null
            if (records.size == chunkSize + 1) {
                moreFromId = "" + records.last()[UA_ORDER_FILES.ID]
                records = records.subList(0, chunkSize)
            }

            val items = records.map {it.toRTO(ctx.q, searchWords)}

            return@run Chunk(items, moreFromId)
        }

        return ItemsResponse(chunk.items, chunk.moreFromId)
    }
)


















//        val chunk = selectChunk(
//            ctx.q,
//            table = Tables.UA_ORDER_FILES.name,
//            pojoClass = JQUaOrderFiles::class, // TODO:vgrechka Use record instead of POJO
//            loadItem = {orderFile, q ->
//                orderFile.toRTO(q, searchWords)
//            },
//            fromID = req.fromID.value?.let {it.toLong()},
//            ordering = req.ordering.value,
//            appendToFrom = {qb->
//                qb.text(", ${Tables.FILES.name}")
//            },
//            appendToWhere = {qb->
//                qb.text("and ${Tables.FILES.name}.${Tables.FILES.ID.name} = ${Tables.UA_ORDER_FILES.name}.${Tables.UA_ORDER_FILES.FILE_ID.name}")
//                qb.text("and ${Tables.UA_ORDER_FILES.name}.${Tables.UA_ORDER_FILES.UA_ORDER_AREA_ID.name} = ").arg(areaID)
//
//                run { // Search string
//                    if (searchWords.isNotEmpty()) {
//                        val query = searchWords.joinToString(" & ")
//                        qb.text("and (tsv @@ to_tsquery('russian', ").arg(query).text(")")
//                        for (word in searchWords) {
//                            try {
//                                val long = word.toLong()
//                                qb.text("or ${Tables.UA_ORDER_FILES.name}.${Tables.UA_ORDER_FILES.ID.name} = ").arg(long)
//                            } catch (e: NumberFormatException) {}
//                        }
//                        qb.text(")")
//                    }
//                }
//
//                run { // Filter
//                    val filter = req.filter.value
//                    exhaustive/when (filter) {
//                        CustomerFileFilter.ALL -> Unit
//                        CustomerFileFilter.FROM_ME -> {
//                            qb.text("and ${Tables.UA_ORDER_FILES.SEEN_AS_FROM.name} = '${JQUserKind.CUSTOMER.literal}'::${Tables.UA_ORDER_FILES.SEEN_AS_FROM.dataType.typeName}")
//                        }
//                        CustomerFileFilter.FROM_WRITER -> {
//                            qb.text("and ${Tables.UA_ORDER_FILES.SEEN_AS_FROM.name} = '${JQUserKind.WRITER.literal}'::${Tables.UA_ORDER_FILES.SEEN_AS_FROM.dataType.typeName}")
//                        }
//                        CustomerFileFilter.FROM_SUPPORT -> {
//                            qb.text("and ${Tables.UA_ORDER_FILES.SEEN_AS_FROM.name} = '${JQUserKind.ADMIN.literal}'::${Tables.UA_ORDER_FILES.SEEN_AS_FROM.dataType.typeName}")
//                        }
//                    }
//                }
//            }
//        )







