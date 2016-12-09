package aps.front

import aps.*
import into.kommon.*

class Button(
    val key: String? = null,
    val style: Style = Style(),
    val className: String = "",
    val level: Level = Button.Level.DEFAULT,
    val title: String? = null,
    val icon: String? = null,
    val iconColor: Color? = null,
    val disabled: Boolean = false,
    val hint: String? = null,
    val onClicka: () -> Promise<Unit> = {async{}}
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

    fun click(): Promise<Unit> = onClicka()

    override fun render(): ToReactElementable {
        return ToReactElementable.from(reactCreateElement(
            "button",
            json(
                "className" to "btn btn-$level $className",
                "style" to style,
                "title" to hint,
                "onClick" to {e: ReactEvent ->
                    preventAndStop(e)
                    click()
                }
            ),
            listOf(
                icon?.let {ki(className = "fa fa-$it", color = iconColor).toReactElement()},
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






