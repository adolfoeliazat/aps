/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import aps.Color.*
import into.kommon.*
import kotlin.properties.Delegates.notNull
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

typealias CSSClassName = String
typealias EnumStyleClassNameGetter<E> = (E) -> String

object css {
    abstract class Group(val parent: Group?) {
        open val groupName get() = this::class.simpleName
    }

    fun styleFQN(thisRef: Any, prop: KProperty<*>): String {
        var name = prop.name

        var group = thisRef as? Group
        while (group != null) {
            val groupName = group!!.groupName
            name = groupName + "-" + name
            group = group.parent
        }

        return name
    }

    fun appendStyleToAllShit(name: CSSClassName, def: Style) {
        def.style?.let {allShit += ".$name {$it}"}
        def.link?.let {allShit += ".$name:link {$it}"}
        def.visited?.let {allShit += ".$name:visited {$it}"}
        def.hover?.let {allShit += ".$name:hover {$it}"}
        def.active?.let {allShit += ".$name:active {$it}"}
        def.firstChild?.let {allShit += ".$name:first-child {$it}"}
        def.notFirstChild?.let {allShit += ".$name:nth-child(1n+2) {$it}"}
    }

    class Style(
        val style: String? = null,
        val link: String? = null,
        val visited: String? = null,
        val hover: String? = null,
        val active: String? = null,
        val firstChild: String? = null,
        val notFirstChild: String? = null
    )
        : ReadOnlyProperty<Any, CSSClassName>
    {
        var propValue: CSSClassName? = null

        override fun getValue(thisRef: Any, property: KProperty<*>): String {
            if (propValue == null) {
                propValue = styleFQN(thisRef, property)
                appendStyleToAllShit(propValue!!, this)
            }
            return propValue!!
        }
    }

    class EnumStyle<E : Enum<E>>(val enumValues: Array<E>, val enumValueToStyle: (E) -> Style)
        : ReadOnlyProperty<Any, EnumStyleClassNameGetter<E>>
    {
        var propValue: EnumStyleClassNameGetter<E>? = null

        override fun getValue(thisRef: Any, property: KProperty<*>): EnumStyleClassNameGetter<E> {
            if (propValue == null) {
                val namePrefix = styleFQN(thisRef, property) + "_"
                for (enumValue in enumValues) {
                    appendStyleToAllShit(namePrefix + enumValue.name, enumValueToStyle(enumValue))
                }
                propValue = {enumValue: E ->
                    namePrefix + enumValue.name
                }
            }
            return propValue!!
        }
    }

    private class KeyFrames(val spec: String) {
        var name: String? = null

        operator fun getValue(thiz: Any, prop: KProperty<*>): String {
            if (name == null) {
                name = prop.name

                var group: Group? = thiz as? Group
                while (group != null) {
                    val groupName = group!!::class.simpleName
                    name = groupName + "-" + name
                    group = group.parent
                }

                allShit += "@keyframes $name {$spec}"
            }
            return name!!
        }
    }

    class WaitingBannerStyles(override val groupName: String, parent: Group?, val containerStyle: String = "") : Group(parent) {
        val container by Style("$dollyLikeContainerBase; background-color: $LIGHT_GREEN_100; $containerStyle")
        val message by Style("flex-grow: 1;")
        val icon by Style("margin-right: 0.5em;")
    }


    var allShit = ""

    val progressTicker = "progressTicker"

    val formFieldLabelOptionalHint by Style("font-weight: normal; font-style: italic;")

    object shebang : Group(null) {
        val paddingRightScrollbarWidthImportant by Style("""
            padding-right: ${fconst.scrollbarWidth}px !important;
        """)

        val modalPane by Style("")
    }

    object profile : Group(null) {
        object writerProfilePendingBanner : Group(this) {
            val container by Style("$dollyLikeContainerBase; background-color: $BLUE_GRAY_50; margin-bottom: 1rem;")
            val message by Style("flex-grow: 1;")
            val icon by Style("margin-right: 0.5em;")
        }
        val writerProfileApprovalPendingBanner by named {WaitingBannerStyles(it, this, containerStyle = "margin-bottom: 1rem;")}
    }

    object dashboard : Group(null) {
        val sectionTitle by Style("background: ${Color.BLUE_GRAY_50}; padding: 0.15rem 0.5rem; margin-bottom: 0.5rem; font-weight: bold;")
        val sectionItem by Style(style = "margin-top: 0.25rem;",
                                 firstChild = "margin-top: 0;")
        val sectionItemIcon by Style("color: ${Color.BLUE_GRAY_600}; margin-right: 0.3em;")
        val sectionItemLink by Style("color: ${Color.BLACK_BOOT}")

