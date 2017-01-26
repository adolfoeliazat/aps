package aps.front

import aps.*
import into.kommon.*
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.events.MouseEventInit
import kotlin.browser.window

open class Button(
    val key: String? = null,
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
    var onClicka: () -> Promisoid<Unit> = {async{}},
    val onClick: () -> Unit = {}
) : Control2(Attrs(id = id)) {

    enum class MenuDirection(val string: String) {
        DOWN("down"),
        UP("up")
    }

    companion object {
        val instances = mutableMapOf<String, Button>()

        fun instance(key: String): Button {
            return instances[key] ?: bitch("No Button keyed `$key`")
        }
    }

    enum class Level(val string: String) {
        DEFAULT("default"), PRIMARY("primary"), DANGER("danger");
        override fun toString() = string
    }

    open fun click(): Promisoid<Unit> = async {
        onClick()
        await(onClicka())
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
                click()
            }
        )
        dataDismiss?.let {buttonJSAttrs["data-dismiss"] = it}

        val button = ToReactElementable.from(reactCreateElement(
            "button",
            buttonJSAttrs,
            listOf(
                icon?.let {ki(className = it.className, color = iconColor).toReactElement()},
                ifornull(icon != null && title != null) {symbols.nbsp.asReactElement()},
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

fun TestScenarioBuilder.buttonClick(key: String, handOpts: HandOpts = HandOpts()) {
    acta("Clicking button `$key`") {buttonClick2(key, handOpts)}
}

fun buttonClick2(key: String, handOpts: HandOpts = HandOpts()): Promisoid<Unit> {
    return async<Unit> {
        val target = Button.instance(key)
        await(TestUserActionAnimation.hand(target, handOpts))
        target.click() // Not await
    }
}

fun TestScenarioBuilder.buttonUserInitiatedClick(key: String) {
    acta("Clicking button `$key`") {async{
        val successOrTimeout = Promisoid<Unit> {resolve, reject ->
            timeoutSet(1000) {reject(Exception("Timed out waiting for a fucking robot click"))}
            window.onclick = {
                window.onclick = null
                Button.instance(key).click()
                resolve(Unit)
            }
        }

        try {
            await(fuckingRemoteCall.robotClickOnChrome())
            await(successOrTimeout)
        } finally {
            window.onclick = null
        }
    }}
}






