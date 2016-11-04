/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

@GenerateSignatureMixes
fun faIcon(icon: String, @Mix attrs: Attrs, @Mix style: Style): ToReactElementable {
    return object:Control2(attrs) {
        override fun defaultControlTypeName() = "icon"
        override fun simpleOnRootClickImpl() = true
        override fun simpleTestClickImpl() = true

        override fun render() =
            Shitus.ia(json("id" to elementID,
                           "className" to "fa fa-${icon} ${attrs.className}",
                           "style" to style.toReactStyle()))

        override fun contributeTestState(state: dynamic) {
            state.put(json("control" to this, "key" to tamePath(), "value" to icon))
        }
    }
}

fun jsFacing_faIcon(def: dynamic): ReactElement {
    // #extract {icon, style, className='', onClick} from def
    val icon = def.icon; val style = def.style; val onClick = def.onClick
    val className = if (def.className) def.className else ""

    var me: dynamic = undefined // @workaround
    me = json(
        "render" to {
            Shitus.ia(json("id" to me.elementID, "className" to "fa fa-${icon} ${className}", "style" to style, "onClick" to {
                if (onClick) onClick()
            }))
        },
        "contributeTestState" to {state: dynamic ->
            if (me.tame) {
                state.put(json("control" to me, "key" to me.getTamePath(), "value" to icon))
            }
        }
    )

    // def.`$definitionStack` = promiseDefinitionStack(js("Error()"), 1)

    me.controlTypeName = "icon"
    legacy_implementControlShit(json("me" to me, "def" to def, "implementTestClick" to json("onClick" to onClick)))
    return jsFacing_elcl(me)
}




