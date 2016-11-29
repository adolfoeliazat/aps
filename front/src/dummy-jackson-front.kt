package com.fasterxml.jackson.annotation

import aps.*

annotation class JsonTypeInfoAlias(val use: JsonTypeInfo.Id, val include: JsonTypeInfo.As, val property: String)

@Dummy class JsonTypeInfo {
    enum class Id {
        NONE,
        CLASS,
        MINIMAL_CLASS,
        NAME,
        CUSTOM
    }

    enum class As {
        PROPERTY,
        WRAPPER_OBJECT,
        WRAPPER_ARRAY,
        EXTERNAL_PROPERTY,
        EXISTING_PROPERTY
    }

}




