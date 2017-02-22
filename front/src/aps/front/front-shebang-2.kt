package aps.front

import aps.*

object FieldSpecToCtrlKey {
    private val map = mutableMapOf<FieldSpec, Any>()

    operator fun get(spec: TextFieldSpec) = get(spec, ::InputKey)
    operator fun get(spec: IntFieldSpec) = get(spec, ::InputKey)
    operator fun get(spec: CheckboxFieldSpec) = get(spec, ::CheckboxKey)
    operator fun <E> get(spec: SelectFieldSpec<E>) where E : Enum<E>, E : Titled = get(spec, {SelectKey<E>(it)})

    private operator fun <K : Any> get(spec: FieldSpec, makeKey: (String) -> K): K =
        cast(map.getOrPut(spec) {makeKey(spec.name)})
}

suspend fun twoStepSequence(action: suspend () -> Unit, aid: String) {
    sequence(action,
             steps = listOf(
                 PauseAssertResumeStep(TestGlobal.shitHalfwayLock, "$aid--halfway"),
                 PauseAssertResumeStep(TestGlobal.shitDoneLock, "$aid--done")))
}

suspend fun halfwayThenModalSequence(action: suspend () -> Unit, modalAction: suspend () -> Unit, aid: String) {
    TestGlobal.shitHalfwayLock.reset()
    TestGlobal.modalShownLock.reset()
    TestGlobal.modalHiddenLock.reset()

    action()

    TestGlobal.shitHalfwayLock.let {
        it.pauseTestFromTest()
        assertScreenHTML("Halfway", aid = "$aid-halfway")
        it.resumeSutFromTest()
    }

    TestGlobal.modalShownLock.let {
        it.pauseTestFromTest()
        assertScreenHTML("Modal", aid = "$aid-modal")
        it.resumeSutFromTest()
    }

    modalAction()

    TestGlobal.modalHiddenLock.let {
        it.pauseTestFromTest()
        assertScreenHTML("Done", aid = "$aid-done")
        it.resumeSutFromTest()
    }
}















