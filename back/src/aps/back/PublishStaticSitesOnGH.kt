package aps.back

import aps.*
import org.apache.tools.ant.Project
import org.apache.tools.ant.taskdefs.*
import org.apache.tools.ant.types.FileSet
import java.io.File
import java.util.*

object PublishStaticSitesOnGH {
    lateinit var cwd: String

    @JvmStatic
    fun main(args: Array<String>) {
        val root = "${const.file.APS_TEMP}/pizda"

        Delete()-{o->
            o.setDir(File(root))
            o.execute()
        }
        Mkdir()-{o->
            o.dir = File(root)
            o.execute()
        }

        cwd = root
        exec("git", "clone",
             "--branch", "gh-pages",
             "--depth", "1",
             "git@github.com:staticshit/apsua.git")

        cwd = "$root/apsua"
        exec("git", "config",
             "user.name", "\"Pablo Huiablo\"")
        exec("git", "config",
             "user.email", "\"pablo@huiablo.me\"")

        Delete()-{o->
            o.project = Project()
            o.setDir(File(root))
            o.setIncludes("**/*")
            o.setExcludes(".git/**/*")
            o.execute()
        }

        cwd = "${const.file.APS_HOME}/front"
        exec("node", "run.js", "MakeStaticSites", "--mode=prod", "--out=$root/fucking-sites")

        Copy()-{o->
            o.project = Project()
            o.setTodir(File("$root/apsua"))
            o.addFileset(FileSet()-{o->
                o.dir = File("$root/fucking-sites/customer-ua")
                o.setIncludes("**/*")
            })
            o.execute()
        }

        cwd = "$root/apsua"
        exec("git", "add", "-A")
        exec("git", "commit",
             "-am", "\"Bunch of shit\"")
        exec("git", "push")

        eprintln("\nFUCK YEAH")
    }

    fun exec(vararg pieces: String) {
        val pb = ProcessBuilder()
        pb.command().addAll(pieces)
        pb.inheritIO()
        pb.environment()-{o->
            o["HOME"] = ""
            o["USERPROFILE"] = ""
            o["GIT_SSH_COMMAND"] = """ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -i "e:\fpebb\ssh\staticshit-id_rsa""""
        }
        pb.directory(File(cwd))
        val proc = pb.start()
        val exitCode = proc.waitFor()
        if (exitCode != 0) bitch("Shitty exit code: $exitCode")
    }
}

