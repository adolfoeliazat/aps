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

    sealed class Content {
        class FileToUpload(val file: File) : Content()
        class ExistingFile(val name: String, val size: Int) : Content()
        class NotProvided : Content()
        class TestFileOnServer(val name: String) : Content()
    }

    val noise = DebugNoise("FileField", mute = false)
    var content: Content = Content.NotProvided()
    var fileChanged = ResolvableShit<Unit>()

    val control = object:Control2(Attrs()) {
        val inputID = puid()

        override fun render(): ToReactElementable {
            val _content = content
            return kdiv(className = "form-group"/*, marginTop = 10*/){o->
                o- klabel {it-title}
                o- reactCreateElement("input", json(
                    "id" to inputID,
                    "type" to "file",
                    "style" to json("display" to "none"),
                    "onChange" to {
                        val files: FileList = byid0(inputID).asDynamic().files
                        val file = files[0]!!
                        gloshit.file = file
                        noise.clog("Got file", file)
                        content = Content.FileToUpload(file)
                        update()
                        fileChanged.resolve(Unit)
                    }
                ), listOf())
                when (_content) {
                    is Content.ExistingFile -> {
                        o- kdiv(Style(display = "flex", alignItems = "center")){o->
                            o- kspan{o->
                                o- (_content.name + " (${formatFileSizeApprox(Globus.lang, _content.size)})")
                            }
                            o- Button("upload", icon = fa.cloudUpload, title = t("TOTE", "Изменить..."), style = Style(marginLeft = "1em"), onClick = {
                                byid(inputID).click()
                            })
                        }
                    }
                    is Content.FileToUpload -> {
                        o- kdiv(Style(display = "flex", alignItems = "center")){o->
                            o- kspan{o->
                                o- (_content.file.name + " (${formatFileSizeApprox(Globus.lang, _content.file.size)})")
                            }
                            o- Button("upload", icon = fa.cloudUpload, title = t("TOTE", "Изменить..."), style = Style(marginLeft = "1em"), onClick = {
                                byid(inputID).click()
                            })
                        }
                    }
                    is Content.NotProvided -> {
                        o- Button("upload" + container.fieldInstanceKeySuffix, icon = fa.cloudUpload, title = t("TOTE", "Выбрать..."), onClick = {
                            byid(inputID).click()
                        })
                    }
                }
            }
        }

        override fun componentDidMount() {
            instances[instanceKey] = this@FileField
        }

        override fun componentWillUnmount() {
            instances.remove(instanceKey)
        }

        val instanceKey get() = key + container.fieldInstanceKeySuffix
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
        val _content = content
        val shit = ResolvableShit<Unit>()
        exhaustive/when (_content) {
            is FileField.Content.FileToUpload -> {
                val reader = FileReader()
                reader.onload = {
                    json[name] = json(
                        "provided" to true,
                        "fileName" to _content.file.name,
                        "base64" to run {
                            val dataURL: String = reader.result
                            dataURL.substring(dataURL.indexOf(",") + 1)
                        }
                    )
                    shit.resolve(Unit)
                }
                reader.readAsDataURL(_content.file)
            }
            is FileField.Content.NotProvided,
            is FileField.Content.ExistingFile  -> {
                json[name] = json(
                    "provided" to false
                )
                shit.resolve(Unit)
            }
            is FileField.Content.TestFileOnServer -> {
                json[name] = json(
                    "provided" to true,
                    "fileName" to _content.name,
                    "testFileOnServerName" to _content.name
                )
                shit.resolve(Unit)
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


