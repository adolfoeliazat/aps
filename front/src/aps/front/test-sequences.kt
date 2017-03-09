package aps.front

import aps.*
import aps.front.testutils.*
import into.kommon.*

suspend fun sequence(
    action: suspend () -> Unit,
    descr: String = "Describe me",
    steps: List<SequenceStep>,
    aopts: AssertScreenOpts? = null
) {
    steps.forEach {it.beforeAnySteps()}

    action()

    for ((i, step) in steps.withIndex()) {
        val stepDescr = step.descr ?: "${i + 1}"
        step.act("$descr [$stepDescr]", aopts)
    }
}

suspend fun step(action: suspend () -> Unit, lock: TestLock, aid: String) {
    sequence(action = action, steps = listOf(PauseAssertResumeStep(lock, aid)))
}

object seq

suspend fun seq.halfway_done(action: suspend () -> Unit, aid: String) {
    sequence(action,
             steps = listOf(
                 PauseAssertResumeStep(TestGlobal.shitHalfwayLock, "$aid--halfway"),
                 PauseAssertResumeStep(TestGlobal.shitDoneLock, "$aid--done")))
}

suspend fun seq.halfway_modal(action: suspend () -> Unit, aid: String) {
    TestGlobal.shitHalfwayLock.reset()
    TestGlobal.modalShownLock.reset()

    action()

    TestGlobal.shitHalfwayLock.let {
        it.pauseTestFromTest()
        assertScreenHTML("Halfway", aid = "$aid--halfway")
        it.resumeSutFromTest()
    }

    TestGlobal.modalShownLock.let {
        it.pauseTestFromTest()
        assertScreenHTML("Modal", aid = "$aid--modal")
        it.resumeSutFromTest()
    }
}

suspend fun seq.halfway_modal_closed(action: suspend () -> Unit, modalAction: suspend () -> Unit, aid: String) {
    TestGlobal.modalHiddenLock.reset()

    seq.halfway_modal(action, "$aid--1")

    modalAction()

    TestGlobal.modalHiddenLock.let {
        it.pauseTestFromTest()
        assertScreenHTML("Modal hidden", aid = "$aid--modalHidden")
        it.resumeSutFromTest()
    }
}

suspend fun seq.halfway_pageLoaded(action: suspend () -> Unit, aid: String) {
    TestGlobal.shitHalfwayLock.reset()
    TestGlobal.pageLoadedLock.reset()

    action()

    TestGlobal.shitHalfwayLock.let {
        it.pauseTestFromTest()
        assertScreenHTML("Halfway", aid = "$aid--halfway")
        it.resumeSutFromTest()
    }

    TestGlobal.pageLoadedLock.let {
        it.pauseTestFromTest()
        assertScreenHTML("Page loaded", aid = "$aid--pageLoaded")
        it.resumeSutFromTest()
    }
}

suspend fun seq.submitForm(
    aid: String,
    descr: String = "Describe me",
    action: (suspend () -> Unit)? = null,
    buttonKey: TestRef<ButtonKey>? = null,
    imposeTimestamp: Boolean = true,
    aopts: AssertScreenOpts? = null,
    useFormDoneLock: Boolean = true
) {
    val theShit = TestGlobal.currentTestShit
    sequence(
        action = {async{
            if (imposeTimestamp) {
                theShit.imposeNextRequestTimestamp_killme()
            }
            val theAction = action ?: {
                buttonClick(buttonKey ?: buttons.primary_testRef)
            }
            theAction()
        }},
        descr = descr,
        steps = listOf(
            PauseAssertResumeStep(TestGlobal.formTickingLock, "$aid--1"),
            ifOrNull(useFormDoneLock) {PauseAssertResumeStep(TestGlobal.formDoneLock, "$aid--2")})
            .filterNotNull(),
        aopts = aopts
    )
}

suspend fun seq.submitForm_pageLoaded(aid: String) {
    step(
        action = {
            submitForm(aid = aid,
                       useFormDoneLock = false)
        },
        lock = TestGlobal.pageLoadedLock,
        aid = "$aid--pageLoaded"
    )
}

suspend fun seq.formSubmissionAttempts(
    aid: String,
    descr: String = "Describe me",
    buttonKey: TestRef<ButtonKey>? = null,
    attempts: List<TestAttempt>,
    useFormDoneLockOnLastAttempt: Boolean = true
) {
    for (i in 0 until attempts.size)
        for (j in i+1 until attempts.size)
            if (attempts[i].subID == attempts[j].subID) bitch("Attempt subID duplication: ${attempts[i].subID}")

    for ((i, attempt) in attempts.withIndex()) {
        attempt.prepare()
        seq.submitForm(
            descr = /*"Attempt: " + */attempt.descr,
            aid = "$aid--${attempt.subID}",
            imposeTimestamp = i == attempts.lastIndex,
            buttonKey = buttonKey,
            aopts = attempt.aopts,
            useFormDoneLock = i < attempts.lastIndex || useFormDoneLockOnLastAttempt)
    }
}

suspend fun seq.formSubmissionAttempts_pageLoaded(
    aid: String,
    descr: String = "Describe me",
    buttonKey: TestRef<ButtonKey>? = null,
    attempts: List<TestAttempt>
) {
    step(
        action = {
            formSubmissionAttempts(
                descr = descr, aid = aid, buttonKey = buttonKey, attempts = attempts,
                useFormDoneLockOnLastAttempt = false)
        },
        lock = TestGlobal.pageLoadedLock,
        aid = "$aid--pageLoaded"
    )
}

suspend fun seq.button_modal(key: TestRef<ButtonKey>, aid: String) {
    step({buttonClick(key)}, TestGlobal.modalShownLock, aid)
}

suspend fun seq.editButton_modal(aid: String) {
    seq.button_modal(buttons.edit_testRef, aid)
}

suspend fun seq.acceptShit(aid: String) {
    seq.halfway_done({buttonClick(buttons.accept_testRef)}, aid)
}

suspend fun seq.acceptShit_errorModal(aid: String, errorStateDescr: String = "We are hosed") {
    halfway_modal_closed(action = {buttonClick(buttons.accept_testRef)},
                         modalAction = {
                             describeState(errorStateDescr)
                             tmodal.close()
                         },
                         aid = aid)
}

suspend fun seq.rejectShit(reason: String, aid: String) {
    seq.button_modal(buttons.reject_testRef, "$aid--1")
    inputSetValue(fields.rejectionReason_testRef, reason)
    step(action = {seq.submitForm(useFormDoneLock = false, aid = "$aid--2")},
         lock = TestGlobal.pageLoadedLock, aid = "$aid--3")
}

suspend fun seq.bid(price: Int, duration: Int, comment: String, aid: String) {
    button_modal(buttons.bid_testRef, "$aid--1")
    inputSetValue(fields.bidPriceOffer_testRef, price.toString())
    inputSetValue(fields.bidDurationOffer_testRef, duration.toString())
    inputSetValue(fields.bidComment_testRef, comment)
    submitForm("$aid--2")
}











