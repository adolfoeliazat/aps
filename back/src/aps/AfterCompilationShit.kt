/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps

import aps.back.*
import into.kommon.*
import java.io.File
import org.apache.tools.ant.taskdefs.*


class AfterCompilationShit {
    init {
        eprintln("Doing after-compilation shit")

        val versionSourceFile = File("$APS_HOME/back/src/aps/version.txt")
        val oldVersion = versionSourceFile.readText().toLong()
        val newVersion = oldVersion + 1
        versionSourceFile.writeText(newVersion.toString())
        File("$APS_HOME/back/out/aps/version.txt").writeText(newVersion.toString())

        val frontJSInPath = "$APS_HOME/front/out/front.js"
        val frontJSOutPath = "$APS_HOME/front/out/front-enhanced.js"
        var frontJS = File(frontJSInPath).readText()
        val versionIndex = frontJS.indexOfOrDie("____VERSION____")
        frontJS = frontJS.replaceRange(versionIndex, versionIndex + "____VERSION____".length, newVersion.toString())
        File(frontJSOutPath).writeText(frontJS)

        for (site in listOf("customer-ua", "writer-ua")) {
            // TODO:vgrechka @duplication cb0e7275-0ce9-4819-9d5d-fdea8a37dfda
            val todir = "$APS_HOME/front/out/static/$site"
            copyFileToDir(frontJSOutPath, todir)
            copyFileToDir("$APS_HOME/front/out/front.js.map", todir)
//            copyFileToDir("$KOMMON_HOME/js/out/into-kommon-js-enhanced.js", todir)
//            copyFileToDir("$KOMMON_HOME/js/out/into-kommon-js.js.map", todir)

//            fixSourceMap("$todir/into-kommon-js.js.map")
            fixSourceMap("$todir/front.js.map")
        }

        eprintln("COOL")
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


