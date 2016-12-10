@file:Suppress("UnsafeCastFromDynamic")

package aps

import aps.Color.*
import aps.front.*
import into.kommon.*
import org.w3c.files.File
import org.w3c.files.FileList

@Front class FileField(
    container: RequestMatumba,
    name: String,
    val title: String
): FormFieldFront(container, name) {
    val noise = DebugNoise("FileField", mute = false)

    val control = object:Control2(Attrs()) {
        val inputID = puid()
        var uploaded = false
        var file: File? = null

        override fun render(): ToReactElementable {
            return kdiv(className = "form-group", marginTop = 10){o->
                val theFile = file
                if (theFile == null) {
                    o- reactCreateElement("input", json(
                        "id" to inputID,
                        "type" to "file",
                        "style" to json("display" to "none"),
                        "onChange" to {
                            val files: FileList = byid0(inputID).asDynamic().files
                            file = files[0]
                            gloshit.file = file
                            noise.clog("Got file", file)
                            update()
                        }
                    ), listOf())
                    o- Button("upload", icon = "cloud-upload", title = t("TOTE", "Выбрать файл..."), onClick = {
                        byid(inputID).click()
                    })
                } else {
                    o- klabel {it-title}
                    if (uploaded) {
                        o- kdiv{o->
                            o- theFile.name
                            o- " (${fileSizeToApproxString(Globus.lang, theFile.size)})"
                        }
                    } else {
                        o- kdiv{o->
                            o- kspan(position = "relative", top = 3){o->
                                o- (t("TOTE", "Загружаю: ") + theFile.name + " (${fileSizeToApproxString(Globus.lang, theFile.size)})...")
                            }
                            o- kdiv(className = "progressTicker", float = "right", width = 14, height = 34, backgroundColor = BLUE_GRAY_600)
                        }
                    }
                }
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


