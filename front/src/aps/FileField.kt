@file:Suppress("UnsafeCastFromDynamic")

package aps

import aps.Color.*
import aps.front.*
import into.kommon.*
import org.w3c.files.File
import org.w3c.files.FileList
import org.w3c.files.FileReader
import org.w3c.xhr.XMLHttpRequest
import kotlin.browser.window

@Front class FileField(
    container: RequestMatumba,
    key: String,
    val title: String
): FormFieldFront(container, key) {
    companion object {
        val instances = mutableMapOf<String, FileField>()

        fun instance(key: String): FileField {
            return instances[key] ?: bitch("No FileField keyed `$key`")
        }
    }

    val noise = DebugNoise("FileField", mute = false)

    var fileChanged = ResolvableShit<Unit>()

    val control = object:Control2(Attrs()) {
        val inputID = puid()
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
                            fileChanged.resolve(Unit)
                        }
                    ), listOf())
                    o- Button("upload", icon = "cloud-upload", title = t("TOTE", "Выбрать файл..."), onClick = {
                        byid(inputID).click()
                    })
                } else {
                    o- klabel {it-title}
                    o- kdiv{o->
                        o- kspan(position = "relative", top = 3){o->
                            o- (theFile.name + " (${fileSizeToApproxString(Globus.lang, theFile.size)})")
                        }
                    }
                }
            }
        }

        override fun componentDidMount() {
            instances[key] = this@FileField
        }

        override fun componentWillUnmount() {
            instances.remove(key)
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
    acta("Typing into Open dialog: ${markdownItalicVerbatim(text)}") {
        fuckingRemoteCall.robotTypeTextCRIntoWindowTitledOpen(text)
    }
}

fun TestScenarioBuilder.fileFieldWaitTillShitChanged(key: String) {
    acta("Waiting till file in `$key` is changed") {
        val shit = ResolvableShit<Unit>()

        val fileField = FileField.instance(key)
        fileField.fileChanged.promise.then<Nothing>({
            shit.resolve(Unit)
        })

        timeoutSet(1000) {
            shit.reject(Exception("Timed out waiting for a fucking file to be changed"))
        }

        shit.promise
    }
}