        val workItem by Style(style = "position: relative; display: flex; margin-top: 0.25rem;",
                              firstChild = "margin-top: 0;")
        val workItemDots by Style("position: absolute; left: 0; top: 0; overflow: hidden; width: 100%;")
        val workItemLinkWideContainer by Style("flex-grow: 1; position: relative;")
        val workItemLinkNarrowContainer by Style("background-color: white; padding-right: 0.5rem;")
        val workItemLink by Style("position: relative; color: ${Color.BLACK_BOOT};")
        val workItemBadgeContainer by Style("position: relative; padding-left: 0.5rem; background-color: white;")
        val workItemBadge by Style("background-color: ${Color.BLUE_GRAY_500}")
    }

    private val dollyLikeContainerBase = """
        margin-top: -0.5em;
        padding-top: 0.25em; padding-bottom: 0.25em;
        padding-left: 0.5em; padding-right: 0;
        display: flex;
        align-items: center;
        min-height: 40.6px;
    """

    object dolly : Group(null) {
        class UsualDollyStyles(_groupName: String, backgroundColor: Color) : Group(dolly), DollyStyles {
            override val groupName = _groupName
            override val container by Style("$dollyLikeContainerBase background-color: $backgroundColor;")
            override val containerBusy by Style("$dollyLikeContainerBase background-color: $WHITE; padding-left: 0;")
            override val message by Style("flex-grow: 1;")
            override val button by Style("")
        }

        val normal by named {UsualDollyStyles(it, backgroundColor = BLUE_GRAY_50)}
        val danger by named {UsualDollyStyles(it, backgroundColor = RED_50)}
    }

    object user : Group(null) {
        val stateLabel by EnumStyle(UserState.values(), {when (it) {
            UserState.COOL -> Style("background-color: $WHITE;")
            UserState.PROFILE_PENDING -> Style("background-color: $AMBER_100;")
            UserState.PROFILE_APPROVAL_PENDING -> Style("background-color: $ORANGE_100;")
            UserState.PROFILE_REJECTED -> Style("background-color: $RED_100;")
            UserState.BANNED -> Style("background-color: $RED_300;")
        }})
    }

    object order : Group(null) {
        val stateLabel by EnumStyle(UAOrderState.values(), {when (it) {
            UAOrderState.CREATED -> Style("background-color: green;")
            UAOrderState.CUSTOMER_DRAFT -> Style("background-color: green;")
            UAOrderState.LOOKING_FOR_WRITERS -> Style("background-color: green;")
            UAOrderState.WAITING_FOR_PAYMENT -> Style("background-color: green;")
            UAOrderState.WRITER_ASSIGNED -> Style("background-color: green;")
            UAOrderState.WAITING_EMAIL_CONFIRMATION -> Style("background-color: green;")
            UAOrderState.WAITING_ADMIN_APPROVAL -> Style("background-color: $AMBER_100;")
            UAOrderState.RETURNED_TO_CUSTOMER_FOR_FIXING -> Style("background-color: $RED_50;")
            UAOrderState.IN_STORE -> Style("background-color: $BLUE_100;")
        }})

        object forCustomer : Group(this) {
            val waitingApprovalBanner by named {WaitingBannerStyles(it, this)}
        }

    }

    object rejectionReasonBanner : Group(null) {
        val container by Style("""
                background-color: $RED_50;
                border-left: 0.4rem solid $RED_300;
                padding-top: 0.5rem;
                padding-left: 1rem;
                padding-bottom: 0.5rem;
                margin-bottom: 1.5rem;
            """)
        val title by Style("font-weight: bold;")
        val body by Style("whitespace: pre-wrap;")
    }

    object textField : Group(null) {
        val labelContainer by Style("""
            position: relative;
        """)

        val labelContainerTestHint by Style("""
            position: absolute;
            right: 0;
            top: 0;
            border-bottom: 1px solid #bbb;
            border-right: 1px solid #bbb;
            border-left: 1px solid #bbb;
            padding: 0rem 0.3rem;
            border-bottom-left-radius: 0.4rem;
            border-top-right-radius: 0.4rem;
            background-color: #eee;
            font-size: 75%;
            margin-top: 2.5rem;
            z-index: 10000;
        """)
    }

