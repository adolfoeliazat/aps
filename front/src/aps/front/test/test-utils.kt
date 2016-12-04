package aps.front

import org.w3c.dom.Storage
import kotlin.browser.window

object TestUtils {
    fun initNewBrowser(o: TestScenarioBuilder, fillStorageLocal: (TypedStorageLocal) -> Unit) {
        o.act {
            val fakeStorageLocal = object : StorageLocal {
                val map = mutableMapOf<String, String>()
                override fun clear() = map.clear()
                override fun getItem(key: String) = map[key]
                override fun setItem(key: String, value: String) {map[key] = value}
                override fun removeItem(key: String) {map.remove(key)}
            }

            val tsl = TypedStorageLocal(fakeStorageLocal)
            fillStorageLocal(tsl)
            Globus.browser = Browser(
                typedStorageLocal = tsl
            )

            ExternalGlobus.storageLocalForStaticContent = object: Storage {
                override fun getItem(key: String) = fakeStorageLocal.getItem(key)
            }
        }
    }

    fun bootWorld(o: TestScenarioBuilder) {
        o.acta {async{
            await(World().boot())
        }}
    }

    fun pushWriterURL(o: TestScenarioBuilder, path: String) {
        o.act {window.history.pushState(null, "", "http://aps-ua-writer.local:3022$path")}
    }

    fun putTinyTestContextLabel(o: TestScenarioBuilder, text: String) {
        o.act {
            byid("tinyTextContextLabel").remove()
            jqbody.append("""
            <div id="tinyTextContextLabel" style="
                position: fixed;
                left: 50%;
                top: 0px;
                z-index: 1000000;
                font-size: 75%;
                background-color: ${Color.BROWN_300};
                color: white;
                padding: 0px 10px;
                transform: translate(-50%);
                border-bottom-left-radius: 5px;
                border-bottom-right-radius: 5px;
            ">$text</div>
        """)
        }
    }

}













