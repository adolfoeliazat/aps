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
import org.apache.lucene.analysis.ru.RussianAnalyzer
import org.jooq.*
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
            ctx.q("Select area")
                .selectFrom(t)
                .where(t.UA_ORDER_ID.eq(req.entityID.value!!.toLong()))
                .and(t.NAME.eq(userKindToAreaName(ctx.user.kind)))
                .fetchOne().id
        }

        val chunk = selectChunk(
            ctx.q,
            table = Tables.UA_ORDER_FILES.name,
            pojoClass = JQUaOrderFiles::class, // TODO:vgrechka Use record instead of POJO
            loadItem = {orderFile, q ->
                orderFile.toRTO(q, searchWords)
            },
            fromID = req.fromID.value?.let {it.toLong()},
            ordering = req.ordering.value,
            appendToFrom = {qb->
                qb.text(", ${Tables.FILES.name}")
            },
            appendToWhere = {qb->
                qb.text("and ${Tables.FILES.name}.${Tables.FILES.ID.name} = ${Tables.UA_ORDER_FILES.name}.${Tables.UA_ORDER_FILES.FILE_ID.name}")
                qb.text("and ${Tables.UA_ORDER_FILES.name}.${Tables.UA_ORDER_FILES.UA_ORDER_AREA_ID.name} = ").arg(areaID)

                run { // Search string
                    if (searchWords.isNotEmpty()) {
                        val query = searchWords.joinToString(" & ")
                        qb.text("and (tsv @@ to_tsquery('russian', ").arg(query).text(")")
                        for (word in searchWords) {
                            try {
                                val long = word.toLong()
                                qb.text("or ${Tables.UA_ORDER_FILES.name}.${Tables.UA_ORDER_FILES.ID.name} = ").arg(long)
                            } catch (e: NumberFormatException) {}
                        }
                        qb.text(")")
                    }
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
























