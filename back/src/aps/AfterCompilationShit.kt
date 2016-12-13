/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps

import aps.back.*
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.v2.DbxClientV2
import into.kommon.*
import org.apache.tools.ant.Project
import org.apache.tools.ant.taskdefs.Copy
import org.apache.tools.ant.taskdefs.Jar
import org.apache.tools.ant.taskdefs.Manifest
import org.apache.tools.ant.taskdefs.Zip
import org.apache.tools.ant.types.FileSet
import java.io.File
import kotlin.system.exitProcess
import com.dropbox.core.v2.users.FullAccount
import java.io.FileInputStream


class AfterCompilationShit {
    init {
        eprintln("Doing after-compilation shit")

        for (site in listOf("customer-ua", "writer-ua")) {
            // TODO:vgrechka @duplication cb0e7275-0ce9-4819-9d5d-fdea8a37dfda
            val todir = "$APS_HOME/front/out/static/$site"
            copyFileToDir("$APS_HOME/front/out/front-enhanced.js", todir)
            copyFileToDir("$APS_HOME/front/out/front.js.map", todir)
            copyFileToDir("$KOMMON_HOME/js/out/into-kommon-js-enhanced.js", todir)
            copyFileToDir("$KOMMON_HOME/js/out/into-kommon-js.js.map", todir)
        }

        val versionSourceFile = File("$APS_HOME/back/src/aps/version.txt")
        val oldVersion = versionSourceFile.readText().toLong()
        val newVersion = oldVersion + 1
        versionSourceFile.writeText(newVersion.toString())
        File("$APS_HOME/back/out/aps/version.txt").writeText(newVersion.toString())

        val slimJarName = "apsback-slim.jar"
        val slimJar = "$APS_TEMP/$slimJarName"
        eprintln("Packing slim jar")
        Jar()-{o->
            o.project = Project()
            o.destFile = File(slimJar)
            o.setDuplicate(Zip.Duplicate()-{o->
                o.value = "add"
            })
            o.addFileset(FileSet()-{o->
                o.dir = File("$KOMMON_HOME/jvm/out")
                o.setIncludes("**/*")
            })
            o.addFileset(FileSet()-{o->
                o.dir = File("$APS_HOME/back/out")
                o.setIncludes("**/*")
            })
            // o.level = 0
            o.execute()
        }

        run {
            eprintln("Connecting to Dropbox")
            val config = DbxRequestConfig("apsback")
            val client = DbxClientV2(config, System.getenv("APS_DROPBOX_TOKEN"))
            val account = client.users().currentAccount
            eprintln("Dropbox account: " + account.name.displayName)
            eprintln("Deleting current shit from Dropbox")
            client.files().delete("/$slimJarName")
            eprintln("Uploading new shit to Dropbox")
            FileInputStream(slimJar).use {
                client.files().uploadBuilder("/$slimJarName").uploadAndFinish(it)
            }
        }

        eprintln("COOL")
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


