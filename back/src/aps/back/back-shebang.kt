/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.tables.pojos.JQUsers
import aps.back.generated.jooq.Tables.*
import org.jooq.*
import org.jooq.impl.DSL
import java.io.File
import java.util.*
import kotlin.properties.Delegates
import kotlin.properties.Delegates.notNull
import kotlin.reflect.KClass

class QueryBuilder(val shortDescription: String) {
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
        return tracingSQL(shortDescription) {q.fetch("" + sql, *bindings.toTypedArray())}
    }
}

class Chunk<T>(val items: List<T>, val moreFromId: String?)


fun <POJO : Any, RTO> selectChunk(
    q: DSLContext,
    table: String,
    pojoClass: KClass<POJO>,
    appendToSelect: (QueryBuilder) -> Unit = {},
    appendToFrom: (QueryBuilder) -> Unit = {},
    appendToWhere: (QueryBuilder) -> Unit = {},
    loadItem: (POJO, DSLContext) -> RTO,
    ordering: Ordering,
    fromID: Long?
) : Chunk<RTO> {
    val chunkSize = 10

    val theFromID = fromID?.let {it} ?: if (ordering == Ordering.ASC) 0L else Long.MAX_VALUE
    var records = QueryBuilder("Select chunk")
        .text("select *")
        .run(appendToSelect)
        .text("from $table")
        .run(appendToFrom)
        .text("where true")
        .run(appendToWhere)
        .text("and $table.id ${if (ordering == Ordering.ASC) ">=" else "<="}").arg(theFromID)
        .text("order by $table.id $ordering")
        .text("fetch first ${chunkSize + 1} rows only")
        .fetch(q)

    var moreFromId: String? = null
    if (records.size == chunkSize + 1) {
        moreFromId = "" + records.last()[DSL.field(DSL.name(table, "id"))]
        records = records.subList(0, chunkSize)
    }

    val items = records.map{it.into(pojoClass.java)}.map{loadItem(it, q)}

    return Chunk(items, moreFromId)
}

fun loadUser(ctx: ProcedureContext): UserRTO {
    val users = tracingSQL("Select user") {ctx.q
        .select().from(USERS)
        .where(USERS.ID.eq(ctx.user.id.toLong()))
        .fetch().into(JQUsers::class.java)
    }

    return users.first().toRTO(ctx.q)
}

object BackGlobus {
    var tracingEnabled = true
    lateinit var startMoment: Date
    val slimJarName = "apsback-slim.jar"
    val killResponse = "Aarrgghh..."

//    val version by lazy {
//        this::class.java.classLoader.getResource("aps/version.txt").readText()
//    }

    val version: String get() = this::class.java.classLoader.getResource("aps/version.txt").readText()
    @Volatile var lastDownloadedPieceOfShit: PieceOfShitDownload? = null
}



















