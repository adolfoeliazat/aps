package scratch

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.memberProperties

fun main(args: Array<String>) {
    testReflection1()
}


class Foo {
    var bars = mutableListOf<Bar>()
}

class Bar {

}

fun testReflection1() {
    val p = Foo::class.memberProperties.first()
    val gt: Type = Foo::class.java.getMethod("get${p.name.capitalize()}").genericReturnType
    val pt = gt as ParameterizedType
    val c = pt.actualTypeArguments[0] as Class<*>
    println(c.name)
}