    object cunt : Group(null) {
        object header : Group(this) {
            val bar by Style("""
                font-size: 18px;
                background-color: #eceff1;
                border-bottom: 1px solid #cfd8dc;
                position: relative;
                display: flex;""")

            val leftIcon by Style("""
                    color: #90a4ae;
                    margin-left: 3px;""")

            val leftOverlayBottomLeftIcon by Style("""
                margin-left: 3px;
                position: absolute;
                left: 2px;
                top: 9px;
                color: #cfd8dc;
                font-size: 60%;""")

            val rightIcon by Style(style = "color: $BLUE_GRAY_300; cursor: pointer;",
                                   hover = "color: $BLUE_GRAY_500;")
            val rightIconActive by Style(style = "color: $BLUE_GRAY_500;")
            val rightIconDisabled by Style(style = "color: $GRAY_500;")

            val controls by Style("padding-right: 0.5rem;")
            val titleAndStuff by Style("flex-grow: 1;")

            val linkStyle = "color: $BLACK_BOOT;"
            val titleLink by Style(link = linkStyle, visited = linkStyle, hover = linkStyle, active = linkStyle)
        }

        val bodyEditing by Style("""
            border-left: 4px solid $BLUE_GRAY_400;
            padding-left: 0.6rem;
            margin-bottom: 1rem;
            padding-top: 1rem;""")
    }

    object test : Group(null) {
        val width = "45rem"

        object sqlfiddle : Group(this) {

        }

        object mailbox : Group(this) {
            val messages by Style("""
            """)

            val message by Style(
                style = """
                """,
                notFirstChild = """
                    margin-top: 1em;
                """)

            val header by Style("""
                background-color: #b3e5fc;
                margin-bottom: 0.25em;;
            """)

            val section by Style("""
                display: flex;
            """)

            val sectionTitle by Style("""
                font-weight: bold;
            """)

            val sectionValue by Style("""
                margin-left: 1ch;
            """)

            val body by Style("""
            """)
        }

        object crossWorld : Group(this) {
            val locationPane by Style("""
                position: fixed;
                top: -0.1rem;
                left: 0rem;
                width: 100%;
                background-color: transparent;
                border: none;
                color: #999;
                font-weight: normal;
                font-size: 1rem;
                font-family: arial narrow;
                z-index: 1000000;
                padding: 0rem;
                text-align: center;
            """)

            val label by Style("""
                border-bottom: 1px solid #bbb;
                border-right: 1px solid #bbb;
                border-left: 1px solid #bbb;
                padding: 0rem 0.3rem;
                border-bottom-right-radius: 0.4rem;
                border-bottom-left-radius: 0.4rem;
                background-color: #eee;
            """)
        }

        object popup : Group(this) {
            val pause by Style("""
                position: fixed;
                width: $width;
                min-height: 10rem;
                background-color: $LIME_100;
                border: 0.4rem solid $LIME_900;
                color: black;
                font-weight: bold;
                padding: 0.2rem;
                z-index: 100000;""")

            val chooseSnapshot by Style("""
                position: fixed;
                bottom: 0px;
                left: 0px;
                width: $width;
                min-height: 10rem;
                background-color: $INDIGO_100;
                border: 0.4rem solid $INDIGO_900;
                color: black;
                font-weight: bold;
                padding: 0.2rem;
                z-index: 100000;""")

            object assertion : Group(this) {
                val notHardened by Style("""
                    opacity: 0.3;
                    position: fixed;
                    width: $width;
                    min-height: 10rem;
                    background-color: $GRAY_300;
                    border: 0.4rem solid $GRAY_700;
                    color: black;
                    font-weight: bold;
                    padding: 0.2rem;
                    z-index: 100000;""")

                val correct by Style("""
                    opacity: 1;
                    position: fixed;
                    width: $width;
                    min-height: 10rem;
                    background-color: $GREEN_200;
                    border: 0.4rem solid $GREEN_700;
                    color: black;
                    font-weight: bold;
                    padding: 0.2rem;
                    z-index: 100000;""")

                val incorrect by Style("""
                    opacity: 0.5;
                    position: fixed;
                    width: $width;
                    min-height: 10rem;
                    background-color: $RED_200;
                    border: 0.4rem solid $RED_700;
                    color: black;
                    font-weight: bold;
                    padding: 0.2rem;
                    z-index: 100000;""")
            }

