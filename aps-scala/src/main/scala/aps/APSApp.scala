package aps

import scala.scalajs.js
import scala.scalajs.js.{Any, Dynamic, JSApp}
import scala.scalajs.js.annotation.JSExport
import js.Dynamic.{literal => dli}
import scala.scalajs.js.RegExp.ExecResult
//import scala.language.implicitConversions

object APSApp extends JSApp {
  val global: js.Dynamic = js.Dynamic.global
  var jsuit: js.Dynamic = null
  var art: js.Dynamic = null
  var byid: js.Dynamic = null

  def main(): Unit = {
    println("Fuck off")
  }

  @JSExport
  def igniteScalaPieceOfShit(_jsuit: js.Dynamic): Unit = {
    println("----- Hello, bitchy user! -----")
    jsuit = _jsuit
    byid = jsuit.byid; art = jsuit.art

    jsuit.art.openTestPassedPane = openTestPassedPane _
  }

//  implicit def dynamicToBoolean(d: js.Dynamic): Boolean = d.asInstanceOf[Boolean]
//  implicit def dynamicToString(d: js.Dynamic): String = d.asInstanceOf[String]


  def openTestPassedPane(spec: js.Dynamic) = {
    val scenario = spec.scenario

    val testPassedPane = jsuit.statefulElement(dli(ctor = (update: js.Dynamic) => {
      var scenarioName: String = scenario.name.asInstanceOf[String]
      val links = js.Array[Dynamic]()

      val m: ExecResult = js.RegExp("\\s+([0-9a-z]{8})-([0-9a-z]{4})-([0-9a-z]{4})-([0-9a-z]{4})-([0-9a-z]{12})$").exec(scenarioName)
      if (m != null) {
        scenarioName = scenarioName.slice(0, m.index)
        links.push(jsuit.OpenSourceCodeLink(dli(where = dli($tag = m(0).asInstanceOf[String].trim()), style = dli(color = jsuit.WHITE))))
      }
      if (!js.isUndefined(art.actionPlaceholderTag)) {
        links.push(jsuit.marginateLeft(10, jsuit.OpenSourceCodeLink(dli(where = dli($tag = art.actionPlaceholderTag), style = dli(color = jsuit.WHITE)))))
      }

      val uq = jsuit.getURLQueryBeforeRunningTest()
      if (js.isUndefined(uq.scrollToBottom) || uq.scrollToBottom == "yes" || uq.scrollToBottom == "success") {
        global.requestAnimationFrame(()=> global.document.body.scrollTop = 99999)
      }

      dli(
        render = ()=> scenarioName match {
          case null => null
          case _ => jsuit.diva(dli(noStateContributions = true,
            style = dli(
              backgroundColor = jsuit.GREEN_700, color = jsuit.WHITE,
              marginTop = 10, padding = "10px 10px", textAlign = "center", fontWeight = "bold"
            )),

            jsuit.diva(dli(style = dli(paddingBottom = 10)),
              scenarioName,
              jsuit.divaArray(dli(style = dli(display = "flex", justifyContent = "center")), links)),

          jsuit.diva(dli(style = dli(background = jsuit.WHITE, color = jsuit.BLACK_BOOT, fontWeight = "normal", textAlign = "left", padding = 5)),
            art.renderStepDescriptions())
          )
        }
      )
    }))

    jsuit.debugPanes.set(dli(name = "openTestPassedPane", parentJqel = byid("underFooter"), element =
      global.React.createElement("div", dli(), testPassedPane.element)))
  }

}

// In fucking browser: aps.APSApp().igniteScalaPieceOfShit()

//@js.native
//trait JSArt extends js.Object {
//  var openTestPassedPane: js.Any = js.native
//}

//@js.native
//trait JSUIT extends js.Object {
//  val debugPanes: js.Dynamic = js.native
//  var art: js.Dynamic = js.native
//}





