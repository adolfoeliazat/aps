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

@RemoteProcedureFactory fun customerGetUAOrderFiles() = customerProcedure(
    ItemsRequest(CustomerFileFilter.values()),
    runShit = fun(ctx, req): ItemsResponse<UAOrderFileRTO> {
        val ss = req.searchString.value
        val searchWords = ss
            .split(Regex("\\s+"))
            .filter {it.contains(Regex("[a-zA-Zа-яА-Я0-9]"))}
            .map {it.replace(Regex("[^a-zA-Zа-яА-Я0-9]"), "")}

        val chunk = selectChunk(
            ctx.q,
            table = Tables.UA_ORDER_FILES.name,
            pojoClass = JQUaOrderFiles::class,
            loadItem = {file, q ->
                file.toRTO(q)-{o->
                    if (searchWords.isNotEmpty()) {
                        o.file.detailsHighlightRanges = luceneHighlightRanges(o.file.details, searchWords, russianAnalyzer)
                    }
                }
            },
            fromID = req.fromID.value?.let {it.toLong()},
            ordering = req.ordering.value,
            appendToFrom = {qb->
                qb.text(", ${Tables.FILES.name}")
            },
            appendToWhere = {qb->
                qb.text("and ${Tables.FILES.name}.${Tables.FILES.ID.name} = ${Tables.UA_ORDER_FILES.name}.${Tables.UA_ORDER_FILES.FILE_ID.name}")

                run { // Search string
                    if (searchWords.isNotEmpty()) {
                        val query = searchWords.joinToString(" & ")
                        qb.text("and tsv @@ ").arg(query).text("::tsquery")
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
























