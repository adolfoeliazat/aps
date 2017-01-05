/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.Color.*
import into.kommon.*
import kotlin.reflect.KProperty

object css {
    abstract class Group(val parent: Group?)

    private class Style(val style: String? = null, val hover: String? = null) {
        var name: String? = null

        operator fun getValue(thiz: Any, prop: KProperty<*>): String {
            if (name == null) {
                name = prop.name

                var group: Group? = thiz as? Group
                while (group != null) {
                    val groupName = group::class.simpleName
                    name = groupName + "-" + name
                    group = group.parent
                }

                style?.let {allShit += ".$name {$it}"}
                hover?.let {allShit += ".$name:hover {$it}"}
            }
            return name!!
        }
    }

    var allShit = ""

    object cunt : Group(null) {
        object header : Group(this) {
            val viewing by Style("""
                font-size: 18px;
                background-color: #eceff1;
                border-bottom: 1px solid #cfd8dc;
                position: relative;""")

            val editing by Style("""
                font-size: 18px;
                background-color: #eceff1;
                border-bottom: 1px solid #cfd8dc;
                position: relative;
                border-left: 4px solid $BLUE_GRAY_400;
                padding-left: 0.6rem;""")

            object leftIcon : Group(this) {
                val viewing by Style("""
                    color: #90a4ae;
                    margin-left: 3px;""")

                val editing by Style("""
                    color: #90a4ae;
                    margin-left: 0px;""")
            }

            object leftOverlayBottomLeftIcon : Group(this) {
                val viewing by Style("""
                    margin-left: 3px;
                    position: absolute;
                    left: 2px;
                    top: 9px;
                    color: #cfd8dc;
                    font-size: 60%;""")

                val editing by Style("""
                    margin-left: 0.6rem;
                    position: absolute;
                    left: 2px;
                    top: 9px;
                    color: #cfd8dc;
                    font-size: 60%;""")
            }

            val rightIcon by Style(
                style = """
                    color: #90a4ae;
                    margin-left: 3px;
                    position: absolute;
                    right: 3px;
                    top: 4px;
                    cursor: pointer;""",
                hover = """
                    color: #607d8b;""")
        }

        val bodyEditing by Style("""
            border-left: 4px solid $BLUE_GRAY_400;
            padding-left: 0.6rem;
            margin-bottom: 1rem;
            padding-top: 1rem;""")
    }

    object test : Group(null) {
        object popup : Group(this) {
            val pause by Style("""
                position: fixed;
                bottom: 0px;
                left: 0px;
                width: 40rem;
                min-height: 10rem;
                background-color: $LIME_100;
                border: 0.4rem solid $LIME_900;
                color: black;
                font-weight: bold;
                padding: 0.2rem;
                z-index: 100000;""")

            object assertion : Group(this) {
                val notHardened by Style("""
                    position: fixed;
                    width: 40rem;
                    min-height: 10rem;
                    background-color: $GRAY_300;
                    border: 0.4rem solid $GRAY_700;
                    color: black;
                    font-weight: bold;
                    padding: 0.2rem;
                    z-index: 100000;""")

                val correct by Style("""
                    position: fixed;
                    width: 40rem;
                    min-height: 10rem;
                    background-color: $GREEN_200;
                    border: 0.4rem solid $GREEN_700;
                    color: black;
                    font-weight: bold;
                    padding: 0.2rem;
                    z-index: 100000;""")

                val incorrect by Style("""
                    position: fixed;
                    width: 40rem;
                    min-height: 10rem;
                    background-color: $RED_200;
                    border: 0.4rem solid $RED_700;
                    color: black;
                    font-weight: bold;
                    padding: 0.2rem;
                    z-index: 100000;""")
            }

            val popupImageViewer by Style("""
                position: fixed;
                width: 90vw;
                height: 100px;
                left: 5vw;
                top: 2rem;
                background-color: pink;
                z-index: 1000000;
            """)
        }

        val assertionErrorPane by Style("""
        """)

        val cutLine by Style("""
            width: 100vw;
            height: 1px;
            position: absolute;
            left: 0px;
            background-color: #ff00ff;
            z-index: 100000;
        """)
    }

    val errorBanner by Style("""
        background-color: $RED_50;
        border-left: 3px solid $RED_300;
        font-size: 14px;
        margin-bottom: 15px;""")

    object diff : Group(null) {
        object expected : Group(this) {
            val title by Style("""
                background-color: $GREEN_100;
                font-weight: bold;""")

            val content by Style("""
                background-color: $GREEN_100;""")
        }

        object actual : Group(this) {
            val title by Style("""
                background-color: $RED_100;
                font-weight: bold;""")

            val content by Style("""
                background-color: $RED_100;""")
        }

        object same : Group(this) {
            val content by Style("""
                background-color: $WHITE;""")
        }
    }

