/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 *
 * SHIT IN THIS FILE IS GENERATED
 */

package aps.front

import aps.*
import aps.front.*
import org.w3c.dom.events.*

operator fun ElementBuilderFactory.invoke (
    // @Mix Attrs
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
    onClick: ((MouseEvent) -> Unit)? = null,
    onClicka: ((MouseEvent) -> Promise<Unit>)? = null,
    onMouseEnter: ((MouseEvent) -> Unit)? = null,
    onMouseEntera: ((MouseEvent) -> Promise<Unit>)? = null,
    onMouseLeave: ((MouseEvent) -> Unit)? = null,
    onMouseLeava: ((MouseEvent) -> Promise<Unit>)? = null,
    // @Mix Style
    marginTop: Any? = null,
    marginLeft: Any? = null,
    marginBottom: Any? = null,
    paddingBottom: Any? = null,
    padding: Any? = null,
    color: Any? = null,
    backgroundColor: Any? = null,
    borderBottom: String? = null,
    textAlign: String? = null,
    fontWeight: String? = null,
    display: String? = null,
    justifyContent: String? = null,
    whiteSpace: String? = null,
    fontFamily: String? = null,
    block: ((ElementBuilder) -> Unit)? = null
): ElementBuilder = invoke(
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
        className = className,
        onClick = onClick,
        onClicka = onClicka,
        onMouseEnter = onMouseEnter,
        onMouseEntera = onMouseEntera,
        onMouseLeave = onMouseLeave,
        onMouseLeava = onMouseLeava
    ),
    style = Style(
        marginTop = marginTop,
        marginLeft = marginLeft,
        marginBottom = marginBottom,
        paddingBottom = paddingBottom,
        padding = padding,
        color = color,
        backgroundColor = backgroundColor,
        borderBottom = borderBottom,
        textAlign = textAlign,
        fontWeight = fontWeight,
        display = display,
        justifyContent = justifyContent,
        whiteSpace = whiteSpace,
        fontFamily = fontFamily
    ),
    block = block
)

fun urlLink (
    url: String,
    delayActionForFanciness: Boolean = false,
    blinkOpts: dynamic = null,
    // @Mix LinkParams
    content: ToReactElementable? = null,
    title: String? = null,
    // @Mix Attrs
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
    onClick: ((MouseEvent) -> Unit)? = null,
    onClicka: ((MouseEvent) -> Promise<Unit>)? = null,
    onMouseEnter: ((MouseEvent) -> Unit)? = null,
    onMouseEntera: ((MouseEvent) -> Promise<Unit>)? = null,
    onMouseLeave: ((MouseEvent) -> Unit)? = null,
    onMouseLeava: ((MouseEvent) -> Promise<Unit>)? = null,
    // @Mix Style
    marginTop: Any? = null,
    marginLeft: Any? = null,
    marginBottom: Any? = null,
    paddingBottom: Any? = null,
    padding: Any? = null,
    color: Any? = null,
    backgroundColor: Any? = null,
    borderBottom: String? = null,
    textAlign: String? = null,
    fontWeight: String? = null,
    display: String? = null,
    justifyContent: String? = null,
    whiteSpace: String? = null,
    fontFamily: String? = null
): ToReactElementable = urlLink(
    url = url,
    delayActionForFanciness = delayActionForFanciness,
    blinkOpts = blinkOpts,
    linkParams = LinkParams(
        content = content,
        title = title
    ),
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
        className = className,
        onClick = onClick,
        onClicka = onClicka,
        onMouseEnter = onMouseEnter,
        onMouseEntera = onMouseEntera,
        onMouseLeave = onMouseLeave,
        onMouseLeava = onMouseLeava
    ),
    style = Style(
        marginTop = marginTop,
        marginLeft = marginLeft,
        marginBottom = marginBottom,
        paddingBottom = paddingBottom,
        padding = padding,
        color = color,
        backgroundColor = backgroundColor,
        borderBottom = borderBottom,
        textAlign = textAlign,
        fontWeight = fontWeight,
        display = display,
        justifyContent = justifyContent,
        whiteSpace = whiteSpace,
        fontFamily = fontFamily
    )
)

fun link (
    // @Mix LinkParams
    content: ToReactElementable? = null,
    title: String? = null,
    // @Mix Attrs
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
    onClick: ((MouseEvent) -> Unit)? = null,
    onClicka: ((MouseEvent) -> Promise<Unit>)? = null,
    onMouseEnter: ((MouseEvent) -> Unit)? = null,
    onMouseEntera: ((MouseEvent) -> Promise<Unit>)? = null,
    onMouseLeave: ((MouseEvent) -> Unit)? = null,
    onMouseLeava: ((MouseEvent) -> Promise<Unit>)? = null,
    // @Mix Style
    marginTop: Any? = null,
    marginLeft: Any? = null,
    marginBottom: Any? = null,
    paddingBottom: Any? = null,
    padding: Any? = null,
    color: Any? = null,
    backgroundColor: Any? = null,
    borderBottom: String? = null,
    textAlign: String? = null,
    fontWeight: String? = null,
    display: String? = null,
    justifyContent: String? = null,
    whiteSpace: String? = null,
    fontFamily: String? = null
): ToReactElementable = link(
    params = LinkParams(
        content = content,
        title = title
    ),
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
        className = className,
        onClick = onClick,
        onClicka = onClicka,
        onMouseEnter = onMouseEnter,
        onMouseEntera = onMouseEntera,
        onMouseLeave = onMouseLeave,
        onMouseLeava = onMouseLeava
    ),
    style = Style(
        marginTop = marginTop,
        marginLeft = marginLeft,
        marginBottom = marginBottom,
        paddingBottom = paddingBottom,
        padding = padding,
        color = color,
        backgroundColor = backgroundColor,
        borderBottom = borderBottom,
        textAlign = textAlign,
        fontWeight = fontWeight,
        display = display,
        justifyContent = justifyContent,
        whiteSpace = whiteSpace,
        fontFamily = fontFamily
    )
)

