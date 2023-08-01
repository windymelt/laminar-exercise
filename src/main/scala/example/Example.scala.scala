package example

import scala.scalajs.js
import scala.scalajs.js.annotation.*

import org.scalajs.dom

@js.native @JSImport("/DVD_logo.png", JSImport.Default)
val dvdLogo: String = js.native

@main
def Example(): Unit =
  dom.document.querySelector("#app").innerHTML = s"""
    <div>
      <img src="$dvdLogo" class="logo vanilla" alt="DVD logo" />
    </div>
  """
end Example
