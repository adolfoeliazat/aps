package aps.back

import aps.*
import java.awt.MouseInfo
import java.awt.Point
import java.awt.Robot

private var origLocation by volatileNotNull<Point>()

fun serveMoveMouseAwayFromPageRequest(req: MoveMouseAwayFromPageRequest) {
    origLocation = MouseInfo.getPointerInfo().location
    val robot = Robot()
    robot.mouseMove(0, 0)
}

fun serveReturnMouseWhereItWasRequest(req: ReturnMouseWhereItWasRequest) {
    val robot = Robot()
    robot.mouseMove(origLocation.x, origLocation.y)
}

