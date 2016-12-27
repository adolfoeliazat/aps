/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import aps.Color.*
import into.kommon.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

object css {

    private class Entry(val style: String? = null, val hover: String? = null) {
        operator fun provideDelegate(thisGroup: css, prop: KProperty<*>): ReadOnlyProperty<css, String> {
            val name = prop.name
            val fullName = name
            style?.let {allShit += ".$fullName {$it}"}
            hover?.let {allShit += ".$fullName:hover {$it}"}

            return object:ReadOnlyProperty<css, String> {
                override fun getValue(thisRef: css, property: KProperty<*>) = fullName
            }
        }
    }

    var allShit = ""

    val cuntHeader by Entry("""
        font-size: 18px;
        background-color: #eceff1;
        border-bottom: 1px solid #cfd8dc;
        position: relative;
    """)

    val cuntHeaderEditing by Entry("""
        font-size: 18px;
        background-color: #eceff1;
        border-bottom: 1px solid #cfd8dc;
        position: relative;
        border-left: 4px solid ${Color.BLUE_GRAY_400};
        padding-left: 0.6rem;
    """)

    val cuntHeaderLeftIcon by Entry("""
        color: #90a4ae;
        margin-left: 3px;
    """)

    val cuntHeaderLeftIconEditing by Entry("""
        color: #90a4ae;
        margin-left: 0px;
    """)

    val cuntHeaderLeftOverlayBottomLeftIcon by Entry("""
        margin-left: 3px;
        position: absolute;
        left: 2px;
        top: 9px;
        color: #cfd8dc;
        font-size: 60%;
    """)

    val cuntHeaderLeftOverlayBottomLeftIconEditing by Entry("""
        margin-left: 0.6rem;
        position: absolute;
        left: 2px;
        top: 9px;
        color: #cfd8dc;
        font-size: 60%;
    """)

    val cuntHeaderRightIcon by Entry(
        style = """
            color: #90a4ae;
            margin-left: 3px;
            position: absolute;
            right: 3px;
            top: 4px;
            cursor: pointer;
        """,
        hover = """
            color: #607d8b;
        """
    )

    val cuntBodyEditing by Entry("""
        border-left: 4px solid ${Color.BLUE_GRAY_400};
        padding-left: 0.6rem;
        margin-bottom: 1rem;
        padding-top: 1rem;
    """)

    val testScenarioPauseBanner by Entry("""
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
        z-index: 100000;
    """)

    val testScenarioAssertionNotHardenedBanner by Entry("""
        position: fixed;
        width: 40rem;
        min-height: 10rem;
        background-color: $GRAY_300;
        border: 0.4rem solid $GRAY_700;
        color: black;
        font-weight: bold;
        padding: 0.2rem;
        z-index: 100000;
    """)

    val testScenarioAssertionCorrectBanner by Entry("""
        position: fixed;
        width: 40rem;
        min-height: 10rem;
        background-color: $GREEN_200;
        border: 0.4rem solid $GREEN_700;
        color: black;
        font-weight: bold;
        padding: 0.2rem;
        z-index: 100000;
    """)

    val testScenarioAssertionIncorrectBanner by Entry("""
        position: fixed;
        width: 40rem;
        min-height: 10rem;
        background-color: $RED_200;
        border: 0.4rem solid $RED_700;
        color: black;
        font-weight: bold;
        padding: 0.2rem;
        z-index: 100000;
    """)

    val errorBanner by Entry("""
        background-color: $RED_50;
        border-left: 3px solid $RED_300;
        font-size: 14px;
        margin-bottom: 15px;
    """)

    val testAssertionErrorPane by Entry("""
    """)

    abstract class Group2(val parent: Group2? = null) {
        init {
            dwarnStriking("Group2 init")
        }
    }

    private class Entry2(val style: String? = null, val hover: String? = null) {
        operator fun provideDelegate(thisGroup: Group2, prop: KProperty<*>): ReadOnlyProperty<Group2, String> {
            var name = prop.name
            var group: Group2? = thisGroup
            while (group != null) {
                val groupName = group::class.simpleName
                dwarnStriking("groupName", groupName)
                name = groupName + "-" + name
                group = group.parent
            }

            style?.let {allShit += ".$name {$it}"}
            hover?.let {allShit += ".$name:hover {$it}"}

            return object:ReadOnlyProperty<Group2, String> {
                override fun getValue(thisRef: Group2, property: KProperty<*>) = name
            }
        }
    }

    object diff : Group2() {
        object expected : Group2(this) {
            val title by Entry2("""
                background-color: lime;
                font-weight: bold;
                font-style: italic;
            """)
        }

        object actual : Group2(this) {
            val title by Entry2("""
                background-color: pink;
                font-weight: bold;
                font-style: italic;
            """)
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

