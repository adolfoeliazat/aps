/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

class ElementBuilderFactory(val tag: String) {
    @GenerateSignatureMixes
    operator fun invoke(@Mix attrs: Attrs,
                        @Mix style: Style,
                        block: ((ElementBuilder) -> Unit)? = null
    ): ElementBuilder {
        val builder = ElementBuilder(tag, attrs, style)
        block?.let {it(builder)}
        return builder
    }

    operator fun invoke(@Mix style: Style,
                        block: ((ElementBuilder) -> Unit)? = null
    ): ElementBuilder = invoke(Attrs(), style, block)

    operator fun invoke(className: String,
                        block: ((ElementBuilder) -> Unit)? = null
    ): ElementBuilder = invoke(Attrs(className = className), Style(), block)

    operator fun invoke(iconClass: IconClass,
                        block: ((ElementBuilder) -> Unit)? = null
    ): ElementBuilder = invoke(Attrs(className = iconClass.className), Style(), block)

    operator fun invoke(id: String,
                        className: String,
                        block: ((ElementBuilder) -> Unit)? = null
    ): ElementBuilder = invoke(Attrs(id = id, className = className), Style(), block)

    operator fun invoke(id: String,
                        style: Style,
                        block: ((ElementBuilder) -> Unit)? = null
    ): ElementBuilder = invoke(Attrs(id = id), style, block)

}

