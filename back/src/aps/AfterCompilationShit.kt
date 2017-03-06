/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps

import aps.back.*
import into.kommon.*
import org.apache.tools.ant.Project
import java.io.File
import org.apache.tools.ant.taskdefs.*


class AfterCompilationShit {
    val newVersion: Long

    init {
        eprintln("Doing after-compilation shit")

        val versionSourceFile = File("${const.file.APS_HOME}/back/src/aps/version.txt")
        val oldVersion = versionSourceFile.readText().toLong()
        newVersion = oldVersion + 1
        versionSourceFile.writeText(newVersion.toString())
        File("${const.file.APS_HOME}/back/out/aps/version.txt").writeText(newVersion.toString())

        jsBusiness()

//        run {
//            eprintln("Packaging aps-javaagent")
//            Jar()-{o->
//                o.project = Project() // Necessary dummy
//                o.destFile = File("$APS_HOME/javaagent/dist/aps-javaagent.jar")
//                o.setBasedir(File("$APS_HOME/javaagent/out"))
//                o.setIncludes("**/*")
//                o.setManifest(File("$APS_HOME/javaagent/src/META-INF/MANIFEST.MF"))
//                o.execute()
//            }
//        }

        eprintln("COOL")
    }

    fun jsBusiness() {
        val frontJSInPath = "${const.file.APS_HOME}/front/out/front.js"
        val frontJSOutPath = "${const.file.APS_HOME}/front/out/front-enhanced.js"

        val frontJSFile = File(frontJSInPath)
        if (!frontJSFile.exists()) {
            clog("Seems compilation was fucked up, so I bail out")
            return
        }

        var frontJS = frontJSFile.readText()
        val versionIndex = frontJS.indexOfOrDie("____VERSION____")
        frontJS = frontJS.replaceRange(versionIndex, versionIndex + "____VERSION____".length, newVersion.toString())
        File(frontJSOutPath).writeText(frontJS)

        for (site in listOf("customer-ua", "writer-ua")) {
            // TODO:vgrechka @duplication cb0e7275-0ce9-4819-9d5d-fdea8a37dfda
            val todir = "${const.file.APS_HOME}/front/out/static/$site"
            copyFileToDir(frontJSOutPath, todir)
            copyFileToDir("${const.file.APS_HOME}/front/out/front.js.map", todir)
//            copyFileToDir("$KOMMON_HOME/js/out/into-kommon-js-enhanced.js", todir)
//            copyFileToDir("$KOMMON_HOME/js/out/into-kommon-js.js.map", todir)

//            fixSourceMap("$todir/into-kommon-js.js.map")
            fixSourceMap("$todir/front.js.map")
        }
    }

    private fun fixSourceMap(path: String) {
        val content = File(path).readText()
        File(path).writeText(content.replace("file://E:/work", "http://localhost:3030"))
    }

    private fun copyFileToDir(file: String, todir: String) {
        Copy().run {
            setFile(File(file))
            setTodir(File(todir))
            setOverwrite(true)
            execute()
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            AfterCompilationShit()
        }
    }
}


