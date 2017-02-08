package aps.back

import java.lang.reflect.Modifier

fun main(args: Array<String>) {
    Shit("little").sayHi()

    val clazz = Shit::class.java
    val dynamicShit = clazz.newInstance()
    val method = clazz.getMethod("sayHi")
    println("Method is " + Modifier.toString(method.modifiers))
    method.invoke(dynamicShit)
}

annotation class Fucking

@Fucking class Shit(val kind: String) {
    fun sayHi() {
        println("Hi, I am a $kind shit. Yeah...")
    }
}



