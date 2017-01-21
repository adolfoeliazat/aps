/*
 * Into Kotlin
 *
 * (C) Copyright 2016 Vladimir Grechka
 */

package kommon.into.kotlin.test.shit2analyze

class ElementBuilderFactory(val tag: String) {
    @GenerateSignatureMixes(extensionOf="ElementBuilderFactory")
    operator fun invoke(@Mix attrs: Attrs,
                        @Mix style: Style,
                        @AfterMix block: ((ElementBuilder) -> Unit)? = null
    ): ElementBuilder {
        return ElementBuilder(tag, attrs, style)
    }
}

data class ElementBuilder(val tag: String, val attrs: Attrs, val style: Style) {
    init {
        println("ElementBuilder: $this")
    }
}


