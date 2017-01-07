package aps.front

import aps.*
import into.kommon.*


class kic(
    val key: String? = null,
    val style: Style = Style(),
    val className: String = "",
    val onClicka: () -> Promise<Unit> = {async{}},
    val onClick: () -> Unit = {}
) : Control2(Attrs()) {

    companion object {
        val instances = mutableMapOf<String, kic>()

        fun instance(key: String): kic {
            return instances[key] ?: bitch("No kic keyed `$key`")
        }
    }

    fun click(): Promise<Unit> {
        onClick()
        return onClicka()
    }

    override fun render(): ToReactElementable =
        ki(id = elementID, className = className, baseStyle = style, onClick = {click()})

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

fun TestScenarioBuilder.kicClick(key: String, handOpts: HandOpts = HandOpts()) {
    acta("Clicking kic `$key`") {async{
        val target = kic.instance(key)
        await(TestUserActionAnimation.hand(target, handOpts))
        target.click()
    }}
}

fun TestScenarioBuilder.kicClickNoWait(key: String) {
    act("Clicking kic `$key`") {
        kic.instance(key).click()
    }
}




