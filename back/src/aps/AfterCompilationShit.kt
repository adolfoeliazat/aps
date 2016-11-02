/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps

import into.kommon.*
import org.apache.tools.ant.taskdefs.Copy
import java.io.File
import kotlin.system.exitProcess

class AfterCompilationShit {
    init {
        print("Doing after-compilation shit...")

        for (site in listOf("customer-ua", "writer-ua")) {
            // TODO:vgrechka @duplication cb0e7275-0ce9-4819-9d5d-fdea8a37dfda
            val todir = "$APS_HOME/front/out/static/$site"
            copyFileToDir("$APS_HOME/front/out/front-enhanced.js", todir)
            copyFileToDir("$APS_HOME/front/out/front.js.map", todir)
            copyFileToDir("$KOMMON_HOME/js/out/into-kommon-js-enhanced.js", todir)
            copyFileToDir("$KOMMON_HOME/js/out/into-kommon-js.js.map", todir)
        }

        println("COOL")
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


