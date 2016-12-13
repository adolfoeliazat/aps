package aps.back

import aps.*
import aps.back.BackGlobus.slimJarName
import sun.misc.Launcher
import java.io.File
import java.io.FileOutputStream

object BackBoot {
    @JvmStatic
    fun main(args: Array<String>) {
        if (System.getenv("APS_BOOT_FROM_DROPBOX") != "true") {
            val clazz = Class.forName("aps.back.BackKt")
            clazz.getDeclaredMethod("reallyBoot").invoke(null)
            return
        }

        val slimJar = "$TMPDIR/$slimJarName"
        eprintln("Downloading shit from Dropbox to $slimJar")
        FileOutputStream(slimJar).use {
            val downloader = dropboxClient.files().download("/$slimJarName")
            downloader.download(it)
        }

        System.setProperty("java.class.path", slimJar + File.pathSeparator + System.getProperty("java.class.path"))
        val loader = Launcher().classLoader
        println("current loader: " + BackBoot.javaClass.classLoader)
        val clazz = Class.forName("aps.back.BackKt", true, loader)
        clazz.getDeclaredMethod("reallyBoot").invoke(null)
    }
}

