package aps.front

import aps.*
import into.kommon.*
import kotlin.js.json

open class ButtonKey(override val name: String) : NamedItem

data class SubscriptButtonKey(val key: ButtonKey, val subscript: Any?)
    : ButtonKey(key.name + "-$subscript")

abstract class ButtonKeyRefs(val group: NamedGroup<ButtonKey>) {
    protected val key = ButtonKey(name = qualifyMe(group))
}

open class Button(
    val key: ButtonKey? = null,
    id: String? = null,
    val style: Style = Style(),
    val className: String = "",
    val level: Level = Button.Level.DEFAULT,
    val title: String? = null,
    val icon: IconClass? = null,
    val iconColor: Color? = null,
    val volatileDisabled: (() -> Boolean)? = null,
    val disabled: Boolean = false,
    val hint: String? = null,
    val dataDismiss: String? = null,
    val dropDownMenu: Menu? = null,
    val separateDropDownMenuButton: Boolean = false,
    val dropDownMenuDirection: MenuDirection = Button.MenuDirection.DOWN,
    val narrowCaret: Boolean = false,
    val onClick: () -> Unit = {},
    var onClicka: suspend () -> Unit = {}
) : Control2(Attrs(id = id)) {

    enum class MenuDirection(val string: String) {
        DOWN("down"),
        UP("up")
    }

    companion object {
        val instances = mutableMapOf<ButtonKey, Button>()

        fun instance(key: ButtonKey): Button {
            return instances[key] ?: bitch("No Button keyed `${key.name}`")
        }
    }

    enum class Level(val string: String) {
        DEFAULT("default"), PRIMARY("primary"), DANGER("danger");
        override fun toString() = string
    }

    open suspend fun click(): Promisoid<Unit> = async {
        onClick()
        onClicka()
    }

    override fun render(): ToReactElementable {
        val buttonJSAttrs = json(
            "id" to elementID,
            "className" to "btn btn-$level $className",
            "style" to style,
            "title" to hint,
            "disabled" to (volatileDisabled?.let {it()} ?: disabled),
            "onClick" to {e: ReactEvent ->
                preventAndStop(e)
                asu {click()}
            }
        )
        dataDismiss?.let {buttonJSAttrs["data-dismiss"] = it}

        val button = ToReactElementable.from(reactCreateElement(
            "button",
            buttonJSAttrs,
            listOf(
                icon?.let {ki(className = it.className, color = iconColor).toReactElement()},
                ifOrNull(icon != null && title != null) {const.text.symbols.nbsp.asReactElement()},
                title?.asReactElement()
            )
        ))

        if (dropDownMenu == null) {
            return button
        } else {
            if (!separateDropDownMenuButton) imf("separateDropDownMenuButton == false")
            return ToReactElementable.from(reactCreateElement(
                "div",
                json("className" to "btn-group ${ifOrEmpty(dropDownMenuDirection == MenuDirection.UP){"dropup"}}"),
                listOf(
                    button.toReactElement(),
                    reactCreateElement(
                        "button",
                        json("type" to "button",
                             "className" to "btn btn-$level dropdown-toggle",
                             "data-toggle" to "dropdown",
                             "aria-haspopup" to "true",
                             "aria-expanded" to "false",
                             "style" to json()-{o->
                                 if (narrowCaret) {
                                     o["paddingLeft"] = "0.5rem"
                                     o["paddingRight"] = "0.5rem"
                                 }
                             }),
                        listOf(
                            reactCreateElement(
                                "span",
                                json("className" to "caret"),
                                listOf()),
                            reactCreateElement(
                                "span",
                                json("className" to "sr-only"),
                                listOf(
                                    asReactElement("Toggle Dropdown")
                                ))
                        )),
                    reactCreateElement(
                        "ul",
                        json("className" to "dropdown-menu"),
                        dropDownMenu.items.map {item->
                            reactCreateElement(
                                "li",
                                json(),
                                listOf(
                                    reactCreateElement(
                                        "a",
                                        json("href" to "#",
                                             "onClick" to {e: ReactEvent->
                                                 preventAndStop(e)
                                                 item.act()
                                             }),
                                        listOf(
                                            asReactElement(item.title)
                                        )
                                    )
                                )
                            )
                        }
                    )
                )
            ))
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

}

suspend fun buttonClick(key: TestRef<ButtonKey>, handOpts: HandOpts = HandOpts()) {
    val target = Button.instance(key.it)
    await(TestUserActionAnimation.hand(target, handOpts))
    notAwait {target.click()}
}

fun TestScenarioBuilder.buttonUserInitiatedClick(key: String) {
    imf("reimplement buttonUserInitiatedClick")
//    acta("Clicking button `$key`") {async{
//        val successOrTimeout = Promisoid<Unit> {resolve, reject ->
//            timeoutSet(1000) {reject(Exception("Timed out waiting for a fucking robot click"))}
//            window.onclick = {
//                window.onclick = null
//                Button.instance(key).click()
//                resolve(Unit)
//            }
//        }
//
//        try {
//            await(fuckingRemoteCall.robotClickOnChrome())
//            await(successOrTimeout)
//        } finally {
//            window.onclick = null
//        }
//    }}
}






