/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import org.w3c.dom.events.*

val kdiv = ElementBuilder("div")
val kspan = ElementBuilder("span")

interface ElementBuilderMutator {
    fun mutate(eb: ElementBuilder)
}

class ElementBuilder(val tag: String) : ToReactElementable {
    var tame: String? = null; var shame: String? = null
    var tamy: String? = null; var shamy: String? = null; var tamyShamy: String? = null
    var style = Style()
    var onClick: ((MouseEvent) -> Unit)? = null
    val children = mutableListOf<ToReactElementable>()

    operator fun invoke(tame: String? = null, shame: String? = null,
                        tamy: String? = null, shamy: String? = null, tamyShamy: String? = null,
                        block: ((ElementBuilder) -> Unit)? = null
    ): ElementBuilder {
        this.tame = tame; this.shame = shame
        this.tamy = tamy; this.shamy = shamy; this.tamyShamy = tamyShamy
        block?.let {it(this)}
        return this
    }

    operator fun invoke(vararg mutators: ElementBuilderMutator, block: ((ElementBuilder) -> Unit)? = null): ElementBuilder {
        mutators.forEach {it.mutate(this)}
        block?.let {it(this)}
        return this
    }

    operator fun invoke(block: (ElementBuilder) -> Unit): ElementBuilder {
        block(this)
        return this
    }

    operator fun minus(eb: ToReactElementable) {
        children.add(eb)
    }

    operator fun minus(s: String) {
        minus(s.asReactElement())
    }

    operator fun minus(re: ReactElement) {
        minus(object:ToReactElementable {
            override fun toReactElement() = re
        })
    }

    override fun toReactElement(): ReactElement {
        @Suppress("UnsafeCastFromDynamic")
        return Shitus.diva.apply(null, js("[]").concat(
            dyna{o->
                o.style = style.toReactStyle()
            },
            /*...*/children.map{it.toReactElement()}.toJSArray()
        ))
    }
}

fun newShitUsage() {
    kdiv(tame="qwe")(TextAlign.CENTER) {o->
        o- "Some shitty text"
        o- kdiv {
            o- "Fucking lorem"
            o- "Crappy ipsum"
        }

        o.onClick = {e->
            e.preventDefault()
            println("He fucking clicked on me")
        }
    }
}

enum class TextAlign(val string: String) : ElementBuilderMutator {
    LEFT("left"), CENTER("center"), RIGHT("right");

    override fun toString() = string
    override fun mutate(eb: ElementBuilder) {eb.style.textAlign = this}
}

data class Style(
    var textAlign: TextAlign? = null) {

    fun toReactStyle(): dynamic {
        return dyna{o->
            textAlign?.let {o.textAlign = it.string}
        }
    }
}

















