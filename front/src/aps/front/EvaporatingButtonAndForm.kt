@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*

interface EvaporatingButtonAndFormHost {
    var showEmptyLabel: Boolean
    var cancelForm: () -> Unit
    val headerControlsDisabled: Boolean
    var headerControlsVisible: Boolean
    var headerControlsClass: String
    fun updateShit()
}

interface IButtonAndForm {
    fun renderButton(): ReactElement
    fun renderForm(): ReactElement?
}

class EvaporatingButtonAndForm<Req : RequestMatumba, Res>(
    private val host: EvaporatingButtonAndFormHost,
    val name: String,
    val level: Button.Level,
    val icon: String,
    val formSpec: FormSpec<Req, Res>,
    val onSuccessa: (Res) -> Promise<Unit>
) : IButtonAndForm {
    private var form: ReactElement? = null
    private var formClass = ""

//    override fun renderButton(): ReactElement = Shitus.button(json(
//        "tamyShamy" to name,
//        "style" to json("marginLeft" to 0),
//        "level" to level,
//        "icon" to icon,
//        "disabled" to host.headerControlsDisabled,
//        "onClick" to {
//            host.showEmptyLabel = false
//            setHeaderControlsDisappearing()
//            formClass = "aniFadeIn"
//
//            host.cancelForm = {
//                setHeaderControlsAppearing()
//                form = null
//                host.updateShit()
//            }
//
//            form = FormMatumba(formSpec.copy(
//                onCancel = {host.cancelForm()},
//                onSuccessa = {res ->
//                    async {
//                        await(onSuccessa(res))
//                    }
//                }
//            )).toReactElement()
//
//            host.updateShit()
//        }
//    ))

    override fun renderButton() = Button(
        key = name,
        level = level,
        icon = icon,
        disabled = host.headerControlsDisabled,
        onClicka = {async{
            host.showEmptyLabel = false
            setHeaderControlsDisappearing()
            formClass = "aniFadeIn"

            host.cancelForm = {
                setHeaderControlsAppearing()
                form = null
                host.updateShit()
            }

            form = FormMatumba(formSpec.copy(
                onCancel = {host.cancelForm()},
                onSuccessa = {res->
                    async {
                        await(onSuccessa(res))
                    }
                }
            )).toReactElement()

            host.updateShit()
        }}
    ).toReactElement()

    override fun renderForm(): ReactElement? = form?.let {
        Shitus.diva(json("className" to formClass, "style" to json("marginBottom" to 15)), it)
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








