package aps

import scala.scalajs.js
import scala.scalajs.js.{Any, Array, Dynamic, JSApp, Object, native}
import scala.scalajs.js.annotation.JSExport
import js.Dynamic.{literal => dli}
import scala.collection.{SeqLike, mutable}
import scala.scalajs.js.RegExp.ExecResult

object APSApp extends JSApp {
  val global: Dynamic = Dynamic.global
  var jsuit: Dynamic = null;
  var art: Dynamic = null;
  var byid: Dynamic = null;
  var diva: Dynamic = null

  import js.JSConverters._

  def div(attrs: Map[String, Any], children: ReactElement*): ReactElement = {
    jsuit.divaArray(attrs, children).asInstanceOf[ReactElement]
  }

  @JSExport
  def igniteScalaPieceOfShit(_jsuit: Dynamic): Unit = {
    println("----- Hello, bitchy user! -----")
    jsuit = _jsuit
    byid = jsuit.byid;
    art = jsuit.art;
    diva = jsuit.diva

    jsuit.art.openTestPassedPane = openTestPassedPane _
    jsuit.art.renderStepDescriptions = renderStepDescriptions _
  }

  def openTestPassedPane(spec: Dynamic) = {
    val scenario = spec.scenario

    trait StatefulElement {
      def render: ReactElement
    }

    val testPassedPane = jsuit.statefulElement(dli(ctor = (update: Dynamic) => {
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
        global.requestAnimationFrame(() => global.document.body.scrollTop = 99999)
      }

      dli(
        render = () => scenarioName match {
          case null => null
          case _ => diva(dli(noStateContributions = true,
            style = dli(
              backgroundColor = jsuit.GREEN_700, color = jsuit.WHITE,
              marginTop = 10, padding = "10px 10px", textAlign = "center", fontWeight = "bold"
            )),

            diva(dli(style = dli(paddingBottom = 10)),
              scenarioName,
              jsuit.divaArray(dli(style = dli(display = "flex", justifyContent = "center")), links)),

            diva(dli(style = dli(background = jsuit.WHITE, color = jsuit.BLACK_BOOT, fontWeight = "normal", textAlign = "left", padding = 5)),
              art.renderStepDescriptions())
          )
        }
      )
    }))

    val tppe: ReactElement = testPassedPane.element.asInstanceOf[ReactElement]

