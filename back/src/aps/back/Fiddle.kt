package aps.back

import org.jooq.Record
import org.jooq.Result

object Fiddle {
    @JvmStatic
    fun main(args: Array<String>) {
        val how = How.valueOf(System.getProperty("how"))
        eprintln("Fiddling: $how")
        _requestShit.set(RequestShit())
        how.act()
        eprintln("\nFUCK YEAH")
    }

    enum class How {
        RecreateDB_bmix_fuckingAround_apsdevua {
            override fun act() {
                DB.bmix_fuckingAround_apsdevua.recreate()
            }
        },
        RecreateSchema_bmix_fuckingAround_apsdevua {
            override fun act() {
                DB.bmix_fuckingAround_apsdevua.recreateSchema()
            }
        };

        abstract fun act()
    }

    fun selectNow(db: DB.Database) {
        db.joo{q->
            val res = q("Fiddling").fetch("select now()")
            printFetchResult(res)
        }
    }

    fun printFetchResult(res: Result<Record>) {
        res.forEachIndexed {recordIndex, record ->
            eprintln("Record $recordIndex:")
            for (fieldIndex in 0 until res.fieldsRow().size()) {
                val fieldName = res.fieldsRow().field(fieldIndex).name
                val fieldValue = record[fieldIndex]
                eprintln("    $fieldName: $fieldValue")
            }
            eprintln()
        }
    }
}

