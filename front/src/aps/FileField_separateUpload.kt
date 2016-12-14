@file:Suppress("UnsafeCastFromDynamic")

package aps

import aps.Color.*
import aps.front.*
import into.kommon.*
import org.w3c.files.File
import org.w3c.files.FileList
import org.w3c.files.FileReader
import org.w3c.xhr.XMLHttpRequest

@Front class FileField_separateUpload(
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
                            uploaded = false
                            update()

//                            val reader = FileReader()
//                            xhr.upload.addEventListener("load", {
//                            }, false)
//                            reader.onload = {
//                                xhr.send(it.target.asDynamic().result)
//                            }

                            val xhr = XMLHttpRequest()
                            xhr.open("POST", "$backendURL/upload")
                            xhr.upload.onload = {
                                dwarnStriking("Uploaded shit")
                            }
                            xhr.send(file)
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
                            o- " (${formatFileSizeApprox(Globus.lang, theFile.size)})"
                        }
                    } else {
                        o- kdiv{o->
                            o- kspan(position = "relative", top = 3){o->
                                o- (t("TOTE", "Загружаю: ") + theFile.name + " (${formatFileSizeApprox(Globus.lang, theFile.size)})...")
                            }
                            o- kdiv(className = "progressTicker", float = "right", width = 14, height = 33.6, backgroundColor = BLUE_GRAY_600)
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


    override fun populateRemote(json: Json): Promise<Unit> = async {
        imf()
//        json[name] = value
    }
}



