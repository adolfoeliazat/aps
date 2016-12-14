package aps.back

object Fiddle {
    @JvmStatic
    fun main(args: Array<String>) {
        val how = How.valueOf(System.getProperty("how"))
        eprintln("Fiddling: $how")
        how.act()
        eprintln("\nFUCK YEAH")
    }

    enum class How {
        RecreateDBOnBluemix {
            override fun act() {
                DB.bmix_fuckingAround_postgres.joo{q->
                    val res = q("Fiddling").fetch("select now()")
                    res.forEachIndexed {recordIndex, record ->
                        println("Record $recordIndex:")
                        for (fieldIndex in 0 until res.fieldsRow().size()) {
                            val fieldName = res.fieldsRow().field(fieldIndex).name
                            val fieldValue = record[fieldIndex]
                            println("    $fieldName: $fieldValue")
                        }
                        println()
                    }
                }
            }
        };

        abstract fun act()
    }
}

