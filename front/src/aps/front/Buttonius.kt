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
    var onClicka: () -> Promise<Unit> = {async{}},
    val onClick: () -> Unit = {}
) : Control2(Attrs(id = id)) {

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

    open fun click(): Promise<Unit> = async {
        onClick()
        await(onClicka())
    }

    override fun render(): ToReactElementable {
        val jsAttrs = json(
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
        dataDismiss?.let {jsAttrs["data-dismiss"] = it}

        return ToReactElementable.from(reactCreateElement(
            "button",
            jsAttrs,
            listOf(
//                icon?.let {ki(className = "fa fa-$it", color = iconColor).toReactElement()},
                icon?.let {ki(className = it.className, color = iconColor).toReactElement()},
                ifornull(icon != null && title != null) {symbols.nbsp.asReactElement()},
                title?.asReactElement()
            )
        ))
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
    acta("Clicking button `$key`") {async{
        val target = Button.instance(key)
        await(TestUserActionAnimation.hand(target, handOpts))
        target.click().finally {
            TestGlobal.actionSignal!!.resolve()
        }
    }}
}

fun TestScenarioBuilder.buttonUserInitiatedClick(key: String) {
    acta("Clicking button `$key`") {async{
        val successOrTimeout = Promise<Unit> {resolve, reject ->
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






