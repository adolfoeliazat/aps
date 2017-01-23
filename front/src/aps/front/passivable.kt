package aps.front

import aps.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

interface PassivableInitializer<out API> {
    fun initialize(): Promisoid<Passivable<API>>
}

interface Passivable<out API> {
    val api: API
    fun passivate(): Promisoid<Passivated<API>>
}

interface Passivated<out API> {
    fun activate(): Promisoid<Passivable<API>>
}


class PassivableHolder<API: Any>(val initializer: PassivableInitializer<API>) : ReadOnlyProperty<Any?, Promisoid<API>> {
    sealed class State<API> {
        class Uninitialized<API> : State<API>()
        class Active<API>(val passivable: Passivable<API>) : State<API>()
        class Passive<API>(val passivated: Passivated<API>) : State<API>()
    }

    var state: State<API> = State.Uninitialized()

    override fun getValue(thisRef: Any?, property: KProperty<*>): Promisoid<API> = async {
        val state = this.state
        when (state) {
            is State.Uninitialized -> {
                await(turnToActive {initializer.initialize()})
            }
            is State.Active -> {
                state.passivable.api
            }
            is State.Passive -> {
                await(turnToActive {state.passivated.activate()})
            }
        }
    }

    private fun turnToActive(how: () -> Promisoid<Passivable<API>>): Promisoid<API> = async {
        val passivable = await(how())
        val newState = State.Active(passivable)
        this.state = newState
        newState.passivable.api
    }
}