            object imageViewer : Group(this) {
                val pane by Style("""
                    position: fixed;
                    top: 6rem;
                    left: 1rem;
                    background: $BROWN_50;
                    width: calc(100vw - 2rem - ${fconst.scrollbarWidth}px);
                    height: calc(100vh - 7rem);
                    border: 0.3rem solid $BROWN_300;
                    z-index: 1000000;
                    display: flex;
                    flex-direction: column;""")

                val titleBar by Style("""
                    background: $BROWN_300;
                    padding: 0.2rem;
                    padding-bottom: 0.5rem;""")

                val title by Style("""
                    float: left;
                    font-weight: normal;
                    color: $WHITE;
                    font-size: 125%;
                    margin-top: 0.4rem;
                    margin-left: 0.4rem;""")

                val content by Style("""
                    flex-grow: 1;
                    position: relative;
                    margin: 0.5rem;""")
            }
        }

        object pane : Group(this) {
            object testList : Group(this) {
                val pane by Style("""
                    background-color: $BROWN_500;
                    color: $WHITE;
                    margin-top: 1rem;
                    padding: 10px 10px;
                    text-align: center;
                    font-weight: bold;""")

                val content by Style("""
                    background-color: $WHITE;
                    color: $BLACK_BOOT;
                    font-weight: normal;
                    text-align: left;
                    padding: 0.5rem; """)

                val separator by Style("""
                    margin-top: 0.5rem;
                    padding-top: 0.5rem;
                    border-top: 1px dashed $GRAY_600;""")
            }

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

        object animateUserActions : Group(this) {
            // TODO:vgrechka Tune fill for DOWN and LEFT directions
            val debug = false
            val fillColor = if (!debug) WHITE else PINK_200

            abstract class HandGroup(
                parent: Group?,
                dir: HandDirection,
                iconMarginLeft: String = "0",
                iconMarginTop: String = "0",
                fillContainerTransform: String = "none"
            ) : Group(parent) {
                val blink by KeyFrames("""
                    0% {
                        opacity: 1;
                    }
                    50% {
                        opacity: ${if (!debug) 0 else 1};
                    }""")

                val pane by Style("""
                    ${ifOrEmpty(debug){"border: 1px solid green;"}}
                    position: absolute;
                    top: 10rem;
                    left: 10rem;
                    width: 3rem;
                    height: 3rem;
                    z-index: 1000000;
                    animation: $blink 300ms step-end infinite;""")

                val handIcon by Style("""
                    font-size: 3rem;
                    margin-left: $iconMarginLeft; /* Fingertip is on the     */
                    margin-top: $iconMarginTop;   /* left top corner of pane */
                    color: $BROWN_500;
                    position: absolute; /* Otherwise it appears below handIconFill */""")

                val fillContainer by Style("""
                    position: absolute;
                    transform: $fillContainerTransform;
                """)

                val fillPointingFinger by Style("""
                    position: absolute;
                    background: $fillColor;
                    left: -0.15em;
                    top: 0.1em;
                    width: 0.4em;
                    height: 1em;""")

                val fillWrist by Style("""
                    position: absolute;
                    background: $fillColor;
                    left: -0.09em;
                    top: 1em;
                    width: 1em;
                    height: 1em;""")

                val fillFist by Style("""
                    position: absolute;
                    background: $fillColor;
                    left: 0em;
                    top: 0.67em;
                    width: 1em;
                    height: 0.9em;
                    transform: rotate(18deg);""")

                val fillBigFinger by Style("""
                    position: absolute;
                    background: $fillColor;
                    left: -0.35em;
                    top: 0.65em;
                    width: 0.4em;
                    height: 1em;
                    transform: rotate(-60deg);""")

            }

            object handUp : HandGroup(this, HandDirection.UP, iconMarginLeft = "-1rem")
            object handDown : HandGroup(this, HandDirection.DOWN, iconMarginLeft = "-1rem")
            object handLeft : HandGroup(this, HandDirection.LEFT)
            object handRight : HandGroup(this, HandDirection.RIGHT,
                                         iconMarginLeft = "-3rem",
                                         iconMarginTop = "-1.2rem",
                                         fillContainerTransform = "rotate(90deg)")
        }
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


    val lipsItemContainer by Style("")
    val lipsItem by Style("margin-bottom: 1.5rem;")

    init {touchObjectGraph(this)}
}

class IconClass(val className: String) {
    override fun toString() = className
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

        .modal-open .modal {
            overflow-x: hidden;
            overflow-y: scroll;
        }
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

