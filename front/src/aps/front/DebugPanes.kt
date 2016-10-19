/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

val debugPanes: dynamic by lazy {
    json(
        "names" to json(),

        "set" to {def: dynamic ->
            val name: dynamic = def.name
            val element: dynamic = def.element
            var parentJqel: dynamic = def.parentJqel

            if (!parentJqel) parentJqel = js("$")(global.document.body)

            debugPanes.delete(json("name" to name))

            val id = "debugPanes-${name}"
            var domel = Shitus.byid0(id)
            if (!domel) {
                parentJqel.append("<div id='${id}'></div>")
                domel = Shitus.byid0(id)
            }

            global.ReactDOM.render(element, domel)

            debugPanes.names[name] = true
        },

        "delete" to {def: dynamic ->
            val name = def.name

            val id = "debugPanes-${name}"
            val domel = Shitus.byid0(id)
            if (domel) {
                global.ReactDOM.unmountComponentAtNode(domel)
                domel.remove()
            }

            jsFacing_deleteKey(debugPanes.names, name)
        },

        "deleteAll" to {
            for (name in jsArrayToList(global.Object.keys(debugPanes.names))) {
                debugPanes.delete({name})
            }
        },

        "isSet" to {arg: dynamic ->
            val name = arg.name
            debugPanes.names[name]
        }
    )
}

