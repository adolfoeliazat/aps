@file:Suppress("UnsafeCastFromDynamic")

package aps

import aps.Color.*
import aps.front.*
import into.kommon.*
import org.w3c.files.File
import org.w3c.files.FileList
import org.w3c.files.FileReader
import org.w3c.files.get
import org.w3c.xhr.XMLHttpRequest
import kotlin.browser.window
import kotlin.js.Json
import kotlin.js.json

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
//    var fileChanged = ResolvableShit<Unit>()

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
                    "onChange" to {async{
                        val files: FileList = byid0(inputID).asDynamic().files
                        val file = files[0]!!
                        onGotFile(file)
                    }}
                ), listOf())
                when (_content) {
                    is Content.ExistingFile -> {
                        o- kdiv(Style(display = "flex", alignItems = "center")){o->
                            o- kspan{o->
                                o- (_content.name + " (${formatFileSizeApprox(Globus.lang, _content.size)})")
                            }
                            o- Button(icon = fa.cloudUpload, title = t("Change...", "Изменить..."), style = Style(marginLeft = "1em"), key = SubscriptButtonKey(fconst.key.button.upload.ref, container.fieldInstanceKeySuffix), onClick = {
                                byid(inputID).click()
                            })
                        }
                    }
                    is Content.FileToUpload -> {
                        o- kdiv(Style(display = "flex", alignItems = "center")){o->
                            o- kspan{o->
                                o- (_content.file.name + " (${formatFileSizeApprox(Globus.lang, _content.file.size)})")
                            }
                            o- Button(icon = fa.cloudUpload, title = t("Change...", "Изменить..."), style = Style(marginLeft = "1em"), key = SubscriptButtonKey(fconst.key.button.upload.ref, container.fieldInstanceKeySuffix), onClick = {
                                byid(inputID).click()
                            })
                        }
                    }
                    is Content.NotProvided -> {
                        o- kdiv{o->
                            o- Button(icon = fa.cloudUpload, title = t("Choose...", "Выбрать..."), key = fconst.key.button.upload.ref, onClick = {
                                byid(inputID).click()
                            })
                        }
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

    fun testUploadFileFast(fileName: String) {
        async {
            val res = send(TestGetFileUploadDataRequest()-{o->
                o.fileName.value = fileName
            })
            val file = File(base64ToUint8ArraySlices(res.base64), res.name)
            onGotFile(file)
        }
    }


    private suspend fun onGotFile(file: File) {
        noise.clog("Got file", file)
        gloshit.file = file
        content = Content.FileToUpload(file)
        control.update()
        TestGlobal.fileFieldChangedLock.sutPause()
    }

    override fun render() = control.toReactElement()

    override var error: String? = null

    // TODO:vgrechka Implement
    override var disabled: Boolean
        get() = false
        set(value) {}

    // TODO:vgrechka Implement
    override fun focus() {}

    override fun populateRemote(json: Json): Promisoid<Unit> {
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
                            // dlog("dataURL", dataURL.substring(0, 20))
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

//fun TestScenarioBuilder.fileFieldWaitTillShitChanged(key: String) {
//    acta("Waiting till file in `$key` is changed") {
//        val shit = ResolvableShit<Unit>()
//
//        val fileField = FileField.instance(key)
//        fileField.fileChanged.promise.then<Nothing>({
//            shit.resolve(Unit)
//        })
//
//        timeoutSet(1000) {
//            shit.reject(Exception("Timed out waiting for a fucking file to be changed"))
//        }
//
//        shit.promise
//    }
//}


suspend fun fileFieldChoose(fileName: String, aid: String, descr: String = "Describe me") {
    sequence(
        action = {
            if (testOpts().fastFileUpload) {
                if (FileField.instances.size != 1) bitch("I want exactly one FileField")
                val ff = FileField.instances.values.first()
                ff.testUploadFileFast(fileName)
            } else {
                buttonUserInitiatedClick(fconst.key.button.upload.testRef)
                typeIntoOpenFileDialog(const.test.filesRoot + fileName)
            }
        },
        steps = listOf(
            PauseAssertResumeStep(TestGlobal.fileFieldChangedLock, aid)
        ),
        descr = descr
    )
}

suspend fun typeIntoOpenFileDialog(text: String) {
    await(fuckingRemoteCall.robotTypeTextCRIntoWindowTitledOpen(text))
}


// http://stackoverflow.com/questions/16245767/creating-a-blob-from-a-base64-string-in-javascript
fun base64ToUint8ArraySlices(base64: String): Array<dynamic> {
    val sliceSize = 512

    val byteCharacters = js("atob")(base64)
    val byteArrays = js("[]")

    var offset = 0
    while (offset.asDynamic() < byteCharacters.length) {
        val slice = byteCharacters.slice(offset, offset + sliceSize)

        val byteNumbers = js("new Array(slice.length)")
        var i = 0
        while (i.asDynamic() < slice.length) {
            byteNumbers[i] = slice.charCodeAt(i)
            i++
        }

        val byteArray = js("new Uint8Array(byteNumbers)")

        byteArrays.push(byteArray)
        offset += sliceSize
    }

    return byteArrays
}



