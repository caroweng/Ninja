package ninja.controller.component

import java.nio.file.{Files, Paths}

import com.google.inject.{Guice, Inject}
import ninja.NinjaModule
import ninja.controller.ControllerInterface
import ninja.model.component.Desk
import ninja.model.component.component.component.{CellInterface, FieldInterface}
import ninja.model.component.component.component.component.{Direction, Weapon}
import ninja.model.fileIO.FileIOInterface
import ninja.model.fileIO.xml.FileIO
import ninja.model.{component, _}
import ninja.util.UndoManager
import ninja.view.Tui
import play.api.libs.json.JsObject
import player.{PlayerInterface, StateOfPlayer}

import scala.swing.Publisher
import scala.swing.event.Event
import scala.util.{Failure, Success}

class Controller @Inject()(var desk: DeskInterface) extends ControllerInterface with Publisher {
    var state: State.state = State.INSERTING_NAME_1
    private val undoManager: UndoManager = new UndoManager();
    private val fileIO = new FileIO()


    def newDesk(player1: PlayerInterface, player2: PlayerInterface, field: FieldInterface): DeskInterface = {
        desk = Desk(field, player2, player1)
        publish(new UpdateEvent)
        desk
    }

    def newGame(): DeskInterface = {
        desk = desk.setNewGame()
        publish(new UpdateEvent)
        desk
    }

    def currentPlayer: PlayerInterface = if (desk.player1.state == StateOfPlayer.go) desk.player1 else desk.player2

    def setName(name: String): State.state = {
        if (state == State.INSERTING_NAME_1) {
            desk = desk.copyWithNewPlayer(1, currentPlayer.setName(name))
            desk = desk.changeTurns()
            switchState(State.INSERTING_NAME_2)
        } else {
            desk = desk.copyWithNewPlayer(2, currentPlayer.setName(name))
            desk = desk.changeTurns()
            switchState(State.SET_FLAG_1)
        }
    }

    def setFlag(row: Int, col: Int): State.state = {
        if (state == State.SET_FLAG_1) {
            if (desk.field.isNinjaOfPlayerAtPosition(desk.player1, row, col)) {
                desk = desk.copyWithNewField(desk.field.setFlag(desk.player1.id, row, col))
                desk = desk.changeTurns()
                return switchState(State.SET_FLAG_2)
            }
            switchState(State.SET_FLAG_1_FAILED)
        } else {
            if (desk.field.isNinjaOfPlayerAtPosition(desk.player2, row, col)) {
                desk = desk.copyWithNewField(desk.field.setFlag(desk.player2.id, row, col))
                desk = desk.changeTurns()
                return switchState(State.TURN)
            }
            switchState(State.SET_FLAG_2_FAILED)
        }
    }

    def wonOrTurn(input: String): State.state = {
        val dir: Direction.direction = getDirection(input)
        val row: Int = input.split(" ")(1).toInt
        val col: Int = input.split(" ")(2).toInt

        if (desk.field.walkAtCellPossible(row, col, dir) && desk.win(row, col, dir)) {
            switchState(State.WON)
        } else {
            walk(row, col, dir)
        }
    }

    def getDirection(input: String): Direction.direction = {
        input.split(" ")(3) match {
            case "down" => Direction.down
            case "up" => Direction.up
            case "left" => Direction.left
            case "right" => Direction.right
        }
    }

    def walk(row: Int, col: Int, d: Direction.direction): State.state = {
        undoManager.doStep(new WalkCommand(row, col, d, this))
    }

    def changeTurns(): State.state = {
        desk = desk.changeTurns()
        switchState(State.TURN)
    }

    override def storeFile: State.state = {
        fileIO.save(desk, state)
        val oldState = state
        switchState(State.STORE_FILE)
        switchState(oldState)
    }

    override def loadFile: State.state = {
        desk = fileIO.load
        switchState(State.LOAD_FILE)
        switchState(State.TURN)
    }

    def switchState(newState: State.state): State.state = {
        state = newState
        println("switch state")
        publish(new UpdateEvent)
        state
    }

    def undo: State.state = {
        val newState = undoManager.undoStep
        if(newState.isDefined)
            newState.get
        else
            state
    }

    def redo: State.state = {
        val newState = undoManager.redoStep
        if(newState.isDefined)
            newState.get
        else
            state
    }

    def gameToHtml: String = {
        "<p  style=\"font-family:'Lucida Console', monospace\"> " + deskToString.replace("\n","<br>").replace("  "," _") +"</p>"
    }

    def deskToString: String = {
        val rows: Int= desk.field.matrix.length
        val lineseparator: String = "  +" + ("----+") * rows + "\n"
        val line: String = (" |" + "   " )*rows + " |\n"
        var box: String = "\nrow 0  | 1  | 2  | 3  | 4  | 5" + "\n" + ( lineseparator + "n" +line ) * rows + lineseparator

        for (i <- desk.field.matrix.indices) {
            box = box.replaceFirst("n", i.toString)
            for (j <- desk.field.matrix.indices)
                box = box.replaceFirst("    ", this.cellToString(currentPlayer, i, j))
        }
        box
    }

    def cellToString(curPlayer: PlayerInterface, row: Int, col: Int): String ={
        var str: String = ""
        val cell: CellInterface = desk.field.getCellAtPosition(row, col)

        val tryNinja = cell.getNinja()
        tryNinja match {
            case Success(ninja) =>
                if(ninja.playerId == curPlayer.id) {
                    if (ninja.playerId == currentPlayer.id) str = " 1" else str = " 2"
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

class UpdateEvent extends Event