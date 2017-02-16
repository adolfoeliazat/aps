/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import into.kommon.*
import org.w3c.dom.events.MouseEvent
import kotlin.js.Json
import kotlin.js.json

open class LinkKey(override val fqn: String) : Fucker(), FQNed

fun jsFacing_link(def: Json): ReactElement {
    var content: dynamic = def["content"]
    val title: String? = def["title"].asDynamic()
    val className: String = (def["className"].asDynamic()) ?: ""
    val style: dynamic = def["style"] ?: json()
    val onClick: dynamic = def["onClick"]
    val onMouseEnter: dynamic = def["onMouseEnter"]
    val onMouseLeave: dynamic = def["onMouseLeave"]

    Shitus.invariant(!(content != null && title != null), "It should be either content or title")

    if (title != null) {
        content = Shitus.spancTitle(json("title" to title))
    }

    var me: dynamic = null
    me = json(
        "render" to {
            Shitus.aa(json(
                "id" to me.elementID,
                "className" to className,
                "style" to style,
                "href" to "#",
                "onMouseEnter" to onMouseEnter,
                "onMouseLeave" to onMouseLeave),
            content)
        },

        "onRootClick" to {e: MouseEvent -> async<Unit> {
            e.preventDefault()
            e.stopPropagation()
            val shit: (() -> Promisoid<Unit>)? = onClick
            shit?.let {await(it())}
        }}
    )

    me.controlTypeName = "link"
    me.tamyPrefix = "link"
    legacy_implementControlShit(json("me" to me, "def" to def, "implementTestClick" to json("onClick" to me.onRootClick)))
    return elcl(me)
}

//fun jsFacing_urlLink(ui: World, def: dynamic): dynamic {
//    val name = def.name // TODO:vgrechka Seems to be unused
//    val url = def.url
//    val delayActionForFanciness = def.delayActionForFanciness
//    val blinkOpts = def.blinkOpts
//    val style = def.style
//
//    val id = puid()
//
//    val linkDef: dynamic = json(
//        "controlTypeName" to "urlLink",
//        "style" to style,
//        "id" to id,
//        "onClick" to {async{
//            await(effects).blinkOn(global.Object.assign(json("target" to Shitus.byid(id), "dtop" to 3), blinkOpts))
//            if (name != null) {
//                Shitus.byid(id).css("text-decoration", "none")
////                TestGlobal["link_" + name + "_blinks"] = true
//            }
//
//            if (delayActionForFanciness && !(isInTestScenario() && art.testSpeed == "fast")) {
//                await<dynamic>(Shitus.delay(global.ACTION_DELAY_FOR_FANCINESS))
//            }
//
//            await<dynamic>(Shitus.entraina(json("name" to "Navigate via urlLink: ${url}", "act" to {async{
//                await<dynamic>(ui.pushNavigate(url))
//            }})))
//
//            effects.blinkOff()
//            if (name) {
//                Shitus.byid(id).css("text-decoration", "")
////                TestGlobal["link_" + name + "_blinks"] = false
//            }
//        }}
//    )
//
//    return Shitus.link(Shitus.asn1(linkDef, def))
//}

fun jsFacing_pageLink(ui: World, def: dynamic) {
    Shitus.raise("Kill me please, I don’t deserve living")
}

@GenerateSignatureMixes
fun urlLink(
    key: LinkKey? = null,
    url: String,
    delayActionForFanciness: Boolean = false,
    blinkOpts: dynamic = null,
    @Mix linkParams: LinkParams,
    @Mix attrs: Attrs = Attrs(),
    @Mix style: Style = Style()
): ToReactElementable {
    val ui = hrss.browserOld.ui
    val id = puid()

    val doClick: suspend () -> Unit = {
        val blinker = await(effects).blinkOn(byid(id), BlinkOpts(dtop = "3px"))
        TestGlobal.shitHalfwayLock.resumeTestAndPauseSutFromSut()
        Shitus.byid(id).css("text-decoration", "none")

        if (delayActionForFanciness && !(isInTestScenario() && art.testSpeed == "fast")) {
            await<dynamic>(Shitus.delay(global.ACTION_DELAY_FOR_FANCINESS))
        }

        ui.pushNavigate(url)

        blinker.unblink()
        Shitus.byid(id).css("text-decoration", "")
        TestGlobal.shitDoneLock.resumeTestFromSut()
    }

    return link(
        linkParams,
        attrs.copy(
            controlTypeName = attrs.controlTypeName ?: "urlLink",
            id = id,
            onClicka = {doClick()}
        ),
        style,
        key = key,
        doClick = {async{doClick()}}
    )
}

@MixableType
data class LinkParams(
    val content: ToReactElementable? = null,
    val title: String? = null
)

class Link(
    val params: LinkParams,
    attrs: Attrs = Attrs(),
    val style: Style = Style(),
    val key: LinkKey? = null,
    val doClick: (suspend () -> Unit)? = null
) : Control2(attrs) {
    init {
        check(params.content != null || params.title != null) {"Either content or title"}
    }

    val content = params.content ?: oldShitAsToReactElementable(jsFacing_spancTitle(json("title" to params.title)))

    companion object {
        val instances = mutableMapOf<LinkKey, Link>()

        fun instance(key: LinkKey): Link {
            return instances[key] ?: bitch("No Link keyed `${key.fqn}`")
        }
    }

    override fun componentDidMount() {
        if (key != null) {
            instances[key] = this
        }
    }

    override fun componentWillUnmount() {
        if (key != null) {
            instances.remove(key)
        }
    }

    fun click() {
        async {doClick!!()}
    }

    override fun defaultControlTypeName() = "link"
    override fun simpleOnRootClickImpl() = true
    override fun simpleTestClickImpl() = true

    override fun render(): ToReactElementable {
        return oldShitAsToReactElementable(Shitus.aa(
            json("id" to elementID,
                 "className" to attrs.className,
                 "style" to style.toReactStyle(),
                 "href" to "#",
                 "onMouseEnter" to {e: MouseEvent -> async {
                     attrs.onMouseEnter?.let {it(e)}
                     attrs.onMouseEntera?.let {it(e)}
                 }},
                 "onMouseLeave" to {e: MouseEvent -> async {
                     attrs.onMouseLeave?.let {it(e)}
                     attrs.onMouseLeava?.let {it(e)}
                 }}),
            content.toReactElement()
        ))
    }
}

@GenerateSignatureMixes
fun link(
    @Mix params: LinkParams,
    @Mix attrs: Attrs = Attrs(),
    @Mix style: Style = Style(),
    key: LinkKey? = null,
    doClick: (suspend () -> Unit)? = null
): ToReactElementable {
    return Link(params, attrs, style, key = key, doClick = doClick)
}

fun DummyMouseEvent() = MouseEvent("dummyTypeArg")


suspend fun linkClick(key: TestRef<LinkKey>, handOpts: HandOpts = HandOpts()) {
    val target = Link.instance(key.it)
    await(TestUserActionAnimation.hand(target, handOpts))
    target.click()
}

//suspend fun linkSequence(
//    descr: String,
//    key: String,
//    aid: String
//) {
//    sequence(
//        action = {
//            linkClick(key)
//        },
//        descr = descr,
//        steps = listOf(
//            PauseAssertResumeStep(TestGlobal.linkTickingLock, "$aid--1"),
//            PauseAssertResumeStep(TestGlobal.linkDoneLock, "$aid--2")
//        )
//    )
//}










