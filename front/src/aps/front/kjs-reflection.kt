@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*

fun setPropertyOrBackingField(obj: Any, prop: String, value: Any?) {
    try {
        obj.asDynamic()[prop] = value
    } catch(e: dynamic) {
        val keys = JSObject.keys(obj).filter {it.startsWith("${prop}_")}
        if (keys.isEmpty()) bitch("No backing field candidates for property `$prop`")
        if (keys.size > 1) bitch("Too many backing field candidates for property `$prop`")

        obj.asDynamic()[keys.first()] = value
    }
}

object KJSReflectionFuckingAround {
    interface IShit {
        val foos4: String
        val foob4: Boolean
    }

    class Shit(val foos1: String, val foob1: Boolean,
               var foos2: String, var foob2: Boolean,
               override val foos4: String, override val foob4: Boolean) : IShit {
        var foos3 = ""
        var foob3 = false
    }

    fun fuck1() {
        val shit: Shit = eval("new _.aps.front.KJSReflectionFuckingAround.Shit()")
        setPropertyOrBackingField(shit, "foos1", "Boobs")
        setPropertyOrBackingField(shit, "foos2", "Boobs")
        setPropertyOrBackingField(shit, "foos3", "Boobs")
        setPropertyOrBackingField(shit, "foos4", "Boobs")
        setPropertyOrBackingField(shit, "foob1", true)
        setPropertyOrBackingField(shit, "foob2", true)
        setPropertyOrBackingField(shit, "foob3", true)
        setPropertyOrBackingField(shit, "foob4", true)
        clog(shit)
        clog("foos1=${shit.foos1}; foob1=${shit.foob1}")
        clog("foos2=${shit.foos2}; foob2=${shit.foob2}")
        clog("foos3=${shit.foos3}; foob3=${shit.foob3}")
        clog("foos4=${shit.foos4}; foob4=${shit.foob4}")
    }
}




