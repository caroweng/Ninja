package ninja

import com.google.inject.Guice
import controller.ControllerInterface
import controller.component.UpdateEvent
import ninja.view.{Gui, Tui}

import scala.io.StdIn.readLine

object NinjaGame {

  val injector = Guice.createInjector(new NinjaModule)
  val controller = injector.getInstance(classOf[ControllerInterface])


  controller.newGame()


  val tui = new Tui(controller)
  val gui = new Gui(controller)
  controller.publish(new UpdateEvent)

  def main(args: Array[String]): Unit= {
    val input: String = ""
    do {
      val input = readLine()
      tui.input(input)
    } while (input != "q")
  }

}
