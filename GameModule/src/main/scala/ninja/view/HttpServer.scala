package ninja.view

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import ninja.controller.ControllerInterface
import ninja.controller.component.State
import ninja.model.component.component.component.component.Direction
import akka.http.scaladsl.server.{Route, StandardRoute}


import scala.util.matching.Regex

class HttpServer(controller: ControllerInterface) {
    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val route: Route = get {
        pathSingleSlash {
            complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, "<h1>Ninja</h1>"))
        } ~
            path("ninja" / "undo") {
                controller.undo
                gametoHtml
            } ~
            path("ninja" / "redo") {
                controller.redo
                gametoHtml
            } ~
            path("ninja" / "store") {
                controller.storeFile
                gametoHtml
            } ~
            path("ninja" / "load") {
                controller.loadFile
                gametoHtml
            } ~
            path("ninja" / "next") {
                controller.changeTurns()
                gametoHtml
            } ~
            path("addPlayers")(get(parameter('name) { (name) =>
                controller.setName(name)
                gametoHtml
            })) ~
            path("ninja" / "flag")(get(parameter('row, 'col) { (row, col) =>
                val currentRow: Int = row.toInt
                val currentCol: Int = col.toInt
                controller.setFlag(currentRow, currentCol)
                gametoHtml
            }))
        path("ninja" / "walk")(get(parameter('row, 'col, 'dir) { (row, col, dir) =>
            val currentRow: Int = row.toInt
            val currentCol: Int = col.toInt
            val toDir: Direction.direction = controller.getDirection(dir)
            controller.walk(currentRow, currentCol, toDir)
            gametoHtml
        }))
    }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    def unbind = {
        bindingFuture
            .flatMap(_.unbind()) // trigger unbinding from the port
            .onComplete(_ => system.terminate()) // and shutdown when done
    }

    def gametoHtml: StandardRoute = {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>HTWG Sudoku</h1>" + controller.gameToHtml))
    }

}