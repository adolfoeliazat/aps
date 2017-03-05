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
    val onMouseEnter: suspend () -> Unit = {},
    val onMouseLeave: suspend () -> Unit = {},
    val onClicka: suspend () -> Unit = {}
) : Control2(Attrs(id = id)) {

    companion object {
        val instances = mutableMapOf<KicKey, kic>()

        fun instance(key: KicKey): kic {
            return instances[key] ?: bitch("No kic keyed `${key.fqn}`")
        }
    }

    private suspend fun click() {
        onClick()
        onClicka()
    }

    fun testClick() {
        notAwait {click()}
        if (dataToggle == "dropdown") {
            byid(elementID).asDynamic().dropdown("toggle")
        }
    }

    private suspend fun mouseEnter() {
        onMouseEnter()
    }

    fun testMouseEnter() {
        notAwait {mouseEnter()}
    }

    private suspend fun mouseLeave() {
        onMouseLeave()
    }

    fun testMouseLeave() {
        notAwait {mouseLeave()}
    }

    override fun render(): ToReactElementable =
        ki(Attrs(id = elementID, className = className, dataToggle = dataToggle,
                 onClick = {async{click()}},
                 onMouseEnter = {async{mouseEnter()}},
                 onMouseLeave = {async{mouseLeave()}}),
           style)

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

    suspend fun hand(handOpts: HandOpts = HandOpts()) {
        await(TestUserActionAnimation.hand(this, handOpts))
    }
}

object tkic {
    val click = fucker {it.hand(); it.testClick()}
    val mouseEnter = fucker {it.testMouseEnter(); it.hand()}
    val mouseEnterClick = fucker {it.testMouseEnter(); it.hand(); it.testClick()}
    val mouseLeave = fucker {it.testMouseLeave()}

    class fucker(val block: suspend (kic) -> Unit) {
        suspend operator fun invoke(key: TestRef<KicKey>, subscript: Any? = null) {
            val target = kic.instance(if (subscript == null) key.it
                                      else SubscriptKicKey(key.it, subscript))
            block(target)
        }
    }
}











