import aps.ReactElement

class DivBuilder {
  var _color: String = _
  var _width: String = _

  def color(x: String) = {_color = x; this}
  def width(x: String) = {_width = x; this}

  override def toString: String = {
    s"div(color=${_color}, width=${_width})"
  }

  def div(build: DivBuilder => Any): ReactElement = {
    null
  }
}

def div(build: DivBuilder => Any): ReactElement = {
  val builder = new DivBuilder()
  println("begin")
  build(builder)
  println("end: " + builder)
  null
}

div {

}



"ok"

