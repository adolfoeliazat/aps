/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import into.kommon.*
import kotlin.js.json

fun userKindIcon(kind: UserKind): ReactElement {
    return faIcon(tame="icon", marginLeft=5, marginRight=5,
        icon = when (kind) {
            UserKind.CUSTOMER -> "user"
            UserKind.WRITER -> "pencil"
            UserKind.ADMIN -> "cog"
        }).toReactElement()
}

fun renderProfile(user: UserRTO): ReactElement {
    imf("f70d80e2-1d36-44e9-a4f7-de36393619e0")
}


















