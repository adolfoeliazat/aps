package aps.back

import aps.UserKind
import aps.back.generated.jooq.tables.JQUaOrders.*
import aps.back.generated.jooq.tables.pojos.*
import org.jooq.Record
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

fun main(args: Array<String>) {
    System.setProperty("user.timezone", "GMT")
    ScratchBack.fiddleWithDB_apsTestOnTestServer()
//    println(Boolean::class == Boolean::class)
}

object ScratchBack {

    fun fiddleWithDB_apsTestOnTestServer() {
        shit2()
    }

    private fun shit2() {
//        DB.apsTestOnTestServer.joo {q->
//            val rec: Record = tracingSQL("Select UA order") {q
//                .select().from(UA_ORDERS)
//                // .where(UA_ORDERS.ID.eq(req.id.value.toLong()))
//                .fetchOne()
//                ?: bitchExpectedly(t("TOTE", "Нет такого заказа (по крайней мере, для тебя)"))
//            }
//
//            val order = rec.into(JQUaOrders::class.java)
//            println(order.insertedAt)
//            val unix = order.insertedAt.time
//
//            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z")
//            sdf.timeZone = TimeZone.getTimeZone("UTC")
//            val formattedDate = sdf.format(Timestamp(unix))
//            println(formattedDate)
//        }
    }

    private fun shit1() {
//        val query = "select inserted_at from ua_orders"
//        //        val query = "select * from users"
//        //        val query = "select * from user_tokens"
//
//        DB.apsTestOnTestServer.joo {q->
//            val res = tracingSQL("Fucking around") {q.fetch(query)}
//            res.forEachIndexed {recordIndex, record ->
//                println("Record $recordIndex:")
//                for (fieldIndex in 0 until res.fieldsRow().size()) {
//                    val fieldName = res.fieldsRow().field(fieldIndex).name
//                    val fieldValue = record[fieldIndex]
//                    println("    $fieldName: $fieldValue")
//                }
//                println()
//            }
//            println("Rows fetched: ${res.size}")
//        }
    }

}















