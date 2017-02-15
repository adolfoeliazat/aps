/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import into.kommon.*
import org.springframework.data.repository.findOrDie
import javax.persistence.EntityManagerFactory

@Servant class ServeUACustomerGetOrderFiles(val emf: EntityManagerFactory,
                                            val orderRepo: UAOrderRepository,
                                            val fileRepo: UAOrderFileRepository) : BitchyProcedure() {
    override fun serve() {
        fuckCustomer(FuckCustomerParams(
            bpc = bpc,
            makeRequest = {ItemsRequest(CustomerFileFilter.values())},
            runShit = fun(ctx, req): ItemsResponse<UAOrderFileRTO> {
                // TODO:vgrechka @security Check permissions
                // TODO:vgrechka Friendly (like "fuck you" or something) message if no such order (user may mistype URL, etc.)

                val tsqueryLanguage = "russian"

                val order = orderRepo.findOrDie(req.entityID.value)

                val searchString = req.searchString.value
                val searchWords = searchString
                    .split(Regex("\\s+"))
                    .filter {it.contains(Regex("[a-zA-Zа-яА-Я0-9]"))}
                    .map {it.replace(Regex("[^a-zA-Zа-яА-Я0-9]"), "")}
                val tsquery = when {
                    searchString.contains("&") || searchString.contains("|") || searchString.contains("!") -> searchString
                    searchWords.isNotEmpty() -> searchWords.joinToString(" & ")
                    else -> ""
                }

                    val fromID = req.fromID.value?.toLong()
                val ordering = req.ordering.value

                val chunk = run {
                    val theFromID = when {
                        fromID != null -> fromID
                        else -> when (ordering) {
                            Ordering.ASC -> 0L
                            Ordering.DESC -> Long.MAX_VALUE
                        }
                    }

                    val em = emf.createEntityManager()
                    em.transaction.begin()
                    try {
                        val params = mutableListOf<Pair<String, Any>>()
                        val query = em.createNativeQuery(stringBuild{s->
                            s += "select * from ua_order_files f where true"

                            if (tsquery.isNotBlank()) {
                                s += " and (tsv @@ to_tsquery('$tsqueryLanguage', :tsquery)"
                                params += Pair("tsquery", tsquery)
                                var idIndex = 1
                                for (word in searchWords) {
                                    try {
                                        val long = word.toLong()
                                        val paramName = "id${idIndex++}"
                                        s += " or id = :$paramName"
                                        params += Pair(paramName, long)
                                    } catch (e: NumberFormatException) {}
                                }
                                s += ")"
                            }

                            val idop = when (ordering) {
                                Ordering.ASC -> ">="
                                Ordering.DESC -> "<="
                            }
                            s += " and id $idop $theFromID"
                            s += " order by id ${ordering.name}"
                        }, UAOrderFile::class.java)

                        query.maxResults = const.moreableChunkSize + 1
                        params.forEach {query.setParameter(it.first, it.second)}

                        var items: List<UAOrderFile> = cast(query.resultList)
                        var moreFromId: Long? = null
                        if (items.size > const.moreableChunkSize) {
                            moreFromId = items.last().id
                            items = items.dropLast(1)
                        }

                        val rtos = items.map {
                            it.toRTO(searchWords)
                        }
                        return@run Chunk(rtos, moreFromId = moreFromId)
                    } finally {
                        em.transaction.rollback()
                        em.close()
                    }
                }

                return ItemsResponse(chunk.items, chunk.moreFromId)
            }
        ))
    }
}


//@RemoteProcedureFactory fun serveCustomerGetUAOrderFiles() = customerProcedure(
//    {ItemsRequest(CustomerFileFilter.values())},
//    runShit = fun(ctx, req): ItemsResponse<UAOrderFileRTO> {
//        val tsqueryLanguage = "russian"
//
//        val searchString = req.searchString.value
//        val searchWords = searchString
//            .split(Regex("\\s+"))
//            .filter {it.contains(Regex("[a-zA-Zа-яА-Я0-9]"))}
//            .map {it.replace(Regex("[^a-zA-Zа-яА-Я0-9]"), "")}
//        val tsquery = when {
//            searchString.contains("&") || searchString.contains("|") || searchString.contains("!") -> searchString
//            searchWords.isNotEmpty() -> searchWords.joinToString(" & ")
//            else -> ""
//        }
//
//        val areaID = Tables.UA_ORDER_AREAS.let {t->
//            tracingSQL("Select area") {ctx.q
//                .selectFrom(t)
//                .where(t.UA_ORDER_ID.eq(req.entityID.value!!.toLong()))
//                .and(t.NAME.eq(userKindToAreaName(ctx.user_killme.kind)))
//                .fetchOne().id
//            }
//        }
//
//        val fromID = req.fromID.value?.toLong()
//        val ordering = req.ordering.value
//
//        val chunk = run {
//            val chunkSize = 10
//            val theFromID = fromID?.let {it} ?: if (ordering == Ordering.ASC) 0L else Long.MAX_VALUE
//            var records: List<JQUaOrderFilesRecord> = tracingSQL("Select chunk") {
//                var step = ctx.q
//                    .selectFrom(UA_ORDER_FILES.join(FILES).on(UA_ORDER_FILES.FILE_ID.eq(FILES.ID)))
//                    .where(UA_ORDER_FILES.UA_ORDER_AREA_ID.eq(areaID))
//
//                if (tsquery.isNotBlank()) {
//                    var cond = DSL.condition("tsv @@ to_tsquery('$tsqueryLanguage', ?)", tsquery)
//                    for (word in searchWords) {
//                        try {
//                            val long = word.toLong()
//                            cond = cond.or(UA_ORDER_FILES.ID.eq(long))
//                        } catch (e: NumberFormatException) {}
//                    }
//
//                    step = step.and(cond)
//                }
//
//                val filter = req.filter.value
//                exhaustive/when (filter) {
//                    CustomerFileFilter.ALL -> Unit
//                    // TODO:vgrechka Generalize below for writer and support
//                    CustomerFileFilter.FROM_ME -> {
//                        step = step.and(UA_ORDER_FILES.SEEN_AS_FROM.eq(JQUserKind.CUSTOMER))
//                    }
//                    CustomerFileFilter.FROM_WRITER -> {
//                        step = step.and(UA_ORDER_FILES.SEEN_AS_FROM.eq(JQUserKind.WRITER))
//                    }
//                    CustomerFileFilter.FROM_SUPPORT -> {
//                        step = step.and(UA_ORDER_FILES.SEEN_AS_FROM.eq(JQUserKind.ADMIN))
//                    }
//                }
//
//                step
//                    .and(if (ordering == Ordering.ASC) UA_ORDER_FILES.ID.greaterOrEqual(theFromID)
//                         else UA_ORDER_FILES.ID.lessOrEqual(theFromID))
//                    .orderBy(UA_ORDER_FILES.ID.sort(
//                        when (ordering) {
//                            Ordering.ASC -> SortOrder.ASC
//                            Ordering.DESC -> SortOrder.DESC
//                        }))
//                    .limit(chunkSize + 1)
//                    .fetch().into(UA_ORDER_FILES)
//            }
//
//            var moreFromId: String? = null
//            if (records.size == chunkSize + 1) {
//                moreFromId = "" + records.last()[UA_ORDER_FILES.ID]
//                records = records.subList(0, chunkSize)
//            }
//
//            val items = records.map {it.toRTO(ctx, searchWords)}
//
//            return@run Chunk(items, moreFromId)
//        }
//
//        return ItemsResponse(chunk.items, chunk.moreFromId)
//    }
//)




















