package aps.front

import aps.*

fun dumpShames() {
    for ((shame, ctrl) in TestGlobal.shameToControl) {
        clog("Shame: $shame")
        // clog(ctrl)
    }
}

fun dumpControls() {
    for (key in Input.instances.keys) clog("Input: $key")
    for (key in Button.instances.keys) clog("Button: $key")
    for (key in Checkbox.instances.keys) clog("Checkbox: $key")
    for (key in DateTimePicker.instances.keys) clog("DateTimePicker: $key")
    for (key in FileField.instances.keys) clog("FileField: $key")
    for (key in Select.instances.keys) clog("Select: $key")
    for (key in kic.instances.keys) clog("kic: $key")
}

