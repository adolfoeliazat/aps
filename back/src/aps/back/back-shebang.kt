/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import org.jooq.*
import kotlin.reflect.KClass

class QueryBuilder {
    private val sql = StringBuilder()
    private val bindings = mutableListOf<Any?>()

    fun text(s: String): QueryBuilder {
        sql.append(" " + s)
        return this
    }

    fun arg(x: Any?): QueryBuilder {
        sql.append("?")
        bindings.add(x)
        return this
    }

    fun run(f: (QueryBuilder) -> Unit): QueryBuilder {
        f(this)
        return this
    }

    fun fetch(q: DSLContext): List<Record> {
        return q.fetch("" + sql, *bindings.toTypedArray())
    }
}

val MORE_CHUNK = 10 + 1

class Chunk<T>(val items: List<T>, val moreFromId: String?)


fun <POJO : Any, RTO> selectChunk(
    q: DSLContext,
    table: String,
    pojoClass: KClass<POJO>,
    appendToSelect: (QueryBuilder) -> Unit = {},
    appendToWhere: (QueryBuilder) -> Unit = {},
    loadItem: (POJO, DSLContext) -> RTO,
    ordering: Ordering,
    fromID: Long?) : Chunk<RTO> {

    val theFromID = fromID?.let {it} ?: if (ordering == Ordering.ASC) 0L else Long.MAX_VALUE

    var records = QueryBuilder()
        .text("select *")
        .run(appendToSelect)
        .text("from $table where true ")
        .run(appendToWhere)
        .text("and id ${if (ordering == Ordering.ASC) ">=" else "<="}").arg(theFromID)
        .text("order by id $ordering")
        .text("fetch first ${MORE_CHUNK} rows only")
        .fetch(q)

    var moreFromId: String? = null
    if (records.size == MORE_CHUNK) {
        moreFromId = "" + records.last()["id"]
        records = records.subList(0, MORE_CHUNK - 1)
    }

    val items = records.map{it.into(pojoClass.java)}.map{loadItem(it, q)}

    return Chunk(items, moreFromId)
}




