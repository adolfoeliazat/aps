/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import aps.Color.*

object css {
    class Entry(val name: String, val style: String) {
        init {
            entries += this
        }
        override fun toString() = name
    }

    val entries = mutableListOf<Entry>()

    val cuntHeader = Entry("cunt-header", """
        font-size: 18px;
        background-color: #eceff1;
        border-bottom: 1px solid #cfd8dc;
        position: relative;
    """)
    val cuntHeaderEditing = Entry("cunt-header-editing", """
        font-size: 18px;
        background-color: #eceff1;
        border-bottom: 1px solid #cfd8dc;
        position: relative;
        border-left: 4px solid ${Color.BLUE_GRAY_400};
        padding-left: 0.6rem;
    """)
    val cuntHeaderLeftIcon = Entry("cunt-header-left-icon", """
        color: #90a4ae;
        margin-left: 3px;
    """)
    val cuntHeaderLeftIconEditing = Entry("cunt-header-left-icon-editing", """
        color: #90a4ae;
        margin-left: 0px;
    """)
    val cuntHeaderLeftOverlayBottomLeftIcon = Entry("cunt-header-left-overlay-bottom-left-icon", """
        margin-left: 3px;
        position: absolute;
        left: 2px;
        top: 9px;
        color: #cfd8dc;
        font-size: 60%;
    """)
    val cuntHeaderLeftOverlayBottomLeftIconEditing = Entry("cunt-header-left-overlay-bottom-left-icon-editing", """
        margin-left: 0.6rem;
        position: absolute;
        left: 2px;
        top: 9px;
        color: #cfd8dc;
        font-size: 60%;
    """)
    val cuntHeaderRightIcon = Entry("cunt-header-right-icon", """
        color: #90a4ae;
        margin-left: 3px;
        position: absolute;
        right: 3px;
        top: 4px;
        cursor: pointer;
    """)
    val cuntHeaderRightIcon_hover = Entry("$cuntHeaderRightIcon:hover", """
        color: #607d8b;
    """)
    val cuntBodyEditing = Entry("cunt-body-editing", """
        border-left: 4px solid ${Color.BLUE_GRAY_400};
        padding-left: 0.6rem;
        margin-bottom: 1rem;
        padding-top: 1rem;
    """)

    val testScenarioPauseBanner = Entry("testScenarioPauseBanner", """
        position: fixed;
        bottom: 0px;
        left: 0px;
        width: 40rem;
        min-height: 10rem;
        background-color: #f0f4c3;
        border: 0.4rem solid #827717;
        color: black;
        font-weight: bold;
        padding: 0.2rem;
    """)
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
}

fun jsFacing_apsCSS(): String {
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

    for (entry in css.entries) {
        res += ".${entry.name} {${entry.style}}"
    }

    return res
}

