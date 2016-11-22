package aps.front

import aps.*

abstract class WriterBootTestScenario_FuckerToken : WriterBootTestScenario() {
    protected abstract val fuckerState: UserState

    override fun prepareShit(): Promise<Unit> {"__async"
        return __reawait(prepareFucker(fuckerState))
    }

    override fun fillStorageLocal() {
        typedStorageLocal.token = fuckerToken
    }

    override fun buildStepsAfterDisplayInitialShit() {
        o.state("There's some token in localStorage, checking it")
        assert_breatheBanner_rightNavbarEmpty()
    }
}