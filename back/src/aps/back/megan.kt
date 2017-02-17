package aps.back

import aps.*
import org.postgresql.util.PSQLException
import javax.persistence.EntityManagerFactory

interface MeganItem<out ItemRTO> {
    val idBang: Long
    fun toRTO(searchWords: List<String>): ItemRTO
}

fun <Item, ItemRTO, Filter> megan(
    req: ItemsRequest<Filter>,
    checkShit: () -> Unit,
    table: String,
    itemClass: Class<Item>,
    parentKey: String? = null
): ItemsResponse<ItemRTO>
where
    Filter: Enum<Filter>,
    Filter: Titled,
    Item: MeganItem<ItemRTO>
{
    TestServerFiddling.nextRequestError.getAndReset()?.let(::bitchExpectedly)
    checkShit()

    val emf = springctx.getBean(EntityManagerFactory::class.java)

    val tsqueryLanguage = "russian"

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

    val fromID = req.fromID.value
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
                s += "select * from $table f where true"
                if (parentKey != null) {
                    s += " and $parentKey = :parentID"
                    params += Pair("parentID", req.parentEntityID.value!!)
                }

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
            }, itemClass)

            query.maxResults = const.moreableChunkSize + 1
            params.forEach {query.setParameter(it.first, it.second)}

            var items: List<Item> = cast(query.resultList)
            var moreFromId: Long? = null
            if (items.size > const.moreableChunkSize) {
                moreFromId = items.last().idBang
                items = items.dropLast(1)
            }

            val rtos = items.map {
                it.toRTO(searchWords)
            }
            return@run Chunk(rtos, moreFromId = moreFromId)
        } catch (e: PSQLException) {
            // TODO:vgrechka Check that exception was actually caused by incorrect tsquery syntax. Otherwise rethrow
            // TODO:vgrechka Return some code to client, so it knows what went wrong and can show query syntax help to user
            bitchExpectedly(t("TOTE", "Отстойный поисковый запрос"))
        } finally {
            em.transaction.rollback()
            em.close()
        }
    }

    return ItemsResponse(chunk.items, chunk.moreFromId)
}


