package aps.back

import aps.*
import org.jooq.EnumType

//fun <T> tracingSQL(descr: String, block: () -> T): T {
//    if (!BackGlobus.tracingEnabled) return block()
//
//    val rlm = RedisLogMessage.SQL()-{o->
//        o.shortDescription = descr
//        o.stage = RedisLogMessage.SQL.Stage.PENDING
//        o.text = "Not known yet"
//    }
//    redisLog.send(rlm)
//
//    RequestGlobus.actualSQLFromJOOQ = null
//    RequestGlobus.resultFromJOOQ = null
//
//    try {
//        val res = block()
//        rlm.stage = RedisLogMessage.SQL.Stage.SUCCESS
//        return res
//    } catch (e: Throwable) {
//        rlm.stage = RedisLogMessage.SQL.Stage.FAILURE
//        rlm.exceptionStack = e.stackString()
//        throw e
//    } finally {
//        RequestGlobus.actualSQLFromJOOQ?.let {rlm.text = it}
//        RequestGlobus.resultFromJOOQ?.let {res->
//            rlm.result = mutableListOf<RecordRTO>()-{o->
//                for (rec in res) {
//                    o += RecordRTO(mutableListOf<FieldValueRTO>()-{o->
//                        for (field in rec.fields()) {
//                            val value = rec[field]
//                            val remoteValue = when (value) {
//                                null -> "<null>"
//                                is String, is Int, is Long, is Boolean -> value
//                                is EnumType -> value.literal
//                                else -> "<${value::class.java.name}>"
//                            }
//                            o += FieldValueRTO(field.name, remoteValue)
//                        }
//                    })
//                }
//            }
//        }
//        rlm.endMillis = currentTimeMillis()
//        redisLog.amend(rlm)
//    }
//}

