package aps.front

import aps.*
import into.kommon.*
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.events.MouseEventInit
import kotlin.browser.window

open class Button(
    val key: String? = null,
    val style: Style = Style(),
    val className: String = "",
    val level: Level = Button.Level.DEFAULT,
    val title: String? = null,
    val icon: IconClass? = null,
    val iconColor: Color? = null,
    val disabled: Boolean = false,
    val hint: String? = null,
    var onClicka: () -> Promise<Unit> = {async{}},
    val onClick: () -> Unit = {}
) : Control2(Attrs()) {

    companion object {
        val instances = mutableMapOf<String, Button>()

        fun instance(key: String): Button {
            return instances[key] ?: bitch("No Button keyed `$key`")
        }
    }

    enum class Level(val string: String) {
        DEFAULT("default"), PRIMARY("primary");
        override fun toString() = string
    }

    open fun click(): Promise<Unit> {
        onClick()
        return onClicka()
    }

    override fun render(): ToReactElementable {
        return ToReactElementable.from(reactCreateElement(
            "button",
            json(
                "id" to elementID,
                "className" to "btn btn-$level $className",
                "style" to style,
                "title" to hint,
                "disabled" to disabled,
                "onClick" to {e: ReactEvent ->
                    preventAndStop(e)
                    click()
                }
            ),
            listOf(
//                icon?.let {ki(className = "fa fa-$it", color = iconColor).toReactElement()},
                icon?.let {ki(className = it.className, color = iconColor).toReactElement()},
                ifornull(icon != null && title != null) {nbsp.asReactElement()},
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

fun TestScenarioBuilder.buttonClick(key: String) {
    acta("Clicking button `$key`") {
        Button.instance(key).click()
    }
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






