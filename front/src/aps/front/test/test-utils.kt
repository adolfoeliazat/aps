package aps.front

import aps.*
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

    fun bootWorld(o: TestScenarioBuilder, name: String, done: (World) -> Unit) {
        o.acta {async{
            val world = World(name)
            await(world.boot())
            done(world)
        }}
    }

    fun pushWriterURL(o: TestScenarioBuilder, path: String) {
        o.act {window.history.pushState(null, "", "http://aps-ua-writer.local:3022$path")}
    }

    fun putTinyTestContextLabel(text: String) {
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

    fun checkAssertAndClearEmail(o: TestScenarioBuilder, descr: String, expectedSubject: String, expectedBody: String) {
        o.acta {debugCheckEmail()}
        o.assertMailInFooter(descr, expectedSubject, expectedBody)
        o.acta {ClearSentEmailsRequest.send()}
        o.act {debugHideMailbox()}
    }

}