    jsuit.debugPanes.set(dli(name = "openTestPassedPane", parentJqel = byid("underFooter"), element =
      React.createElement("div", dli(), tppe)))
  }

  def raise(msg: String): Unit = {
    jsuit.raise(msg)
    throw new Exception("Scala, I've just throwed above")
  }

  def t(x: Any): String = x.toString

  def renderStepDescriptions(): ReactElement = {
    val testInstructions = art.getTestInstructions().asInstanceOf[js.Array[Dynamic]]
    val els = mutable.Buffer[Dynamic]()
    els.append(diva(dli(style = dli(background = jsuit.GRAY_200, fontWeight = "bold")), t("Steps")))

    var stepIndex = -1
    var indent = 0
    for (instrIndex <- 0 until testInstructions.length) {
      val instrdef = testInstructions(instrIndex)

      def addLine(indent: Int,
                  stepRowStyle: Dynamic = null,
                  rulerContent: Dynamic = null,
                  lineContent: Dynamic = null,
                  actions: Seq[Dynamic] = Seq()) {

//        def div(build: => Unit): ReactElement = {
//          val attrs = mutable.Map()
//          jsuit.divaArray(attrs, shit.toJSArray)
//        }
//
//        div {
//          println("world")
//        }


        els.append(diva(dli(style = dli(marginTop = 5, display = "flex")),
          diva(dli(style = dli(fontWeight = "bold", width = 40)), rulerContent),
          // XXX This `width = 100%` is for fucking flexbox to not change `width = 40` above... http://stackoverflow.com/questions/7985021/css-flexbox-issue-why-is-the-width-of-my-flexchildren-affected-by-their-content

          jsuit.divaArray(dli(className = "showOnParentHovered-parent", style = dli(width = "100%", display = "flex").asnn(stepRowStyle)),
            ( (1 to indent map {_=> diva(dli(style = dli(width = 20, borderLeft = s"2px dotted ${jsuit.GRAY_500}")))})
              ++
              Seq(lineContent,
                diva(dli(className = "showOnParentHovered"),
                  jsuit.hor2Array(dli(style = dli(marginLeft = 8, paddingLeft = 8, borderLeft = s"2px solid ${jsuit.GRAY_500}")),
                    (actions ++ Seq(jsuit.OpenSourceCodeLink(dli(where = instrdef, style = dli(marginLeft = 20))))).toJSArray ) )
              )).toJSArray)))
      }

      Object.keys(instrdef.asInstanceOf[Object]).find(x => x(0) != '$') match {
        case None => raise(s"TWF is opcode in instruction $instrIndex")
        case Some(opcode) => {
          val instr = instrdef.selectDynamic(opcode)
          opcode match {
            case "step" => {
              val stepRowStyle = js.Dictionary().asInstanceOf[Dynamic]
              if (js.isUndefined(instr.fulfilled)) {
                stepRowStyle.opacity = 0.3
              }

              val untilParamValue = if (stepIndex == art.stepDescriptions.length.asInstanceOf[Int] - 1) "infinity" else instrIndex

              
              stepIndex += 1
              addLine(
                indent,
                stepRowStyle = stepRowStyle,
                rulerContent = ("#" + (stepIndex + 1)).asInstanceOf[Dynamic],
                lineContent = diva(dli(style = dli(display = "flex")),
                  instr.kind.asInstanceOf[String] match {
                    case "action" => jsuit.spana(dli(style = dli(marginRight = 5, padding = 3, background = jsuit.GREEN_100, fontSize = "75%")), t("Action"))
                    case "state" => jsuit.spana(dli(style = dli(marginRight = 5, padding = 3, background = jsuit.LIGHT_BLUE_100, fontSize = "75%")), t("State"))
                    case "navigation" => jsuit.spana(dli(style = dli(marginRight = 5, padding = 3, background = jsuit.BROWN_50, fontSize = "75%")), t("Navigation"))
                  },
                  jsuit.spana(dli(style = dli()), instr.long)
                ),
                actions = Seq(
                  jsuit.link(dli(title = t("Run until " + untilParamValue), onClick = () => {
                    var href = global.location.href.asInstanceOf[String]
                    href = href.replaceAll("&from[^&]*", "")
                    href = href.replaceAll("&until[^&]*", "")
                    href += "&until=" + untilParamValue
                    global.location.href = href
                  }))
                )
)
            }
            case "beginSection" => {
              addLine(indent, lineContent = diva(dli(style = dli(fontWeight = "bold")), instr.long))
              indent += 1
            }
            case "endSection" => {
              indent -= 1
            }
            case "worldPoint" => {
              addLine(
                indent,
                lineContent = diva(dli(style = dli(fontWeight = "normal", fontStyle = "italic")), "World point: " + instr.name),
                rulerContent = diva(dli(style = dli(position = "relative")),
                  jsuit.ia(dli(className = "fa fa-circle", style = dli(color = jsuit.GRAY_500))),
                  diva(dli(style = dli(width = 38, position = "absolute", left = 0, top = 9, borderTop = s"2px dotted ${jsuit.GRAY_500}")))),
                actions = Seq(
                  jsuit.link(dli(title = t("Run from"), onClick = () => {
                    var href = global.location.href
                    href = href.replace(global.RegExp("&from[^&]*"), "")
                    href = href.replace(global.RegExp("&until[^&]*"), "")
                    href += "&from=" + instr.name
                    global.location.href = href
                  }))
                )
)
            }
            case _ => ()
            
          }
        }
      }
    }

    jsuit.divaArray(dli(controlTypeName = "renderStepDescriptions", noStateContributions = true), els.toJSArray).asInstanceOf[ReactElement]
  }

  @scala.scalajs.js.annotation.JSExport
  override def main(): Unit = {}
}

@js.native
object React extends js.Object {
  def createElement(tag: String, attrs: Object with Dynamic, elements: ReactElement*): ReactElement = js.native
}

@js.native
trait ReactElement extends js.Object {
}

// In fucking browser: aps.APSApp().igniteScalaPieceOfShit()

//@js.native
//trait JSArt extends js.Object {
//  var openTestPassedPane: js.Any = js.native
//}





