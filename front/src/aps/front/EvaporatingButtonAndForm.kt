@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import kotlin.js.json

interface EvaporatingButtonAndFormHost {
    var showEmptyLabel: Boolean
    var cancelForm: () -> Unit
    val headerControlsDisabled: Boolean
    var headerControlsVisible: Boolean
    var headerControlsClass: String
    var headerMode: HeaderMode
    fun updateShit()
}

interface IButtonAndForm {
    fun renderButton(): ReactElement
    fun renderForm(): ReactElement?
}

class EvaporatingButtonAndForm<Req : RequestMatumba, Res>(
    private val host: EvaporatingButtonAndFormHost,
    val key: String,
    val level: Button.Level,
    val icon: IconClass,
    val formSpec: FormSpec<Req, Res>,
    val onSuccessa: suspend (Res) -> Unit
) : IButtonAndForm {
    private var form: FormMatumba<*, *>? = null
    private var formClass = ""

    override fun renderButton() = Button(
        key = key,
        level = level,
        icon = icon,
        disabled = host.headerControlsDisabled,
        onClicka = {asu{
            host.showEmptyLabel = false
            setHeaderControlsDisappearing()
            // formClass = "aniFadeIn"

            host.cancelForm = {
                setHeaderControlsAppearing()
                form = null
                host.headerMode = HeaderMode.BROWSING
                host.updateShit()
            }

            form = FormMatumba(formSpec.copy(
                onCancel = {host.cancelForm()},
                onSuccessa = {res->
                    asu {onSuccessa(res)}
                }
            ))

            host.headerMode = HeaderMode.CREATING
            host.updateShit()
            await(effects).fadeIn(form!!.elementID)
        }}
    ).toReactElement()

    override fun renderForm(): ReactElement? = form?.let {
        Shitus.diva(json("className" to formClass, "style" to json("marginBottom" to 15)), it.toReactElement())
    }

    fun setHeaderControlsDisappearing() {
        host.headerControlsVisible = false
        host.headerControlsClass = ""
    }

    fun setHeaderControlsAppearing() {
        host.headerControlsVisible = true
        host.headerControlsClass = "aniFadeIn"
        timeoutSet(500) {
            host.headerControlsClass = ""
            host.updateShit()
        }
    }
}

suspend fun sequence_openPlusForm(aid: String) = sequence(
    action = {
        buttonClick(fconst.key.button.plus_testRef)
    },
    descr = "Opened plus form",
    steps = listOf(
        PauseAssertResume(TestGlobal.fadeHalfwayLock, "$aid--1"),
        PauseAssertResume(TestGlobal.fadeDoneLock, "$aid--2")
    )
)






