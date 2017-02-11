package aps.front

import aps.*
import into.kommon.*

open class KicKey(override val fqn: String) : Fucker(), FQNed

data class SubscriptKicKey(val key: KicKey, val subscript: Any?)
    : KicKey(key.fqn + "-$subscript")

class kic(
    val className: String = "",
    val key: KicKey? = null,
    val style: Style = Style(),
    val onClick: () -> Unit = {},
    val onClicka: suspend () -> Unit = {}
) : Control2(Attrs()) {

    companion object {
        val instances = mutableMapOf<KicKey, kic>()

        fun instance(key: KicKey): kic {
            return instances[key] ?: bitch("No kic keyed `${key.fqn}`")
        }
    }

    suspend fun click() {
        onClick()
        onClicka()
    }

    override fun render(): ToReactElementable =
        ki(id = elementID, className = className, baseStyle = style, onClick = {async{click()}})

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
    notAwait {target.click()}
}












