package aps.back

import aps.*
import aps.back.BackGlobus.slimJarName
import into.kommon.*
import org.apache.tools.ant.Project
import org.apache.tools.ant.taskdefs.*
import org.apache.tools.ant.types.FileSet
import java.io.File
import java.io.FileInputStream
import java.net.URL

object PushSlimShitToCF {
    @JvmStatic
    fun main(args: Array<String>) {
        val slimJar = "$APS_TEMP/$slimJarName"
        eprintln("Packing slim jar")

        Delete()-{o->
            o.setDir(File("$APS_TEMP/slim-override"))
            o.execute()
        }
        Mkdir()-{o->
            o.dir = File("$APS_TEMP/slim-override/aps")
            o.execute()
        }
        val normalVersion = File("$APS_HOME/back/out/aps/version.txt").readText()
        File("$APS_TEMP/slim-override/aps/version.txt").writeText(normalVersion + ".slim")

        Jar()-{o->
            o.project = Project()
            o.destFile = File(slimJar)
            // o.level = 0
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
                o.setExcludes("aps/version.txt")
            })
            o.addFileset(FileSet()-{o->
                o.dir = File("$APS_TEMP/slim-override")
                o.setIncludes("**/*")
            })
            o.execute()
        }

        run {
            eprintln("Deleting current shit from Dropbox")
            dropboxClient.files().delete("/$slimJarName")
            eprintln("Uploading new shit to Dropbox")
            FileInputStream(slimJar).use {
                dropboxClient.files().uploadBuilder("/$slimJarName").uploadAndFinish(it)
            }
        }

        run {
            eprintln("Sending 'kill yourself' to the service")
            val response = URL("http://$APS_CLOUD_BACK_HOST/kill").readText()
            eprintln("Shit responded: " + response)
        }

        eprintln("ЗАЕБИСЬ")
    }
}


