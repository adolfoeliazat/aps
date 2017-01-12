package aps.back.spike

import aps.*
import aps.RedisLogMessage.Separator.Type.*
import aps.back.*
import into.kommon.*
import org.jetbrains.kotlin.incremental.makeModuleFile

fun main(args: Array<String>) {
//    test1()
//    test2()
//    test3()
    test4()
}

private fun test4() {
    class Cunt(val name: String)

    val om = shittyObjectMapper

//    val obj = mutableListOf<Any>()-{o->
//        o += mutableMapOf<String, Any?>()-{o->
//            o["foo"] = 10
//            o["bar"] = "cunt"
//        }
//        o += mutableMapOf<String, Any?>()-{o->
//            o["foo"] = 20
//            o["bar"] = "boobs"
//        }
//    }

    val obj = mutableListOf<Any>()-{o->
        o += RecordRTO(mutableListOf<FieldValueRTO>()-{o->
            o += FieldValueRTO("pussy", "hairy")
        })
    }

    val json = om.writeValueAsString(obj)
    clog("json", json)

//    val obj2 = om.readValue(json, VisualShitCapturedRequest::class.java)
//    clog("id", obj2.id)
//    for ((i, shot) in obj2.shots.withIndex()) {
//        clog("shot $i:")
//        clog("  dataURL", shot.dataURL)
//        clog("  windowScrollY", shot.windowScrollY)
//    }
}

private fun test3() {
    val om = shittyObjectMapper

    val obj = VisualShitCapturedRequest()-{o->
        o.id = "fucking id"
        o.shots = listOf(
            BrowserShot()-{o->
                o.dataURL = "fucking data 1"
                o.windowScrollY = 10.0
            },
            BrowserShot()-{o->
                o.dataURL = "fucking data 2"
                o.windowScrollY = 20.0
            }
        )
    }

    val json = om.writeValueAsString(obj)
    clog("json", json)

    val obj2 = om.readValue(json, VisualShitCapturedRequest::class.java)
    clog("id", obj2.id)
    for ((i, shot) in obj2.shots.withIndex()) {
        clog("shot $i:")
        clog("  dataURL", shot.dataURL)
        clog("  windowScrollY", shot.windowScrollY)
    }
}

private fun test2() {
    val om = shittyObjectMapper
//    val om = objectMapper

    val msgOut = RedisLogMessage.SQL()-{o->
        o.id = "123"
        o.beginMillis = Long.MAX_VALUE
        o.endMillis = 234L
        o.stamp = "sometime"
        o.stack = "somewhere"
        o.shortDescription = "poeben'"
        o.text = "some shit"
        o.stage = RedisLogMessage.SQL.Stage.SUCCESS
        o.exceptionStack = "pizda"
    }

    val json1 = om.writeValueAsString(msgOut)
//    val json1 = """{"@class":"aps.RedisLogMessage${'$'}SQL","${'$'}${'$'}${'$'}class":"aps.RedisLogMessage${'$'}SQL","id":"123","stamp":"sometime","stack":"somewhere","text":"some shit","shortDescription":"poeben'","stage":{"${'$'}${'$'}${'$'}enum":"aps.RedisLogMessage${'$'}SQL${'$'}Stage","value":"SUCCESS"},"exceptionStack":"pizda","beginMillis":9223372036854775807}"""
//    val json1 = """{"@class":"aps.RedisLogMessage${'$'}SQL","${'$'}${'$'}${'$'}class":"aps.RedisLogMessage${'$'}SQL","id":"123","stamp":"sometime","stack":"somewhere","text":"some shit","shortDescription":"poeben'","stage":{"${'$'}${'$'}${'$'}enum":"aps.RedisLogMessage${'$'}SQL${'$'}Stage","value":"SUCCESS"},"exceptionStack":"pizda","beginMillis":9223372036854775807}"""
//    val json1 = """{"@class":"aps.RedisLogMessage${'$'}SQL","id":"123","stamp":"sometime","stack":"somewhere","text":"some shit","shortDescription":"poeben'","stage":"SUCCESS","exceptionStack":"pizda","beginMillis":9223372036854775807}"""
    clog("json1", json1)

    val msgIn = om.readValue(json1, RedisLogMessage::class.java)
    clog("id", msgIn.id)
    clog("beginMillis", msgIn.beginMillis)
    clog("endMillis", msgIn.endMillis)
    clog("shortDescription", msgIn.shortDescription)
    clog("stack", msgIn.stack)
    clog("stamp", msgIn.stamp)
    clog("text", msgIn.text)
    msgIn as RedisLogMessage.SQL
    clog("stage.name", msgIn.stage.name)
}

private fun test1() {
    run {
        clog("----- RedisLogMessage -----")
        val msgOut: RedisLogMessage = RedisLogMessage.Separator() - {o ->
            o.stamp = "sometime"
            o.stack = "somewhere"
            o.type = THICK_SEPARATOR
            o.text = "fuck you"
        }
        val json_RedisLogMessage = objectMapper.writeValueAsString(msgOut)
        clog("json_RedisLogMessage", json_RedisLogMessage)
        val hackyJson_RedisLogMessage = hackyObjectMapper.writeValueAsString(msgOut)
        clog("hackyJson_RedisLogMessage", hackyJson_RedisLogMessage)

        val msgIn = objectMapper.readValue(json_RedisLogMessage, RedisLogMessage::class.java)
        exhaustive / when (msgIn) {
            is RedisLogMessage.Separator -> {
                clog("Yeah, separator: ${msgIn.type} ${msgIn.text}")
            }
            else -> wtf()
        }
    }

    run {
        clog("----- Foo -----")
        class Foo {
            lateinit var fuck: String
            lateinit var shit: String
        }

        val foo = Foo() - {o ->
            o.fuck = "little"
            o.shit = "big"
        }
        val json_Foo = objectMapper.writeValueAsString(foo)
        clog("json_Foo", json_Foo)
        val hackyJson_Foo = hackyObjectMapper.writeValueAsString(foo)
        clog("hackyJson_Foo", hackyJson_Foo)
    }

    clog("OK")
}