    init {touchObjectGraph(this)}
}

class IconClass(val className: String) {
    override fun toString() = className
}

object fa {
    val file = IconClass("fa fa-file")
    val user = IconClass("fa fa-user")
    val pencil = IconClass("fa fa-pencil")
    val cog = IconClass("fa fa-cog")
    val cloudDownload = IconClass("fa fa-cloud-download")
    val search = IconClass("fa fa-search")
    val play = IconClass("fa fa-play")
    val cloudUpload = IconClass("fa fa-cloud-upload")
    val plus = IconClass("fa fa-plus")
    val edit = IconClass("fa fa-edit")
    val trash = IconClass("fa fa-trash")
    val bomb = IconClass("fa fa-bomb")
    val arrowUp = IconClass("fa fa-arrow-up")
    val arrowDown = IconClass("fa fa-arrow-down")
    val arrowLeft = IconClass("fa fa-arrow-left")
    val arrowRight = IconClass("fa fa-arrow-right")
}

fun loadCSS() {
    js("$")(global.document.head).append("<style id='css'>${apsCSS()}</style>")
}

fun apsCSS(): String {
    val zebraLight = WHITE
    val zebraDark = BLUE_GRAY_50

    var res = """
        body {overflow-x: hidden; padding-right: 0px !important;}

        button:disabled {cursor: default !important;}
        input:disabled {cursor: default !important;}
        textarea:disabled {cursor: default !important;}
        select:disabled {cursor: default !important;}

        .form-control:focus {border-color: #b0bec5; box-shadow: inset 0 1px 1px rgba(0,0,0,.075),0 0 8px rgba(176,190,197,.6);}

        .btn-primary {background-color: #78909c; border-color: #546e7a;}
        .btn-primary:hover {background-color: #546e7a; border-color: #37474f;}
        .btn-primary:focus {background-color: #455a64; border-color: #263238; outline-color: #b0bec5;}
        .btn-primary:focus:hover {background-color: #455a64; border-color: #263238;}
        .btn-primary:active {background-color: #455a64; border-color: #263238;}
        .btn-primary:active:focus {background-color: #455a64; border-color: #263238; outline-color: #b0bec5;}
        .btn-primary:active:hover {background-color: #455a64; border-color: #263238;}

        .btn-primary.disabled.focus,
        .btn-primary.disabled:focus,
        .btn-primary.disabled:hover,
        .btn-primary[disabled].focus,
        .btn-primary[disabled]:focus,
        .btn-primary[disabled]:hover,
        fieldset[disabled] .btn-primary.focus,
        fieldset[disabled] .btn-primary:focus,
        fieldset[disabled] .btn-primary:hover {
            background-color: #78909c;
            border-color: #546e7a;
        }

        .aniFadeOutDelayed {
            animation-name: aniFadeOutDelayed;
            animation-delay: 0.5s;
            animation-duration: 500ms;
            animation-iteration-count: 1;
            animation-fill-mode: forwards;
        }
        @keyframes aniFadeOutDelayed {
            0% {
                opacity: 1;
            }

            100% {
                opacity: 0;
            }
        }

        .aniFadeOutAfterSomeBlinking {
            animation-name: aniFadeOutAfterSomeBlinking;
            animation-delay: 0;
            animation-duration: 500ms;
            animation-iteration-count: 3;
            animation-fill-mode: forwards;
        }
        @keyframes aniFadeOutAfterSomeBlinking {
            0% {
                opacity: 1;
            }
            100% {
                opacity: 0;
            }
        }

        .zebra-0 {background: ${zebraLight};}
        .zebra-0 .borderTopColoredOnZebra {border-top-color: ${zebraDark};}
        .zebra-0 .borderRightColoredOnZebra {border-right-color: ${zebraDark};}
        .label1 {background-color: ${TEAL_50};}

        .zebra-1 {background: ${zebraDark};}
        .zebra-1 .borderTopColoredOnZebra {border-top-color: ${zebraLight};}
        .zebra-1 .borderRightColoredOnZebra {border-right-color: ${zebraLight};}
        .zebra-1 .label1 {background-color: ${TEAL_100};}

        .hover-color-BLUE_GRAY_800:hover {color: ${BLUE_GRAY_800};}
    """

    res += css.allShit

    return res
}











//    private class Entry0(val style: String? = null, val hover: String? = null) {
//        operator fun provideDelegate(thisGroup: css, prop: KProperty<*>): ReadOnlyProperty<css, String> {
//            val name = prop.name
//            val fullName = name
//            style?.let {allShit += ".$fullName {$it}"}
//            hover?.let {allShit += ".$fullName:hover {$it}"}
//
//            return object:ReadOnlyProperty<css, String> {
//                override fun getValue(thisRef: css, property: KProperty<*>) = fullName
//            }
//        }
//    }

