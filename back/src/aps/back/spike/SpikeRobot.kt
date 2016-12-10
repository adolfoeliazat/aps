package aps.back.spike

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.User32Util
import into.kommon.*
import java.awt.Event
import java.awt.Robot
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent

fun main(args: Array<String>) {
//    shit1()
//    shit2()
    shit3()
}

private fun shit1() {
    val robot = Robot()
    robot.keyPress(KeyEvent.VK_WINDOWS)
    robot.keyPress(KeyEvent.VK_R)
    robot.keyRelease(KeyEvent.VK_R)
    robot.keyRelease(KeyEvent.VK_WINDOWS)
}

private fun shit2() {
    val hwnd = User32.INSTANCE.GetForegroundWindow()
    println("hwnd: $hwnd")
    val titleBuf = CharArray(256)
    val len = User32.INSTANCE.GetWindowText(hwnd, titleBuf, titleBuf.size)
    val title = String(titleBuf, 0, len)
    println("title: $title")
}

private fun shit3() {
    val hwnd = User32.INSTANCE.FindWindow(null, "APS UA - Google Chrome") ?: bitch("No necessary Chrome window")
    println("hwnd: $hwnd")
    User32.INSTANCE.SetForegroundWindow(hwnd) || bitch("Cannot bring Chrome to foreground")
    val robot = Robot()
    robot.mouseMove(18, 216)
    robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
    robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
}















