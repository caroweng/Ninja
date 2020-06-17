package ninja.game

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import ninja.controller.ControllerInterface
import ninja.controller.component.State
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import model.component.PlayerInterface
import model.component.component.component.CellInterface
import model.component.component.component.component.{Direction, Weapon}

import scala.util.{Failure, Success}


class PlayerRequestHandlerWui(controller: ControllerInterface) {
    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val ninjaRoute: Route = pathPrefix("ninja") {
        path("undo")(get(complete(handle(controller.undo)))) ~
            path("redo")(get(complete(handle(controller.redo)))) ~
            path("store")(get(complete(handle(controller.storeFile)))) ~
            path("next")(get(complete(handle(controller.changeTurns())))) ~
            path("walk")(get(parameter('row, 'col, 'dir) { (row, col, dir) =>
                val currentRow: Int = row.toInt
                val currentCol: Int = col.toInt
                val toDir: Direction.direction = getDirectionFromString(dir)
                complete(handle(controller.walk(currentRow, currentCol, toDir)))
            })) ~
            path("flag")(get(parameter('row, 'col) { (row, col) =>
                val currentRow: Int = row.toInt
                val currentCol: Int = col.toInt
                complete(handle(controller.setFlag(currentRow, currentCol)))
            }))
    }

    val startMenuRoute: Route = pathPrefix("startmenu") {
        path("load")(get(complete(handle(controller.loadFile)))) ~
            path("addPlayer")(get(parameter('name) { name =>
                complete(handle(controller.setName(name)))
            }))
    }

    val stateRoute: Route = pathPrefix("state") {
        complete(HttpResponse(OK, entity = gameAsString))
    }

    val startRoute: Route = pathPrefix("") {
        complete(HttpResponse(OK, entity = "Ninja"))
    }

    private def handle(state: State.state): HttpResponse =
        if (state == controller.state) {
            HttpResponse(OK, entity = gameAsString)
        } else {
            HttpResponse(InternalServerError, entity = s"Wrong server state. Use endpoint only when in ${state} state")
        }

    def getDirectionFromString(input: String): Direction.direction = {
        input match {
            case "down" => Direction.down
            case "up" => Direction.up
            case "left" => Direction.left
            case "right" => Direction.right
        }
    }

    val bindingFuture = Http().bindAndHandle(concat(ninjaRoute, startMenuRoute, startRoute, stateRoute), "localhost", 8000)

    def unbind = {
        bindingFuture
            .flatMap(_.unbind()) // trigger unbinding from the port
            .onComplete(_ => system.terminate()) // and shutdown when done
    }

    def gameAsString(): String = {
        controller.state match {
            case State.INSERTING_NAME_1 => "player.Player 1 insert your name! name <name>"
            case State.INSERTING_NAME_2 => "player.Player 2 insert your name! name <name>"
            case State.SET_FLAG_1 => controller.currentPlayer.name + " set your flag! f <row> <col>" + deskToString
            case State.SET_FLAG_1_FAILED => "Flag could not be set, try again!"
            case State.SET_FLAG_2 => controller.currentPlayer.name + " set your flag! f <row> <col>" + deskToString
            case State.SET_FLAG_2_FAILED => "Flag could not be set, try again!"
            case State.NAME_REGEX_INCORRECT_1 => "Eingabe war nicht korrekt, bitte in der Form \"name [Name]\" eingeben"
            case State.NAME_REGEX_INCORRECT_2 => "Eingabe war nicht korrekt, bitte in der Form \"name [Name]\" eingeben"
            case State.WALK_REGEX_INCORRECT => "Eingabe war nicht korrekt, bitte in der Form \"w Zahl Zahl Richtung\" eingeben"
            case State.WALKED_INPUT_INCORRECT => "Eingabe war nicht korrekt, bitte <y> für nächsten Spieler oder <u> um Zug rückgängig machen eingeben"
            case State.FLAG_REGEX_INCORRECT_1 => "Eingabe war nicht korrekt, bitte in der Form \"f Zahl Zahl\" eingeben"
            case State.FLAG_REGEX_INCORRECT_2 => "Eingabe war nicht korrekt, bitte in der Form \"f Zahl Zahl\" eingeben"
            case State.No_NINJA_OR_NOT_VALID => "Not valid, try again!"
            case State.DIRECTION_DOES_NOT_EXIST => "Direction not valid, try again"
            case State.TURN => controller.currentPlayer.name + " it's your turn! w <row> <col> <up|down|left|right>" + deskToString
            case State.WALKED => controller.currentPlayer.name + " press <y> for next player or <u> for undo." + deskToString
            case State.WON => controller.currentPlayer.name + " you win!"
            case State.STORE_FILE => "Game stored."
            case State.LOAD_FILE => "Game loaded."
            case State.COULD_NOT_LOAD_FILE => "File could not be loaded."
        }
    }

    def deskToString: String = {
        val rows: Int = controller.desk.field.matrix.length
        val lineseparator: String = "  +" + ("----+") * rows + "\n"
        val line: String = (" |" + "   ") * rows + " |\n"
        var box: String = "\nrow 0  | 1  | 2  | 3  | 4  | 5" + "\n" + (lineseparator + "n" + line) * rows + lineseparator

        for (i <- controller.desk.field.matrix.indices) {
            box = box.replaceFirst("n", i.toString)
            for (j <- controller.desk.field.matrix.indices)
                box = box.replaceFirst("    ", this.cellToString(controller.currentPlayer, i, j))
        }
        box
    }

    def cellToString(curPlayer: PlayerInterface, row: Int, col: Int): String = {
        var str: String = ""
        val cell: CellInterface = controller.desk.field.getCellAtPosition(row, col)

        val tryNinja = cell.getNinja()
        tryNinja match {
            case Success(ninja) =>
                if (ninja.playerId == curPlayer.id) {
                    if (ninja.playerId == controller.currentPlayer.id) str = " 1" else str = " 2"
                    ninja.weapon match {
                        case Weapon.flag => str.concat("f ")
                        case Weapon.rock => str.concat("r ")
                        case Weapon.paper => str.concat("p ")
                        case Weapon.scissors => str.concat("s ")
                    }
                } else {
                    " xx "
                }
            case Failure(e) => "[  ]"
        }
    }
}