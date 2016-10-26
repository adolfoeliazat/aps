/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 *
 * SHIT IN THIS FILE IS GENERATED
 */

import aps.*
import aps.front.*

fun urlLink(title: String,
            url: String,
            delayActionForFanciness: Boolean = false,
            blinkOpts: dynamic = null,

            // attrs
            tame: String? = null,
            tamy: String? = null,
            shame: String? = null,
            shamy: String? = null,
            tamyShamy: String? = null,
            controlTypeName: String? = null,
            id: String? = null,
            tattrs: Json? = null,
            noStateContributions: Boolean? = null,
            className: String? = null,

            // style
            marginTop: Any? = null,
            marginBottom: Any? = null,
            paddingBottom: Any? = null,
            padding: Any? = null,
            color: Any? = null,
            backgroundColor: Any? = null,
            borderBottom: String? = null,
            textAlign: String? = null,
            fontWeight: String? = null,
            display: String? = null,
            justifyContent: String? = null
): ToReactElementable {
    return urlLink(
        title = title,
        url = url,
        delayActionForFanciness = delayActionForFanciness,
        blinkOpts = blinkOpts,

        attrs = Attrs(
            tame = tame,
            tamy = tamy,
            shame = shame,
            shamy = shamy,
            tamyShamy = tamyShamy,
            controlTypeName = controlTypeName,
            id = id,
            tattrs = tattrs,
            noStateContributions = noStateContributions,
            className = className
        ),

        style = Style(
            marginTop = marginTop,
            marginBottom = marginBottom,
            paddingBottom = paddingBottom,
            padding = padding,
            color = color,
            backgroundColor = backgroundColor,
            borderBottom = borderBottom,
            textAlign = textAlign,
            fontWeight = fontWeight,
            display = display,
            justifyContent = justifyContent
        )
    )
}
