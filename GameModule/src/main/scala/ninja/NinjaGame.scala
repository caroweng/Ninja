package ninja

import com.google.inject.Guice
import ninja.controller.ControllerInterface
import ninja.controller.component.UpdateEvent
import ninja.view.{HttpServer, Gui, Tui}

import scala.io.StdIn.readLine

object NinjaGame {

  val injector = Guice.createInjector(new NinjaModule)
  val controller = injector.getInstance(classOf[ControllerInterface])


  controller.newGame()


  val tui = new Tui(controller)
  val gui = new Gui(controller)
  val webServer = new HttpServer(controller)
  controller.publish(new UpdateEvent)

  def main(args: Array[String]): Unit= {
    val input: String = ""
    do {
      val input = readLine()
      tui.input(input)
    } while (input != "q")
    webServer.unbind
  }

}
