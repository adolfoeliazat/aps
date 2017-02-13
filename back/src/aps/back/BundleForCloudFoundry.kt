package aps.back

import aps.*
import into.kommon.*
import org.apache.tools.ant.Project
import org.apache.tools.ant.taskdefs.Jar
import org.apache.tools.ant.taskdefs.Manifest
import org.apache.tools.ant.taskdefs.Zip
import org.apache.tools.ant.types.FileSet
import org.apache.tools.ant.types.ZipFileSet
import java.io.File

class BundleForCloudFoundry {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Jar()-{o->
                o.project = Project()
                o.destFile = File("${const.file.APS_HOME}/back/built/apsback.jar")
                o.setDuplicate(Zip.Duplicate()-{o->
                    o.value = "add"
                })
                addFuckingJar(o, "$KOMMON_HOME/lib/kotlin/1.1-m03/kotlin-runtime.jar")
                addFuckingJar(o, "$KOMMON_HOME/lib/kotlin/1.1-m03/kotlin-reflect.jar")
                addFuckingJarsFrom(o, "${const.file.APS_HOME}/back/lib-gradle")
                addFuckingJarsFrom(o, "${const.file.APS_HOME}/back/lib")
                o.addFileset(FileSet()-{o->
                    o.dir = File("$KOMMON_HOME/jvm/out")
                    o.setIncludes("**/*")
                })
                o.addFileset(FileSet()-{o->
                    o.dir = File("${const.file.APS_HOME}/back/out")
                    o.setIncludes("**/*")
                })
                o.addConfiguredManifest(Manifest()-{o->
                    o.addConfiguredAttribute(Manifest.Attribute()-{o->
                        o.name = "Main-Class"
                        o.value = "aps.back.BackBoot"
                    })
                })
                // o.level = 0
                o.execute()
            }

            println("fuck you")
        }

        private fun addFuckingJarsFrom(o: Jar, dir: String) {
            File(dir).listFiles().forEach {
                val path = it.absolutePath
                if (path.endsWith(".jar"))
                    addFuckingJar(o, path)
            }
        }

        private fun addFuckingJar(o: Jar, file: String) {
            o.addZipfileset(ZipFileSet()-{o->
                o.src = File(file)
                o.setExcludes("**/*.SF, **/*.DSA, **/*.RSA")
            })
        }
    }
}


