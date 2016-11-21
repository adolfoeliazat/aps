package aps.front

import into.kommon.global
import org.w3c.dom.Storage
import kotlin.browser.localStorage
import kotlin.browser.window
import kotlin.reflect.KProperty

//val storageLocal: StorageLocal get() = Globus.browser.storageLocal
val typedStorageLocal: TypedStorageLocal get() = Globus.browser.typedStorageLocal

@JsName("global")
@native object ExternalGlobus {
    fun displayInitialShit(): Unit = noImpl
//    fun makeSignInNavbarLinkVisible(): Unit = noImpl
    var storageLocalForStaticContent: Storage = noImpl
}

object Globus {
    var lastAttemptedRPCName: String? = null

    val realStorageLocal = object:StorageLocal {
        override fun clear() = localStorage.clear()
        override fun getItem(key: String): String? = localStorage.getItem(key)
        override fun setItem(key: String, value: String) = localStorage.setItem(key, value)
        override fun removeItem(key: String) = localStorage.removeItem(key)
    }

    var browser = Browser(typedStorageLocal = TypedStorageLocal(realStorageLocal))
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

    inner class StringValue {
        operator fun getValue(thisRef: Any?, property: KProperty<*>) = store.getItem(property.name)

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
            if (value == null) store.removeItem(property.name)
            else store.setItem(property.name, value)
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
