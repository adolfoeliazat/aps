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

//    operator fun invoke(attrs: Attrs, block: ((ElementBuilder) -> Unit)? = null): ElementBuilder {
//        return invoke(attrs, Style(), block)
//    }
//
//    operator fun invoke(style: Style, block: ((ElementBuilder) -> Unit)? = null): ElementBuilder {
//        return invoke(Attrs(), style, block)
//    }

//    operator fun invoke(block: ((ElementBuilder) -> Unit)? = null): ElementBuilder {
//        return invoke(Attrs(), Style(), block)
//    }

//    //---------- BEGIN GENERATED SHIT { ----------
//
//    operator fun invoke(
//        tame: String? = null,
//        tamy: String? = null,
//        shame: String? = null,
//        shamy: String? = null,
//        tamyShamy: String? = null,
//        controlTypeName: String? = null,
//        id: String? = null,
//        tattrs: Json? = null,
//        noStateContributions: Boolean? = null,
//        className: String? = null,
//        marginTop: Any? = null,
//        marginBottom: Any? = null,
//        paddingBottom: Any? = null,
//        padding: Any? = null,
//        color: Any? = null,
//        backgroundColor: Any? = null,
//        borderBottom: String? = null,
//        textAlign: String? = null,
//        fontWeight: String? = null,
//        display: String? = null,
//        justifyContent: String? = null,
//            block: ((ElementBuilder) -> Unit)? = null): ElementBuilder
//        = invoke(
//            Attrs(
//            tame=tame,
//            tamy=tamy,
//            shame=shame,
//            shamy=shamy,
//            tamyShamy=tamyShamy,
//            controlTypeName=controlTypeName,
//            id=id,
//            tattrs=tattrs,
//            noStateContributions=noStateContributions,
//            className=className),
//            Style(
//            marginTop=marginTop,
//            marginBottom=marginBottom,
//            paddingBottom=paddingBottom,
//            padding=padding,
//            color=color,
//            backgroundColor=backgroundColor,
//            borderBottom=borderBottom,
//            textAlign=textAlign,
//            fontWeight=fontWeight,
//            display=display,
//            justifyContent=justifyContent),
//            block)
//
//    //---------- END GENERATED SHIT } ----------
}

