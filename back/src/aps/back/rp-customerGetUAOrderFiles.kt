/*
 * APS
 *
 * (C) Copyright 2015-2017 Vladimir Grechka
 */

package aps.back

import aps.*
import org.postgresql.util.PSQLException
import org.springframework.data.repository.findOrDie
import javax.persistence.EntityManagerFactory

@Servant class ServeUAAdminGetOrders(val emf: EntityManagerFactory) : BitchyProcedure() {
    override fun serve() {
        fuckAdmin(FuckAdminParams(
            bpc = bpc,
            makeRequest = {ItemsRequest(AdminOrderFilter.values())},
            runShit = fun(ctx, req): ItemsResponse<UserRTO> {
                TestServerFiddling.nextRequestError.getAndReset()?.let(::bitchExpectedly)
                return ItemsResponse(listOf(), null)
            }
        ))
    }
}

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
                        val query = em.createNativeQuery(stringBuild {s ->
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
                                    } catch (e: NumberFormatException) {
                                    }
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
                    } catch (e: PSQLException) {
                        // TODO:vgrechka Return some code to client, so it knows what went wrong and can show query syntax help
                        bitchExpectedly(t("TOTE", "Отстойный поисковый запрос"))
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





















