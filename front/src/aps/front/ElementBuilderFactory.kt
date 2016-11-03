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

}

