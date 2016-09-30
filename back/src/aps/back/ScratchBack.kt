package aps.back

import aps.UserKind
import org.jooq.SQLDialect
import org.jooq.impl.DSL

fun main(args: Array<String>) {
//    ScratchBack.fiddleWithDB_apsTestOnTestServer()

    run {
        for (c in "foobar".toCharArray()) {
            println(c)
        }
    }
}

object ScratchBack {

    fun fiddleWithDB_apsTestOnTestServer() {
//        val query = "select * from users"
        val query = "select * from user_tokens"

        DB.apsTestOnTestServer.joo{q->
            val res = q.fetch(query)
            res.forEachIndexed {recordIndex, record ->
                println("Record $recordIndex:")
                for (fieldIndex in 0 until res.fieldsRow().size()) {
                    val fieldName = res.fieldsRow().field(fieldIndex).name
                    val fieldValue = record[fieldIndex]
                    println("    $fieldName: $fieldValue")
                }
                println()
            }
            println("Rows fetched: ${res.size}")
        }
    }

}















