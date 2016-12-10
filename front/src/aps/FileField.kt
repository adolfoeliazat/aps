@file:Suppress("UnsafeCastFromDynamic")

package aps

import aps.front.*
import into.kommon.*

@Front class FileField(
    container: RequestMatumba,
    name: String,
    val title: String
): FormFieldFront(container, name) {
    val noise = DebugNoise("FileField", mute = false)

    val control = object:Control2(Attrs()) {
        val inputID = puid()

        override fun render(): ToReactElementable {
            return kdiv(className = "form-group", marginTop = 10){o->
                o- reactCreateElement("input", json(
                    "id" to inputID,
                    "type" to "file",
                    "style" to json("display" to "none"),
                    "onChange" to {
                        val files: Array<dynamic> = byid0(inputID).asDynamic().files
                        val file = files[0]
                        gloshit.file = files
                        noise.clog("Got file", file)
                    }
                ), listOf())
                o- Button("upload", icon = "cloud-upload", title = t("TOTE", "Выбрать файл..."), onClick = {
                    byid(inputID).click()
                })
            }
        }
    }

    override fun render() = control.toReactElement()

    override var error: String? = null


    override var disabled: Boolean
        get() = imf()
        set(value) {imf()}

    override fun focus() = imf()


    override fun populateRemote(json: Json) {
        imf()
//        json[name] = value
    }
}

fun TestScenarioBuilder.typeIntoOpenFileDialog(text: String) {
    acta("Typing into Open dialog: `${markdownItalicVerbatim(text)}`") {
        fuckingRemoteCall.robotTypeTextCRIntoWindowTitledOpen(text)
    }
}


