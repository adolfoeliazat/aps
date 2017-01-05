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

            object imageViewer : Group(this) {
                val pane by Style("""
                    position: fixed;
                    top: 6rem;
                    left: 1rem;
                    background: $BROWN_50;
                    width: calc(100vw - 2rem - 17px);
                    height: calc(100vh - 7rem);
                    border: 0.3rem solid $BROWN_300;
                    z-index: 1000000;""")
                val title by Style("""
                    """)
                val content by Style("""
                    """)
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
    val addressBook = IconClass("fa fa-address-book")
    val addressBookO = IconClass("fa fa-address-book-o")
    val addressCard = IconClass("fa fa-address-card")
    val addressCardO = IconClass("fa fa-address-card-o")
    val adjust = IconClass("fa fa-adjust")
    val anchor = IconClass("fa fa-anchor")
    val archive = IconClass("fa fa-archive")
    val asterisk = IconClass("fa fa-asterisk")
    val at = IconClass("fa fa-at")
    val balanceScale = IconClass("fa fa-balance-scale")
    val ban = IconClass("fa fa-ban")
    val bank = IconClass("fa fa-bank")
    val barcode = IconClass("fa fa-barcode")
    val bars = IconClass("fa fa-bars")
    val bath = IconClass("fa fa-bath")
    val bathtub = IconClass("fa fa-bathtub")
    val battery = IconClass("fa fa-battery")
    val battery0 = IconClass("fa fa-battery-0")
    val battery1 = IconClass("fa fa-battery-1")
    val battery2 = IconClass("fa fa-battery-2")
    val battery3 = IconClass("fa fa-battery-3")
    val battery4 = IconClass("fa fa-battery-4")
    val batteryEmpty = IconClass("fa fa-battery-empty")
    val batteryFull = IconClass("fa fa-battery-full")
    val batteryHalf = IconClass("fa fa-battery-half")
    val batteryQuarter = IconClass("fa fa-battery-quarter")
    val batteryThreeQuarters = IconClass("fa fa-battery-three-quarters")
    val bed = IconClass("fa fa-bed")
    val beer = IconClass("fa fa-beer")
    val bell = IconClass("fa fa-bell")
    val bellO = IconClass("fa fa-bell-o")
    val bellSlash = IconClass("fa fa-bell-slash")
    val bellSlashO = IconClass("fa fa-bell-slash-o")
    val binoculars = IconClass("fa fa-binoculars")
    val birthdayCake = IconClass("fa fa-birthday-cake")
    val bolt = IconClass("fa fa-bolt")
    val bomb = IconClass("fa fa-bomb")
    val book = IconClass("fa fa-book")
    val bookmark = IconClass("fa fa-bookmark")
    val bookmarkO = IconClass("fa fa-bookmark-o")
    val briefcase = IconClass("fa fa-briefcase")
    val bug = IconClass("fa fa-bug")
    val building = IconClass("fa fa-building")
    val buildingO = IconClass("fa fa-building-o")
    val bullhorn = IconClass("fa fa-bullhorn")
    val bullseye = IconClass("fa fa-bullseye")
    val calculator = IconClass("fa fa-calculator")
    val calendar = IconClass("fa fa-calendar")
    val calendarCheckO = IconClass("fa fa-calendar-check-o")
    val calendarMinusO = IconClass("fa fa-calendar-minus-o")
    val calendarO = IconClass("fa fa-calendar-o")
    val calendarPlusO = IconClass("fa fa-calendar-plus-o")
    val calendarTimesO = IconClass("fa fa-calendar-times-o")
    val camera = IconClass("fa fa-camera")
    val cameraRetro = IconClass("fa fa-camera-retro")
    val cartArrowDown = IconClass("fa fa-cart-arrow-down")
    val cartPlus = IconClass("fa fa-cart-plus")
    val certificate = IconClass("fa fa-certificate")
    val check = IconClass("fa fa-check")
    val checkCircle = IconClass("fa fa-check-circle")
    val checkCircleO = IconClass("fa fa-check-circle-o")
    val child = IconClass("fa fa-child")
    val circleONotch = IconClass("fa fa-circle-o-notch")
    val circleThin = IconClass("fa fa-circle-thin")
    val clockO = IconClass("fa fa-clock-o")
    val clone = IconClass("fa fa-clone")
    val close = IconClass("fa fa-close")
    val cloud = IconClass("fa fa-cloud")
    val cloudDownload = IconClass("fa fa-cloud-download")
    val cloudUpload = IconClass("fa fa-cloud-upload")
    val code = IconClass("fa fa-code")
    val codeFork = IconClass("fa fa-code-fork")
    val coffee = IconClass("fa fa-coffee")
    val cogs = IconClass("fa fa-cogs")
    val comment = IconClass("fa fa-comment")
    val commentO = IconClass("fa fa-comment-o")
    val commenting = IconClass("fa fa-commenting")
    val commentingO = IconClass("fa fa-commenting-o")
    val comments = IconClass("fa fa-comments")
    val commentsO = IconClass("fa fa-comments-o")
    val compass = IconClass("fa fa-compass")
    val copyright = IconClass("fa fa-copyright")
    val creativeCommons = IconClass("fa fa-creative-commons")
    val crop = IconClass("fa fa-crop")
    val crosshairs = IconClass("fa fa-crosshairs")
    val cube = IconClass("fa fa-cube")
    val cubes = IconClass("fa fa-cubes")
    val cutlery = IconClass("fa fa-cutlery")
    val dashboard = IconClass("fa fa-dashboard")
    val database = IconClass("fa fa-database")
    val desktop = IconClass("fa fa-desktop")
    val diamond = IconClass("fa fa-diamond")
    val download = IconClass("fa fa-download")
    val driversLicense = IconClass("fa fa-drivers-license")
    val driversLicenseO = IconClass("fa fa-drivers-license-o")
    val edit = IconClass("fa fa-edit")
    val ellipsisH = IconClass("fa fa-ellipsis-h")
    val ellipsisV = IconClass("fa fa-ellipsis-v")
    val envelope = IconClass("fa fa-envelope")
    val envelopeO = IconClass("fa fa-envelope-o")
    val envelopeOpen = IconClass("fa fa-envelope-open")
    val envelopeOpenO = IconClass("fa fa-envelope-open-o")
    val envelopeSquare = IconClass("fa fa-envelope-square")
    val exclamation = IconClass("fa fa-exclamation")
    val exclamationCircle = IconClass("fa fa-exclamation-circle")
    val exclamationTriangle = IconClass("fa fa-exclamation-triangle")
    val externalLink = IconClass("fa fa-external-link")
    val externalLinkSquare = IconClass("fa fa-external-link-square")
    val eye = IconClass("fa fa-eye")
    val eyeSlash = IconClass("fa fa-eye-slash")
    val eyedropper = IconClass("fa fa-eyedropper")
    val fax = IconClass("fa fa-fax")
    val feed = IconClass("fa fa-feed")
    val female = IconClass("fa fa-female")
    val film = IconClass("fa fa-film")
    val filter = IconClass("fa fa-filter")
    val fire = IconClass("fa fa-fire")
    val fireExtinguisher = IconClass("fa fa-fire-extinguisher")
    val flag = IconClass("fa fa-flag")
    val flagCheckered = IconClass("fa fa-flag-checkered")
    val flagO = IconClass("fa fa-flag-o")
    val flash = IconClass("fa fa-flash")
    val flask = IconClass("fa fa-flask")
    val folder = IconClass("fa fa-folder")
    val folderO = IconClass("fa fa-folder-o")
    val folderOpen = IconClass("fa fa-folder-open")
    val folderOpenO = IconClass("fa fa-folder-open-o")
    val frownO = IconClass("fa fa-frown-o")
    val futbolO = IconClass("fa fa-futbol-o")
    val gamepad = IconClass("fa fa-gamepad")
    val gavel = IconClass("fa fa-gavel")
    val gears = IconClass("fa fa-gears")
    val gift = IconClass("fa fa-gift")
    val glass = IconClass("fa fa-glass")
    val globe = IconClass("fa fa-globe")
    val graduationCap = IconClass("fa fa-graduation-cap")
    val group = IconClass("fa fa-group")
    val handshakeO = IconClass("fa fa-handshake-o")
    val hashtag = IconClass("fa fa-hashtag")
    val hddO = IconClass("fa fa-hdd-o")
    val headphones = IconClass("fa fa-headphones")
    val history = IconClass("fa fa-history")
    val home = IconClass("fa fa-home")
    val hotel = IconClass("fa fa-hotel")
    val hourglass = IconClass("fa fa-hourglass")
    val hourglass1 = IconClass("fa fa-hourglass-1")
    val hourglass2 = IconClass("fa fa-hourglass-2")
    val hourglass3 = IconClass("fa fa-hourglass-3")
    val hourglassEnd = IconClass("fa fa-hourglass-end")
    val hourglassHalf = IconClass("fa fa-hourglass-half")
    val hourglassO = IconClass("fa fa-hourglass-o")
    val hourglassStart = IconClass("fa fa-hourglass-start")
    val iCursor = IconClass("fa fa-i-cursor")
    val idBadge = IconClass("fa fa-id-badge")
    val idCard = IconClass("fa fa-id-card")
    val idCardO = IconClass("fa fa-id-card-o")
    val image = IconClass("fa fa-image")
    val inbox = IconClass("fa fa-inbox")
    val industry = IconClass("fa fa-industry")
    val info = IconClass("fa fa-info")
    val infoCircle = IconClass("fa fa-info-circle")
    val institution = IconClass("fa fa-institution")
    val key = IconClass("fa fa-key")
    val keyboardO = IconClass("fa fa-keyboard-o")
    val language = IconClass("fa fa-language")
    val laptop = IconClass("fa fa-laptop")
    val leaf = IconClass("fa fa-leaf")
    val legal = IconClass("fa fa-legal")
    val lemonO = IconClass("fa fa-lemon-o")
    val levelDown = IconClass("fa fa-level-down")
    val levelUp = IconClass("fa fa-level-up")
    val lifeBouy = IconClass("fa fa-life-bouy")
    val lifeBuoy = IconClass("fa fa-life-buoy")
    val lifeRing = IconClass("fa fa-life-ring")
    val lifeSaver = IconClass("fa fa-life-saver")
    val lightbulbO = IconClass("fa fa-lightbulb-o")
    val locationArrow = IconClass("fa fa-location-arrow")
    val lock = IconClass("fa fa-lock")
    val magic = IconClass("fa fa-magic")
    val magnet = IconClass("fa fa-magnet")
    val mailForward = IconClass("fa fa-mail-forward")
    val mailReply = IconClass("fa fa-mail-reply")
    val mailReplyAll = IconClass("fa fa-mail-reply-all")
    val male = IconClass("fa fa-male")
    val map = IconClass("fa fa-map")
    val mapMarker = IconClass("fa fa-map-marker")
    val mapO = IconClass("fa fa-map-o")
    val mapPin = IconClass("fa fa-map-pin")
    val mapSigns = IconClass("fa fa-map-signs")
    val mehO = IconClass("fa fa-meh-o")
    val microchip = IconClass("fa fa-microchip")
    val microphone = IconClass("fa fa-microphone")
    val microphoneSlash = IconClass("fa fa-microphone-slash")
    val minus = IconClass("fa fa-minus")
    val minusCircle = IconClass("fa fa-minus-circle")
    val mobile = IconClass("fa fa-mobile")
    val mobilePhone = IconClass("fa fa-mobile-phone")
    val moonO = IconClass("fa fa-moon-o")
    val mortarBoard = IconClass("fa fa-mortar-board")
    val mousePointer = IconClass("fa fa-mouse-pointer")
    val music = IconClass("fa fa-music")
    val navicon = IconClass("fa fa-navicon")
    val newspaperO = IconClass("fa fa-newspaper-o")
    val objectGroup = IconClass("fa fa-object-group")
    val objectUngroup = IconClass("fa fa-object-ungroup")
    val paintBrush = IconClass("fa fa-paint-brush")
    val paperPlane = IconClass("fa fa-paper-plane")
    val paperPlaneO = IconClass("fa fa-paper-plane-o")
    val paw = IconClass("fa fa-paw")
    val pencil = IconClass("fa fa-pencil")
    val pencilSquare = IconClass("fa fa-pencil-square")
    val pencilSquareO = IconClass("fa fa-pencil-square-o")
    val percent = IconClass("fa fa-percent")
    val phone = IconClass("fa fa-phone")
    val phoneSquare = IconClass("fa fa-phone-square")
    val photo = IconClass("fa fa-photo")
    val pictureO = IconClass("fa fa-picture-o")
    val plug = IconClass("fa fa-plug")
    val plus = IconClass("fa fa-plus")
    val plusCircle = IconClass("fa fa-plus-circle")
    val podcast = IconClass("fa fa-podcast")
    val powerOff = IconClass("fa fa-power-off")
    val print = IconClass("fa fa-print")
    val puzzlePiece = IconClass("fa fa-puzzle-piece")
    val qrcode = IconClass("fa fa-qrcode")
    val question = IconClass("fa fa-question")
    val questionCircle = IconClass("fa fa-question-circle")
    val quoteLeft = IconClass("fa fa-quote-left")
    val quoteRight = IconClass("fa fa-quote-right")
    val recycle = IconClass("fa fa-recycle")
    val registered = IconClass("fa fa-registered")
    val remove = IconClass("fa fa-remove")
    val reorder = IconClass("fa fa-reorder")
    val reply = IconClass("fa fa-reply")
    val replyAll = IconClass("fa fa-reply-all")
    val retweet = IconClass("fa fa-retweet")
    val road = IconClass("fa fa-road")
    val rss = IconClass("fa fa-rss")
    val rssSquare = IconClass("fa fa-rss-square")
    val s15 = IconClass("fa fa-s15")
    val search = IconClass("fa fa-search")
    val searchMinus = IconClass("fa fa-search-minus")
    val searchPlus = IconClass("fa fa-search-plus")
    val send = IconClass("fa fa-send")
    val sendO = IconClass("fa fa-send-o")
    val server = IconClass("fa fa-server")
    val share = IconClass("fa fa-share")
    val shareSquare = IconClass("fa fa-share-square")
    val shareSquareO = IconClass("fa fa-share-square-o")
    val shield = IconClass("fa fa-shield")
    val shoppingBag = IconClass("fa fa-shopping-bag")
    val shoppingBasket = IconClass("fa fa-shopping-basket")
    val shoppingCart = IconClass("fa fa-shopping-cart")
    val shower = IconClass("fa fa-shower")
    val signIn = IconClass("fa fa-sign-in")
    val signOut = IconClass("fa fa-sign-out")
    val signal = IconClass("fa fa-signal")
    val sitemap = IconClass("fa fa-sitemap")
    val sliders = IconClass("fa fa-sliders")
    val smileO = IconClass("fa fa-smile-o")
    val snowflakeO = IconClass("fa fa-snowflake-o")
    val soccerBallO = IconClass("fa fa-soccer-ball-o")
    val sort = IconClass("fa fa-sort")
    val sortAlphaAsc = IconClass("fa fa-sort-alpha-asc")
    val sortAlphaDesc = IconClass("fa fa-sort-alpha-desc")
    val sortAmountAsc = IconClass("fa fa-sort-amount-asc")
    val sortAmountDesc = IconClass("fa fa-sort-amount-desc")
    val sortAsc = IconClass("fa fa-sort-asc")
    val sortDesc = IconClass("fa fa-sort-desc")
    val sortDown = IconClass("fa fa-sort-down")
    val sortNumericAsc = IconClass("fa fa-sort-numeric-asc")
    val sortNumericDesc = IconClass("fa fa-sort-numeric-desc")
    val sortUp = IconClass("fa fa-sort-up")
    val spoon = IconClass("fa fa-spoon")
    val star = IconClass("fa fa-star")
    val starHalf = IconClass("fa fa-star-half")
    val starHalfEmpty = IconClass("fa fa-star-half-empty")
    val starHalfFull = IconClass("fa fa-star-half-full")
    val starHalfO = IconClass("fa fa-star-half-o")
    val starO = IconClass("fa fa-star-o")
    val stickyNote = IconClass("fa fa-sticky-note")
    val stickyNoteO = IconClass("fa fa-sticky-note-o")
    val streetView = IconClass("fa fa-street-view")
    val suitcase = IconClass("fa fa-suitcase")
    val sunO = IconClass("fa fa-sun-o")
    val support = IconClass("fa fa-support")
    val tablet = IconClass("fa fa-tablet")
    val tachometer = IconClass("fa fa-tachometer")
    val tag = IconClass("fa fa-tag")
    val tags = IconClass("fa fa-tags")
    val tasks = IconClass("fa fa-tasks")
    val television = IconClass("fa fa-television")
    val terminal = IconClass("fa fa-terminal")
    val thermometer = IconClass("fa fa-thermometer")
    val thermometer0 = IconClass("fa fa-thermometer-0")
    val thermometer1 = IconClass("fa fa-thermometer-1")
    val thermometer2 = IconClass("fa fa-thermometer-2")
    val thermometer3 = IconClass("fa fa-thermometer-3")
    val thermometer4 = IconClass("fa fa-thermometer-4")
    val thermometerEmpty = IconClass("fa fa-thermometer-empty")
    val thermometerFull = IconClass("fa fa-thermometer-full")
    val thermometerHalf = IconClass("fa fa-thermometer-half")
    val thermometerQuarter = IconClass("fa fa-thermometer-quarter")
    val thermometerThreeQuarters = IconClass("fa fa-thermometer-three-quarters")
    val thumbTack = IconClass("fa fa-thumb-tack")
    val ticket = IconClass("fa fa-ticket")
    val times = IconClass("fa fa-times")
    val timesCircle = IconClass("fa fa-times-circle")
    val timesCircleO = IconClass("fa fa-times-circle-o")
    val timesRectangle = IconClass("fa fa-times-rectangle")
    val timesRectangleO = IconClass("fa fa-times-rectangle-o")
    val tint = IconClass("fa fa-tint")
    val toggleOff = IconClass("fa fa-toggle-off")
    val toggleOn = IconClass("fa fa-toggle-on")
    val trademark = IconClass("fa fa-trademark")
    val trash = IconClass("fa fa-trash")
    val trashO = IconClass("fa fa-trash-o")
    val tree = IconClass("fa fa-tree")
    val trophy = IconClass("fa fa-trophy")
    val tv = IconClass("fa fa-tv")
    val umbrella = IconClass("fa fa-umbrella")
    val university = IconClass("fa fa-university")
    val unlock = IconClass("fa fa-unlock")
    val unlockAlt = IconClass("fa fa-unlock-alt")
    val unsorted = IconClass("fa fa-unsorted")
    val upload = IconClass("fa fa-upload")
    val user = IconClass("fa fa-user")
    val userCircle = IconClass("fa fa-user-circle")
    val userCircleO = IconClass("fa fa-user-circle-o")
    val userO = IconClass("fa fa-user-o")
    val userPlus = IconClass("fa fa-user-plus")
    val userSecret = IconClass("fa fa-user-secret")
    val userTimes = IconClass("fa fa-user-times")
    val users = IconClass("fa fa-users")
    val vcard = IconClass("fa fa-vcard")
    val vcardO = IconClass("fa fa-vcard-o")
    val videoCamera = IconClass("fa fa-video-camera")
    val volumeDown = IconClass("fa fa-volume-down")
    val volumeOff = IconClass("fa fa-volume-off")
    val volumeUp = IconClass("fa fa-volume-up")
    val warning = IconClass("fa fa-warning")
    val wifi = IconClass("fa fa-wifi")
    val windowClose = IconClass("fa fa-window-close")
    val windowCloseO = IconClass("fa fa-window-close-o")
    val windowMaximize = IconClass("fa fa-window-maximize")
    val windowMinimize = IconClass("fa fa-window-minimize")
    val windowRestore = IconClass("fa fa-window-restore")
    val wrench = IconClass("fa fa-wrench")
    val americanSignLanguageInterpreting = IconClass("fa fa-american-sign-language-interpreting")
    val aslInterpreting = IconClass("fa fa-asl-interpreting")
    val assistiveListeningSystems = IconClass("fa fa-assistive-listening-systems")
    val audioDescription = IconClass("fa fa-audio-description")
    val blind = IconClass("fa fa-blind")
    val braille = IconClass("fa fa-braille")
    val cc = IconClass("fa fa-cc")
    val deaf = IconClass("fa fa-deaf")
    val deafness = IconClass("fa fa-deafness")
    val hardOfHearing = IconClass("fa fa-hard-of-hearing")
    val lowVision = IconClass("fa fa-low-vision")
    val questionCircleO = IconClass("fa fa-question-circle-o")
    val handGrabO = IconClass("fa fa-hand-grab-o")
    val handLizardO = IconClass("fa fa-hand-lizard-o")
    val handPaperO = IconClass("fa fa-hand-paper-o")
    val handPeaceO = IconClass("fa fa-hand-peace-o")
    val handPointerO = IconClass("fa fa-hand-pointer-o")
    val handRockO = IconClass("fa fa-hand-rock-o")
    val handScissorsO = IconClass("fa fa-hand-scissors-o")
    val handSpockO = IconClass("fa fa-hand-spock-o")
    val handStopO = IconClass("fa fa-hand-stop-o")
    val automobile = IconClass("fa fa-automobile")
    val bicycle = IconClass("fa fa-bicycle")
    val bus = IconClass("fa fa-bus")
    val cab = IconClass("fa fa-cab")
    val car = IconClass("fa fa-car")
    val fighterJet = IconClass("fa fa-fighter-jet")
    val motorcycle = IconClass("fa fa-motorcycle")
    val plane = IconClass("fa fa-plane")
    val rocket = IconClass("fa fa-rocket")
    val subway = IconClass("fa fa-subway")
    val train = IconClass("fa fa-train")
    val genderless = IconClass("fa fa-genderless")
    val intersex = IconClass("fa fa-intersex")
    val mars = IconClass("fa fa-mars")
    val marsDouble = IconClass("fa fa-mars-double")
    val marsStroke = IconClass("fa fa-mars-stroke")
    val marsStrokeH = IconClass("fa fa-mars-stroke-h")
    val marsStrokeV = IconClass("fa fa-mars-stroke-v")
    val mercury = IconClass("fa fa-mercury")
    val neuter = IconClass("fa fa-neuter")
    val transgender = IconClass("fa fa-transgender")
    val transgenderAlt = IconClass("fa fa-transgender-alt")
    val venus = IconClass("fa fa-venus")
    val venusDouble = IconClass("fa fa-venus-double")
    val venusMars = IconClass("fa fa-venus-mars")
    val fileArchiveO = IconClass("fa fa-file-archive-o")
    val fileAudioO = IconClass("fa fa-file-audio-o")
    val fileCodeO = IconClass("fa fa-file-code-o")
    val fileExcelO = IconClass("fa fa-file-excel-o")
    val fileImageO = IconClass("fa fa-file-image-o")
    val fileMovieO = IconClass("fa fa-file-movie-o")
    val filePdfO = IconClass("fa fa-file-pdf-o")
    val filePhotoO = IconClass("fa fa-file-photo-o")
    val filePictureO = IconClass("fa fa-file-picture-o")
    val filePowerpointO = IconClass("fa fa-file-powerpoint-o")
    val fileSoundO = IconClass("fa fa-file-sound-o")
    val fileVideoO = IconClass("fa fa-file-video-o")
    val fileWordO = IconClass("fa fa-file-word-o")
    val fileZipO = IconClass("fa fa-file-zip-o")
    val checkSquare = IconClass("fa fa-check-square")
    val checkSquareO = IconClass("fa fa-check-square-o")
    val circle = IconClass("fa fa-circle")
    val circleO = IconClass("fa fa-circle-o")
    val dotCircleO = IconClass("fa fa-dot-circle-o")
    val minusSquare = IconClass("fa fa-minus-square")
    val minusSquareO = IconClass("fa fa-minus-square-o")
    val plusSquareO = IconClass("fa fa-plus-square-o")
    val square = IconClass("fa fa-square")
    val squareO = IconClass("fa fa-square-o")
    val creditCard = IconClass("fa fa-credit-card")
    val creditCardAlt = IconClass("fa fa-credit-card-alt")
    val areaChart = IconClass("fa fa-area-chart")
    val barChart = IconClass("fa fa-bar-chart")
    val barChartO = IconClass("fa fa-bar-chart-o")
    val lineChart = IconClass("fa fa-line-chart")
    val pieChart = IconClass("fa fa-pie-chart")
    val cny = IconClass("fa fa-cny")
    val dollar = IconClass("fa fa-dollar")
    val eur = IconClass("fa fa-eur")
    val euro = IconClass("fa fa-euro")
    val gbp = IconClass("fa fa-gbp")
    val ils = IconClass("fa fa-ils")
    val inr = IconClass("fa fa-inr")
    val jpy = IconClass("fa fa-jpy")
    val krw = IconClass("fa fa-krw")
    val money = IconClass("fa fa-money")
    val rmb = IconClass("fa fa-rmb")
    val rouble = IconClass("fa fa-rouble")
    val rub = IconClass("fa fa-rub")
    val ruble = IconClass("fa fa-ruble")
    val rupee = IconClass("fa fa-rupee")
    val shekel = IconClass("fa fa-shekel")
    val sheqel = IconClass("fa fa-sheqel")
    val turkishLira = IconClass("fa fa-turkish-lira")
    val usd = IconClass("fa fa-usd")
    val won = IconClass("fa fa-won")
    val yen = IconClass("fa fa-yen")
    val alignCenter = IconClass("fa fa-align-center")
    val alignJustify = IconClass("fa fa-align-justify")
    val alignLeft = IconClass("fa fa-align-left")
    val alignRight = IconClass("fa fa-align-right")
    val bold = IconClass("fa fa-bold")
    val chain = IconClass("fa fa-chain")
    val chainBroken = IconClass("fa fa-chain-broken")
    val clipboard = IconClass("fa fa-clipboard")
    val columns = IconClass("fa fa-columns")
    val copy = IconClass("fa fa-copy")
    val cut = IconClass("fa fa-cut")
    val dedent = IconClass("fa fa-dedent")
    val eraser = IconClass("fa fa-eraser")
    val file = IconClass("fa fa-file")
    val fileO = IconClass("fa fa-file-o")
    val fileText = IconClass("fa fa-file-text")
    val fileTextO = IconClass("fa fa-file-text-o")
    val filesO = IconClass("fa fa-files-o")
    val floppyO = IconClass("fa fa-floppy-o")
    val font = IconClass("fa fa-font")
    val header = IconClass("fa fa-header")
    val indent = IconClass("fa fa-indent")
    val italic = IconClass("fa fa-italic")
    val link = IconClass("fa fa-link")
    val list = IconClass("fa fa-list")
    val listAlt = IconClass("fa fa-list-alt")
    val listOl = IconClass("fa fa-list-ol")
    val listUl = IconClass("fa fa-list-ul")
    val outdent = IconClass("fa fa-outdent")
    val paperclip = IconClass("fa fa-paperclip")
    val paragraph = IconClass("fa fa-paragraph")
    val paste = IconClass("fa fa-paste")
    val repeat = IconClass("fa fa-repeat")
    val rotateLeft = IconClass("fa fa-rotate-left")
    val rotateRight = IconClass("fa fa-rotate-right")
    val save = IconClass("fa fa-save")
    val scissors = IconClass("fa fa-scissors")
    val strikethrough = IconClass("fa fa-strikethrough")
    val subscript = IconClass("fa fa-subscript")
    val superscript = IconClass("fa fa-superscript")
    val table = IconClass("fa fa-table")
    val textHeight = IconClass("fa fa-text-height")
    val textWidth = IconClass("fa fa-text-width")
    val th = IconClass("fa fa-th")
    val thLarge = IconClass("fa fa-th-large")
    val thList = IconClass("fa fa-th-list")
    val underline = IconClass("fa fa-underline")
    val undo = IconClass("fa fa-undo")
    val unlink = IconClass("fa fa-unlink")
    val angleDoubleDown = IconClass("fa fa-angle-double-down")
    val angleDoubleLeft = IconClass("fa fa-angle-double-left")
    val angleDoubleRight = IconClass("fa fa-angle-double-right")
    val angleDoubleUp = IconClass("fa fa-angle-double-up")
    val angleDown = IconClass("fa fa-angle-down")
    val angleLeft = IconClass("fa fa-angle-left")
    val angleRight = IconClass("fa fa-angle-right")
    val angleUp = IconClass("fa fa-angle-up")
    val arrowCircleDown = IconClass("fa fa-arrow-circle-down")
    val arrowCircleLeft = IconClass("fa fa-arrow-circle-left")
    val arrowCircleODown = IconClass("fa fa-arrow-circle-o-down")
    val arrowCircleOLeft = IconClass("fa fa-arrow-circle-o-left")
    val arrowCircleORight = IconClass("fa fa-arrow-circle-o-right")
    val arrowCircleOUp = IconClass("fa fa-arrow-circle-o-up")
    val arrowCircleRight = IconClass("fa fa-arrow-circle-right")
    val arrowCircleUp = IconClass("fa fa-arrow-circle-up")
    val arrowDown = IconClass("fa fa-arrow-down")
    val arrowLeft = IconClass("fa fa-arrow-left")
    val arrowRight = IconClass("fa fa-arrow-right")
    val arrowUp = IconClass("fa fa-arrow-up")
    val arrows = IconClass("fa fa-arrows")
    val arrowsH = IconClass("fa fa-arrows-h")
    val arrowsV = IconClass("fa fa-arrows-v")
    val caretDown = IconClass("fa fa-caret-down")
    val caretLeft = IconClass("fa fa-caret-left")
    val caretRight = IconClass("fa fa-caret-right")
    val caretSquareODown = IconClass("fa fa-caret-square-o-down")
    val caretSquareOLeft = IconClass("fa fa-caret-square-o-left")
    val caretSquareORight = IconClass("fa fa-caret-square-o-right")
    val caretSquareOUp = IconClass("fa fa-caret-square-o-up")
    val caretUp = IconClass("fa fa-caret-up")
    val chevronCircleDown = IconClass("fa fa-chevron-circle-down")
    val chevronCircleLeft = IconClass("fa fa-chevron-circle-left")
    val chevronCircleRight = IconClass("fa fa-chevron-circle-right")
    val chevronCircleUp = IconClass("fa fa-chevron-circle-up")
    val chevronDown = IconClass("fa fa-chevron-down")
    val chevronLeft = IconClass("fa fa-chevron-left")
    val chevronRight = IconClass("fa fa-chevron-right")
    val chevronUp = IconClass("fa fa-chevron-up")
    val exchange = IconClass("fa fa-exchange")
    val handOLeft = IconClass("fa fa-hand-o-left")
    val handORight = IconClass("fa fa-hand-o-right")
    val handOUp = IconClass("fa fa-hand-o-up")
    val longArrowDown = IconClass("fa fa-long-arrow-down")
    val longArrowLeft = IconClass("fa fa-long-arrow-left")
    val longArrowRight = IconClass("fa fa-long-arrow-right")
    val longArrowUp = IconClass("fa fa-long-arrow-up")
    val toggleDown = IconClass("fa fa-toggle-down")
    val toggleLeft = IconClass("fa fa-toggle-left")
    val toggleRight = IconClass("fa fa-toggle-right")
    val toggleUp = IconClass("fa fa-toggle-up")
    val arrowsAlt = IconClass("fa fa-arrows-alt")
    val backward = IconClass("fa fa-backward")
    val compress = IconClass("fa fa-compress")
    val eject = IconClass("fa fa-eject")
    val expand = IconClass("fa fa-expand")
    val fastBackward = IconClass("fa fa-fast-backward")
    val fastForward = IconClass("fa fa-fast-forward")
    val forward = IconClass("fa fa-forward")
    val pause = IconClass("fa fa-pause")
    val pauseCircle = IconClass("fa fa-pause-circle")
    val pauseCircleO = IconClass("fa fa-pause-circle-o")
    val play = IconClass("fa fa-play")
    val playCircle = IconClass("fa fa-play-circle")
    val playCircleO = IconClass("fa fa-play-circle-o")
    val random = IconClass("fa fa-random")
    val stepBackward = IconClass("fa fa-step-backward")
    val stepForward = IconClass("fa fa-step-forward")
    val stop = IconClass("fa fa-stop")
    val stopCircle = IconClass("fa fa-stop-circle")
    val stopCircleO = IconClass("fa fa-stop-circle-o")
    val adn = IconClass("fa fa-adn")
    val amazon = IconClass("fa fa-amazon")
    val android = IconClass("fa fa-android")
    val angellist = IconClass("fa fa-angellist")
    val apple = IconClass("fa fa-apple")
    val bandcamp = IconClass("fa fa-bandcamp")
    val behance = IconClass("fa fa-behance")
    val behanceSquare = IconClass("fa fa-behance-square")
    val bitbucket = IconClass("fa fa-bitbucket")
    val bitbucketSquare = IconClass("fa fa-bitbucket-square")
    val bitcoin = IconClass("fa fa-bitcoin")
    val blackTie = IconClass("fa fa-black-tie")
    val bluetooth = IconClass("fa fa-bluetooth")
    val bluetoothB = IconClass("fa fa-bluetooth-b")
    val btc = IconClass("fa fa-btc")
    val buysellads = IconClass("fa fa-buysellads")
    val ccAmex = IconClass("fa fa-cc-amex")
    val ccDinersClub = IconClass("fa fa-cc-diners-club")
    val ccDiscover = IconClass("fa fa-cc-discover")
    val ccJcb = IconClass("fa fa-cc-jcb")
    val ccMastercard = IconClass("fa fa-cc-mastercard")
    val ccPaypal = IconClass("fa fa-cc-paypal")
    val ccStripe = IconClass("fa fa-cc-stripe")
    val ccVisa = IconClass("fa fa-cc-visa")
    val chrome = IconClass("fa fa-chrome")
    val codepen = IconClass("fa fa-codepen")
    val codiepie = IconClass("fa fa-codiepie")
    val connectdevelop = IconClass("fa fa-connectdevelop")
    val contao = IconClass("fa fa-contao")
    val css3 = IconClass("fa fa-css3")
    val dashcube = IconClass("fa fa-dashcube")
    val delicious = IconClass("fa fa-delicious")
    val deviantart = IconClass("fa fa-deviantart")
    val digg = IconClass("fa fa-digg")
    val dribbble = IconClass("fa fa-dribbble")
    val dropbox = IconClass("fa fa-dropbox")
    val drupal = IconClass("fa fa-drupal")
    val edge = IconClass("fa fa-edge")
    val eercast = IconClass("fa fa-eercast")
    val empire = IconClass("fa fa-empire")
    val envira = IconClass("fa fa-envira")
    val etsy = IconClass("fa fa-etsy")
    val expeditedssl = IconClass("fa fa-expeditedssl")
    val fa = IconClass("fa fa-fa")
    val facebook = IconClass("fa fa-facebook")
    val facebookF = IconClass("fa fa-facebook-f")
    val facebookOfficial = IconClass("fa fa-facebook-official")
    val facebookSquare = IconClass("fa fa-facebook-square")
    val firefox = IconClass("fa fa-firefox")
    val firstOrder = IconClass("fa fa-first-order")
    val flickr = IconClass("fa fa-flickr")
    val fontAwesome = IconClass("fa fa-font-awesome")
    val fonticons = IconClass("fa fa-fonticons")
    val fortAwesome = IconClass("fa fa-fort-awesome")
    val forumbee = IconClass("fa fa-forumbee")
    val foursquare = IconClass("fa fa-foursquare")
    val freeCodeCamp = IconClass("fa fa-free-code-camp")
    val ge = IconClass("fa fa-ge")
    val getPocket = IconClass("fa fa-get-pocket")
    val gg = IconClass("fa fa-gg")
    val ggCircle = IconClass("fa fa-gg-circle")
    val git = IconClass("fa fa-git")
    val gitSquare = IconClass("fa fa-git-square")
    val github = IconClass("fa fa-github")
    val githubAlt = IconClass("fa fa-github-alt")
    val githubSquare = IconClass("fa fa-github-square")
    val gitlab = IconClass("fa fa-gitlab")
    val gittip = IconClass("fa fa-gittip")
    val glide = IconClass("fa fa-glide")
    val glideG = IconClass("fa fa-glide-g")
    val google = IconClass("fa fa-google")
    val googlePlus = IconClass("fa fa-google-plus")
    val googlePlusCircle = IconClass("fa fa-google-plus-circle")
    val googlePlusOfficial = IconClass("fa fa-google-plus-official")
    val googlePlusSquare = IconClass("fa fa-google-plus-square")
    val googleWallet = IconClass("fa fa-google-wallet")
    val gratipay = IconClass("fa fa-gratipay")
    val grav = IconClass("fa fa-grav")
    val hackerNews = IconClass("fa fa-hacker-news")
    val houzz = IconClass("fa fa-houzz")
    val html5 = IconClass("fa fa-html5")
    val imdb = IconClass("fa fa-imdb")
    val instagram = IconClass("fa fa-instagram")
    val internetExplorer = IconClass("fa fa-internet-explorer")
    val ioxhost = IconClass("fa fa-ioxhost")
    val joomla = IconClass("fa fa-joomla")
    val jsfiddle = IconClass("fa fa-jsfiddle")
    val lastfm = IconClass("fa fa-lastfm")
    val lastfmSquare = IconClass("fa fa-lastfm-square")
    val leanpub = IconClass("fa fa-leanpub")
    val linkedin = IconClass("fa fa-linkedin")
    val linkedinSquare = IconClass("fa fa-linkedin-square")
    val linode = IconClass("fa fa-linode")
    val linux = IconClass("fa fa-linux")
    val maxcdn = IconClass("fa fa-maxcdn")
    val meanpath = IconClass("fa fa-meanpath")
    val medium = IconClass("fa fa-medium")
    val meetup = IconClass("fa fa-meetup")
    val mixcloud = IconClass("fa fa-mixcloud")
    val modx = IconClass("fa fa-modx")
    val odnoklassniki = IconClass("fa fa-odnoklassniki")
    val odnoklassnikiSquare = IconClass("fa fa-odnoklassniki-square")
    val opencart = IconClass("fa fa-opencart")
    val openid = IconClass("fa fa-openid")
    val opera = IconClass("fa fa-opera")
    val optinMonster = IconClass("fa fa-optin-monster")
    val pagelines = IconClass("fa fa-pagelines")
    val paypal = IconClass("fa fa-paypal")
    val piedPiper = IconClass("fa fa-pied-piper")
    val piedPiperAlt = IconClass("fa fa-pied-piper-alt")
    val piedPiperPp = IconClass("fa fa-pied-piper-pp")
    val pinterest = IconClass("fa fa-pinterest")
    val pinterestP = IconClass("fa fa-pinterest-p")
    val pinterestSquare = IconClass("fa fa-pinterest-square")
    val productHunt = IconClass("fa fa-product-hunt")
    val qq = IconClass("fa fa-qq")
    val quora = IconClass("fa fa-quora")
    val ra = IconClass("fa fa-ra")
    val ravelry = IconClass("fa fa-ravelry")
    val rebel = IconClass("fa fa-rebel")
    val reddit = IconClass("fa fa-reddit")
    val redditAlien = IconClass("fa fa-reddit-alien")
    val redditSquare = IconClass("fa fa-reddit-square")
    val renren = IconClass("fa fa-renren")
    val resistance = IconClass("fa fa-resistance")
    val safari = IconClass("fa fa-safari")
    val scribd = IconClass("fa fa-scribd")
    val sellsy = IconClass("fa fa-sellsy")
    val shareAlt = IconClass("fa fa-share-alt")
    val shareAltSquare = IconClass("fa fa-share-alt-square")
    val shirtsinbulk = IconClass("fa fa-shirtsinbulk")
    val simplybuilt = IconClass("fa fa-simplybuilt")
    val skyatlas = IconClass("fa fa-skyatlas")
    val skype = IconClass("fa fa-skype")
    val slack = IconClass("fa fa-slack")
    val slideshare = IconClass("fa fa-slideshare")
    val snapchat = IconClass("fa fa-snapchat")
    val snapchatGhost = IconClass("fa fa-snapchat-ghost")
    val snapchatSquare = IconClass("fa fa-snapchat-square")
    val soundcloud = IconClass("fa fa-soundcloud")
    val spotify = IconClass("fa fa-spotify")
    val stackExchange = IconClass("fa fa-stack-exchange")
    val stackOverflow = IconClass("fa fa-stack-overflow")
    val steam = IconClass("fa fa-steam")
    val steamSquare = IconClass("fa fa-steam-square")
    val stumbleupon = IconClass("fa fa-stumbleupon")
    val stumbleuponCircle = IconClass("fa fa-stumbleupon-circle")
    val superpowers = IconClass("fa fa-superpowers")
    val telegram = IconClass("fa fa-telegram")
    val tencentWeibo = IconClass("fa fa-tencent-weibo")
    val themeisle = IconClass("fa fa-themeisle")
    val trello = IconClass("fa fa-trello")
    val tripadvisor = IconClass("fa fa-tripadvisor")
    val tumblr = IconClass("fa fa-tumblr")
    val tumblrSquare = IconClass("fa fa-tumblr-square")
    val twitch = IconClass("fa fa-twitch")
    val twitter = IconClass("fa fa-twitter")
    val twitterSquare = IconClass("fa fa-twitter-square")
    val usb = IconClass("fa fa-usb")
    val viacoin = IconClass("fa fa-viacoin")
    val viadeo = IconClass("fa fa-viadeo")
    val viadeoSquare = IconClass("fa fa-viadeo-square")
    val vimeo = IconClass("fa fa-vimeo")
    val vimeoSquare = IconClass("fa fa-vimeo-square")
    val vine = IconClass("fa fa-vine")
    val vk = IconClass("fa fa-vk")
    val wechat = IconClass("fa fa-wechat")
    val weibo = IconClass("fa fa-weibo")
    val weixin = IconClass("fa fa-weixin")
    val whatsapp = IconClass("fa fa-whatsapp")
    val wikipediaW = IconClass("fa fa-wikipedia-w")
    val windows = IconClass("fa fa-windows")
    val wordpress = IconClass("fa fa-wordpress")
    val wpbeginner = IconClass("fa fa-wpbeginner")
    val wpexplorer = IconClass("fa fa-wpexplorer")
    val wpforms = IconClass("fa fa-wpforms")
    val xing = IconClass("fa fa-xing")
    val xingSquare = IconClass("fa fa-xing-square")
    val yCombinator = IconClass("fa fa-y-combinator")
    val yCombinatorSquare = IconClass("fa fa-y-combinator-square")
    val yahoo = IconClass("fa fa-yahoo")
    val yc = IconClass("fa fa-yc")
    val ycSquare = IconClass("fa fa-yc-square")
    val yelp = IconClass("fa fa-yelp")
    val yoast = IconClass("fa fa-yoast")
    val youtube = IconClass("fa fa-youtube")
    val youtubePlay = IconClass("fa fa-youtube-play")
    val youtubeSquare = IconClass("fa fa-youtube-square")
    val ambulance = IconClass("fa fa-ambulance")
    val hSquare = IconClass("fa fa-h-square")
    val heart = IconClass("fa fa-heart")
    val heartO = IconClass("fa fa-heart-o")
    val heartbeat = IconClass("fa fa-heartbeat")
    val hospitalO = IconClass("fa fa-hospital-o")
    val medkit = IconClass("fa fa-medkit")
    val plusSquare = IconClass("fa fa-plus-square")
    val stethoscope = IconClass("fa fa-stethoscope")
    val userMd = IconClass("fa fa-user-md")
    val wheelchair = IconClass("fa fa-wheelchair")
    val wheelchairAlt = IconClass("fa fa-wheelchair-alt")
    val cog = IconClass("fa fa-cog fa-spinner")
    val gear = IconClass("fa fa-gear fa-spinner")
    val refresh = IconClass("fa fa-refresh fa-spinner")
    val spinner = IconClass("fa fa-spinner fa-spinner")

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

