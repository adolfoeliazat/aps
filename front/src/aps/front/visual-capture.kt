@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import into.kommon.*
import jquery.jq
import kotlin.browser.document
import kotlin.browser.window
import kotlin.properties.Delegates.notNull

val visualShitCaptured = ResolvableShit<VisualShitCapturedMessageData>()
private var shitID by notNull<String>()

external interface VisualShitCapturedMessageData {
    val dataURL: String
}

fun visualShitCaptured(data: VisualShitCapturedMessageData) {
    visualShitCaptured.resolve(data)
}

fun captureVisualShit(id: String): Promise<VisualShitCapturedRequest.Response> = async {
    check(window.devicePixelRatio == 1.25 && window.innerWidth == 1008) {
        "I am designed for window.devicePixelRatio = 1.25 && window.innerWidth == 1008. " +
        "Otherwise there are tiny little differences in rendering of, for example, rounded corners, etc."
    }

    var documentHeight by notNull<Double>()
    var documentHeightPhysicalDouble by notNull<Double>()
    var isHeightGood by notNull<Boolean>()

    fun determineHeight(original: Boolean = false) {
        documentHeight = document.documentElement!!.asDynamic().offsetHeight
        documentHeightPhysicalDouble = documentHeight.toPhysicalPixelsDouble()
        isHeightGood = Math.floor(documentHeightPhysicalDouble) == Math.ceil(documentHeightPhysicalDouble)
        if (original) {
            clog("Original: documentHeight = $documentHeight; documentHeightPhysicalDouble = $documentHeightPhysicalDouble; isHeightGood = $isHeightGood")
        }
    }

    determineHeight(original = true)
    if (!isHeightGood) {
        for (extraBottomMargin in 1..10) {
            jqbody.css("margin-bottom", "${extraBottomMargin}px")
            determineHeight()
            if (isHeightGood)
                break
        }
    }
    check(isHeightGood) {"Fucky document height"}

    val documentHeightPhysical: Int = documentHeight.toPhysicalPixels()
    val windowHeight: Double = window.asDynamic().innerHeight
    val windowHeightPhysical: Int = windowHeight.toPhysicalPixels()
    val topNavbarHeightPhysical: Int = const.topNavbarHeight.toPhysicalPixels()
    val scrollStepPhysical: Int = windowHeightPhysical - topNavbarHeightPhysical
    clog("documentHeight = $documentHeight; documentHeightPhysicalDouble = $documentHeightPhysicalDouble; documentHeightPhysical = $documentHeightPhysical; windowHeight = $windowHeight; windowHeightPhysical = $windowHeightPhysical; topNavbarHeightPhysical = $topNavbarHeightPhysical; scrollStepPhysical = $scrollStepPhysical")

    byid(const.elementID.dynamicFooter).css("display", "none")
    val origScrollY = window.scrollY

    val drawPurpleLines = false
    if (drawPurpleLines) {
        jqbody.append("<div id='${const.elementID.cutLineContainer}'></div>")
        var yPhysical = topNavbarHeightPhysical
        while (yPhysical < documentHeightPhysical) {
            byid(const.elementID.cutLineContainer).append(
                "<div class='${css.test.cutLine}' style='top: ${yPhysical.toLayoutPixels()}px; height: ${1.0 / window.devicePixelRatio}px'></div>")
            byid(const.elementID.cutLineContainer).append(
                "<div class='${css.test.cutLine}' style='top: ${(yPhysical + scrollStepPhysical - 1).toLayoutPixels()}px; height: ${1.0 / window.devicePixelRatio}px'></div>")
            yPhysical += scrollStepPhysical
        }
    }
    // await(tillHourPasses())

    val shots = mutableListOf<BrowserShot>()

    while (true) {
        if (shots.size == 10) bitch("Too many fucking chunks to shoot")

        val requestedYPhysical = shots.size * scrollStepPhysical
        val requestedY = requestedYPhysical.toLayoutPixels()
        window.scroll(0.0, requestedY)
        await(delay(100)) // Fucking Chrome...
        clog("Shooting at $requestedY (window.scrollY = ${window.scrollY}; requestedYPhysical = $requestedYPhysical)")

        visualShitCaptured.reset()
        shitID = id
        window.postMessage(json("type" to "captureVisualShit"), "*")
        val data = await(visualShitCaptured.promise)
        shots += BrowserShot()-{o->
            o.dataURL = data.dataURL
            o.windowScrollY = window.scrollY
            o.windowScrollYPhysical = window.scrollY.toPhysicalPixels()
        }

        val oldY = window.scrollY
        window.scroll(0.0, oldY + 1)
        val dy = Math.abs(oldY - window.scrollY)
        clog("oldY = $oldY; window.scrollY = ${window.scrollY}; dy = $dy")
        if (dy < 0.001)
            break
    }

    val res = await(send(VisualShitCapturedRequest()-{o->
        o.id = shitID
        o.shots = shots
        o.devicePixelRatio = window.devicePixelRatio
        o.headerHeight = const.topNavbarHeight
        o.contentWidth = jq("#topNavbarContainer > nav > .container").outerWidth()
        o.contentLeft = jq("#topNavbarContainer > nav > .container").offset().left.toDouble()
        o.documentHeightPhysical = documentHeightPhysical
    }))
    clog("Sent captured shit to backend")

    jqbody.css("margin-bottom", "0px")
    byid(const.elementID.cutLineContainer).remove()
    window.scroll(0.0, origScrollY)
    byid(const.elementID.dynamicFooter).css("display", "")

    res
}














