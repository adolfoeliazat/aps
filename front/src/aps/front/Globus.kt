package aps.front

import aps.*
import aps.front.testutils.*
import into.kommon.*
import org.w3c.dom.Storage
import kotlin.browser.localStorage
import kotlin.browser.window
import kotlin.properties.Delegates.notNull
import kotlin.reflect.KProperty

//val storageLocal: StorageLocal get() = Globus.browser.storageLocal
val typedStorageLocal: TypedStorageLocal get() = Globus.browser.typedStorageLocal

@native interface IExternalGlobus {
    fun displayInitialShit()
    var storageLocalForStaticContent: IStorage
    var LANG: String
    var MODE: String
    var DB: String
}
@JsName("global")
@native val ExternalGlobus: IExternalGlobus = noImpl

enum class Mode { DEBUG, PROD }

object Globus {
    val version = "____VERSION____"
    var lastAttemptedRPCName: String? = null
    var rootRedisLogMessageID: String? = null
    val lang: Language get() = Language.valueOf(ExternalGlobus.LANG)
    val mode by lazy {Mode.valueOf(ExternalGlobus.MODE)}
    var worldMaybe: World? = null
    val realLocation = RealLocationProxy()
    var location: LocationProxy = realLocation

    val clientKind: ClientKind get() {
        return when {
            location.host.contains("customer") -> ClientKind.CUSTOMER
            location.host.contains("writer") -> ClientKind.WRITER
            else -> wtf("clientKind")
        }
    }

    val world get() = worldMaybe!!

    val realStorageLocal = object:StorageLocal {
        override val length get() = localStorage.length
        override fun key(index: Int) = localStorage.key(index)
        override fun clear() = localStorage.clear()
        override fun getItem(key: String): String? = localStorage.getItem(key)
        override fun setItem(key: String, value: String) = localStorage.setItem(key, value)
        override fun removeItem(key: String) = localStorage.removeItem(key)
    }
    val realTypedStorageLocal = TypedStorageLocal(realStorageLocal)
    var browser = Browser(typedStorageLocal = realTypedStorageLocal)
    var isTest = false
}

class Browser(val typedStorageLocal: TypedStorageLocal)


interface StorageLocal {
    val length: Int
    fun clear()
    fun getItem(key: String): String?
    fun setItem(key: String, value: String)
    fun removeItem(key: String)
    fun key(index: Int): String?
}


class TypedStorageLocal(val store: StorageLocal) {
    var token by StringValue()
    var reloadTest by BooleanValue()
    var dontScrollOnTestPassed by BooleanValue()
    var DebugWordMentionFinderPage_inputText by StringValue()
    var lastTestURL by StringValue()
    var lastTestSuiteURL by StringValue()
    var oneOffTestOptionsTemplateTitle by StringValue()

    inner class StringValue {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): String? =
            store.getItem(property.name)

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
            if (value == null) store.removeItem(property.name)
            else store.setItem(property.name, value)
        }
    }

    inner class IntValue {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Int? {
            return store.getItem(property.name)?.let {parseInt(it)}
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int?) {
            if (value == null) store.removeItem(property.name)
            else store.setItem(property.name, value.toString())
        }
    }

    inner class BooleanValue {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean =
            store.getItem(property.name).let {it == "true" || it == "yes"}

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
            store.setItem(property.name, if (value) "true" else "false")
        }
    }

    fun clear() = store.clear()
}

// -------------------- OLD SHIT --------------------

class BrowserOld(val name: String) {
    var storageLocalItems = js("({})")
    var topNavbarElement: dynamic = null
    var rootElement: dynamic = null
    lateinit var ui: World
    lateinit var impl: World

//    val typedStorageLocal = TypedStorageLocal(_storageLocal)

    val storageLocal = object : StorageLocal {
        override val length get() = JSObject.keys(storageLocalItems).size

        override fun key(index: Int) = JSObject.keys(storageLocalItems)[index]

        override fun removeItem(key: String) {
            throw UnsupportedOperationException("Implement me, please, fuck you")
        }

        override fun clear() {
            storageLocalItems = js("({})")
        }

        override fun getItem(key: dynamic): dynamic {
            return storageLocalItems[key]
        }

        override fun setItem(key: dynamic, value: dynamic) {
            storageLocalItems[key] = value
            global.localStorage.setItem(key, value) // TODO:vgrechka @kill
        }
    }
}

// TODO:vgrechka Use class delegate
class RealStorageLocal : StorageLocal {
    override val length get() = window.localStorage.length

    override fun key(index: Int) = window.localStorage.key(index)

    override fun clear() {
        window.localStorage.clear()
    }

    override fun getItem(key: String): String? {
        return window.localStorage.getItem(key)
    }

    override fun setItem(key: String, value: String) {
        window.localStorage.setItem(key, value)
    }

    override fun removeItem(key: String) {
        window.localStorage.removeItem(key)
    }
}


















