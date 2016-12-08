package aps.front

import aps.*
import into.kommon.global
import org.w3c.dom.Storage
import kotlin.browser.localStorage
import kotlin.browser.window
import kotlin.reflect.KProperty

//val storageLocal: StorageLocal get() = Globus.browser.storageLocal
val typedStorageLocal: TypedStorageLocal get() = Globus.browser.typedStorageLocal

@native interface IExternalGlobus {
    fun displayInitialShit()
    var storageLocalForStaticContent: Storage
    var LANG: String
}
@JsName("global")
@native val ExternalGlobus: IExternalGlobus = noImpl

object Globus {
    var lastAttemptedRPCName: String? = null

    val realStorageLocal = object:StorageLocal {
        override fun clear() = localStorage.clear()
        override fun getItem(key: String): String? = localStorage.getItem(key)
        override fun setItem(key: String, value: String) = localStorage.setItem(key, value)
        override fun removeItem(key: String) = localStorage.removeItem(key)
    }

    val realTypedStorageLocal = TypedStorageLocal(realStorageLocal)

    var browser = Browser(typedStorageLocal = realTypedStorageLocal)
    var rootRedisLogMessageID: String? = null

    val lang: Language get() = Language.valueOf(ExternalGlobus.LANG)
}

class Browser(val typedStorageLocal: TypedStorageLocal)


interface StorageLocal {
    fun clear()
    fun getItem(key: String): String?
    fun setItem(key: String, value: String)
    fun removeItem(key: String)
}


class TypedStorageLocal(val store: StorageLocal) {
    var token by StringValue()
    var reloadTest by BooleanValue()
    var dontScrollOnTestPassed by BooleanValue()

    inner class StringValue {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): String? =
            store.getItem(property.name)

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
            if (value == null) store.removeItem(property.name)
            else store.setItem(property.name, value)
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

@native fun moment(ms: Double): Moment = noImpl

















