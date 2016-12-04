package aps.front

import aps.*

abstract class WriterBootTestScenario_FuckerToken : WriterBootTestScenario() {
    protected abstract fun setFuckerFields(o: TestSetUserFieldsRequest)

    override fun prepareShit(): Promise<Unit> {"__async"
        val req = TestSetUserFieldsRequest()
        setFuckerFields(req)
        return __reawait(prepareFucker(req))
    }

    override fun fillStorageLocal(tsl: TypedStorageLocal) {
        tsl.token = fuckerToken
    }

    override fun buildStepsAfterDisplayInitialShit() {
        o.state("There's some token in localStorage, checking it")
        assert_breatheBanner_rightNavbarEmpty()
    }
}