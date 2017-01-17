package aps.front

import aps.*
import aps.front.testutils.*
import org.w3c.dom.Storage
import kotlin.browser.window

object TestUtils {
    fun initNewBrowser(o: TestScenarioBuilder, fillStorageLocal: (TypedStorageLocal) -> Unit) {
        o.act {
            val fakeStorageLocal = FakeStorageLocal()

            val tsl = TypedStorageLocal(fakeStorageLocal)
            fillStorageLocal(tsl)
            Globus.browser = Browser(
                typedStorageLocal = tsl
            )

            ExternalGlobus.storageLocalForStaticContent = object:IStorage {
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
        o.act {Globus.location.pushState(null, "", "http://aps-ua-writer.local:3022$path")}
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

}













