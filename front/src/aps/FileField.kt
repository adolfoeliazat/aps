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
    val title: String,
    val shouldBeProvided: Boolean = true
): FormFieldFront(container, key) {
    companion object {
        val instances = mutableMapOf<String, FileField>()

        fun instance(key: String): FileField {
            return instances[key] ?: bitch("No FileField keyed `$key`")
        }
    }

    class TestFileOnServer(val name: String, val path: String)
    class FileMeta(val name: String, val size: Int)

    val noise = DebugNoise("FileField", mute = false)

    var file: File? = null
    var existingFile: FileMeta? = null
    var testFileOnServer: TestFileOnServer? = null
    var fileChanged = ResolvableShit<Unit>()

    val control = object:Control2(Attrs()) {
        val inputID = puid()

        override fun render(): ToReactElementable {
            return kdiv(className = "form-group", marginTop = 10){o->
                val _file = file; val _existingFile = existingFile
                when {
                    _existingFile != null -> {
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
                        o- klabel {it-title}
                        o- kdiv(Style(display = "flex", alignItems = "center")){o->
                            o- kspan{o->
                                o- (_existingFile.name + " (${formatFileSizeApprox(Globus.lang, _existingFile.size)})")
                            }
                            o- Button("upload", icon = "cloud-upload", title = t("TOTE", "Изменить..."), style = Style(marginLeft = "1em"), onClick = {
                                byid(inputID).click()
                            })
                        }
                    }
                    _file == null -> {
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
                    }
                    else -> {
                        o- klabel {it-title}
                        o- kdiv{o->
                            o- kspan(position = "relative", top = 3){o->
                                o- (_file.name + " (${formatFileSizeApprox(Globus.lang, _file.size)})")
                            }
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

    // TODO:vgrechka Implement
    override var disabled: Boolean
        get() = false
        set(value) {}

    // TODO:vgrechka Implement
    override fun focus() {}

    override fun populateRemote(json: Json): Promise<Unit> {
        val _testFileOnServer = testFileOnServer; val _file = file

        val shit = ResolvableShit<Unit>()
        if (_testFileOnServer != null) {
            json[name] = json(
                "provided" to true,
                "fileName" to _testFileOnServer.name,
                "testFileOnServerPath" to _testFileOnServer.path
            )
            shit.resolve(Unit)
        } else {
            if (_file == null) {
                json[name] = json(
                    "provided" to false
                )
                shit.resolve(Unit)
            } else {
                val reader = FileReader()
                reader.onload = {
                    json[name] = json(
                        "provided" to true,
                        "fileName" to _file.name,
                        "base64" to run {
                            val dataURL: String = reader.result
                            dataURL.substring(dataURL.indexOf(",") + 1)
                        }
                    )
                    shit.resolve(Unit)
                }
                reader.readAsDataURL(_file)
            }
        }

        return shit.promise
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


