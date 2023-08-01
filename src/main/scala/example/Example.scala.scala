package example

import scala.scalajs.js
import scala.scalajs.js.annotation.*
import org.scalajs.dom
import com.raquo.laminar.api.L.{*, given}

import org.scalajs.dom
import scala.util.Random
import com.raquo.airstream.ownership.OneTimeOwner

@js.native @JSImport("/DVD_logo.png", JSImport.Default)
val dvdLogo: String = js.native

case class Box(x: Int, y: Int, hue: Double, ax: Int, ay: Int)

@main
def Example(): Unit =
  val width = dom.window.innerWidth
  val height = dom.window.innerHeight

  val (w, h) = (512, 261)
  val boxVar = Var(initial =
    Box(
      Random.between(0, width - w).toInt,
      Random.between(0, height - h).toInt,
      0,
      1,
      1
    )
  )

  def collidesV(box: Box): Boolean = box.x match {
    case x if x < 0         => true
    case x if x > width - w => true
    case _                  => false
  }

  def collidesH(box: Box): Boolean = box.y match {
    case y if y < 0          => true
    case y if y > height - h => true
    case _                   => false
  }

  def bounceV(box: Box): Box = {
    box.copy(ax = -box.ax)
  }

  def bounceH(box: Box): Box = {
    box.copy(ay = -box.ay)
  }

  val updateBox = (b: Box) => {
    val hue = (b.hue + 0.1) % 360

    val (ax, ay) = b match {
      case box if collidesV(box) => (bounceV(box).ax, box.ay)
      case box if collidesH(box) => (box.ax, bounceH(box).ay)
      case otherwise             => (otherwise.ax, otherwise.ay)
    }

    b.copy(x = b.x + ax, y = b.y + ay, hue = hue, ax = ax, ay = ay)
  }

  val tickObserver = Observer[Unit] { _ => boxVar.update(updateBox) }
  val tickStream = EventStream
    .periodic(5)
    .mapToUnit

  tickStream.addObserver(tickObserver)(
    OneTimeOwner(() => println("double free"))
  )

  val rootElement = div(
    div(
      a(
        href := "https://github.com/windymelt/laminar-exercise",
        target := "_blank",
        img(
          src := dvdLogo,
          cls := Seq("logo", "vanilla"),
          alt := "DVD logo",
          onClick.mapToUnit --> tickObserver,
          styleAttr <-- boxVar.signal.map(b =>
            s"filter: hue-rotate(${b.hue}deg); position: absolute; top: ${b.y}px; left: ${b.x}px"
          )
        )
      )
    )
  )
  val container = dom.document.querySelector("#app")
  render(container, rootElement)
end Example
