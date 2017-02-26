package aps.front

import aps.*
import into.kommon.*
import jquery.jq

open class KicKey(override val fqn: String) : Fucker(), FQNed

data class SubscriptKicKey(val key: KicKey, val subscript: Any?)
    : KicKey(key.fqn + "-$subscript")

class kic(
    val className: String = "",
    id: String? = null,
    val key: KicKey? = null,
    val style: Style = Style(),
    val dataToggle: String? = null,
    val onClick: () -> Unit = {},
    val onClicka: suspend () -> Unit = {}
) : Control2(Attrs(id = id)) {

    companion object {
        val instances = mutableMapOf<KicKey, kic>()

        fun instance(key: KicKey): kic {
            return instances[key] ?: bitch("No kic keyed `${key.fqn}`")
        }
    }

    suspend fun click(fromTest: Boolean = false) {
        onClick()
        onClicka()

        if (fromTest) {
            if (dataToggle == "dropdown") {
                byid(elementID).asDynamic().dropdown("toggle")
            }
        }
    }

    override fun render(): ToReactElementable =
        ki(Attrs(id = elementID, className = className, dataToggle = dataToggle, onClick = {async{click()}}), style)

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

suspend fun kicClick(key: TestRef<KicKey>, subscript: Any? = null, handOpts: HandOpts = HandOpts()) {
    val target = kic.instance(if (subscript == null) key.it
                              else SubscriptKicKey(key.it, subscript))
    await(TestUserActionAnimation.hand(target, handOpts))
    notAwait {target.click(fromTest = true)}
}












