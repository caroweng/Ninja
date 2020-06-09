package ninja.controller.component

import java.nio.file.{Files, Paths}

import com.google.inject.{Guice, Inject}
import database.DaoInterface
import database.relational.RelationalDb
import model.DeskInterface
import model.component.component.component.component.Direction
import model.component.{Desk, PlayerInterface}
import model.component.component.component.{FieldInterface, StateOfPlayer}
import ninja.controller.ControllerInterface
import ninja.fileIO.json.FileIO
import ninja.game.PlayerRequestHandler
import ninja.util.UndoManager

import scala.swing.Publisher
import scala.swing.event.Event

class Controller @Inject()(var desk: DeskInterface) extends ControllerInterface with Publisher {
    var state: State.state = State.INSERTING_NAME_1
    private val undoManager: UndoManager = new UndoManager();
    private val playerRequestHandler: PlayerRequestHandler = new PlayerRequestHandler();
    private val fileIO = new FileIO()
    private val database: DaoInterface = RelationalDb;


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
//            desk = desk.copyWithNewPlayer(1, currentPlayer.setName(name))
            desk = setNameRequest(name, 1)
//            desk = changeTurnsRequest()
            switchState(State.INSERTING_NAME_2)
        } else {
//            desk = desk.copyWithNewPlayer(2, currentPlayer.setName(name))
            desk = setNameRequest(name, 2)
//            desk = changeTurnsRequest()
            switchState(State.SET_FLAG_1)
        }
    }

    def changeTurnsRequest(): DeskInterface = {
        println(desk.player1)
        println(desk.player2)
        val players: (PlayerInterface, PlayerInterface) = playerRequestHandler.changeTurns()
        desk = desk.copyWithNewPlayer(players._1.id, players._1)
        desk = desk.copyWithNewPlayer(players._2.id, players._2)
        println(players)
        desk
    }

    def setNameRequest(name: String, id: Int): DeskInterface = {
        val player: PlayerInterface = playerRequestHandler.setName(name, id.toString)
//        while(player==null) {
//            Thread.sleep(100)
//        }
        desk.copyWithNewPlayer(player.id, player)
    }

    def setFlag(row: Int, col: Int): State.state = {
        if (state == State.SET_FLAG_1) {
            if (desk.field.isNinjaOfPlayerAtPosition(desk.player1, row, col)) {
                desk = desk.copyWithNewField(desk.field.setFlag(desk.player1.id, row, col))
                desk = changeTurnsRequest()
                return switchState(State.SET_FLAG_2)
            }
            switchState(State.SET_FLAG_1_FAILED)
        } else {
            if (desk.field.isNinjaOfPlayerAtPosition(desk.player2, row, col)) {
                desk = desk.copyWithNewField(desk.field.setFlag(desk.player2.id, row, col))
                desk = changeTurnsRequest()
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
        desk = changeTurnsRequest()
        switchState(State.TURN)
    }

    override def storeFile: State.state = {
        fileIO.save(desk, state)
        val oldState = state
        switchState(State.STORE_FILE)
        switchState(oldState)
    }

    override def storeGameInDB: State.state = {
        database.putGameInDb(fileIO.deskToString(desk, state).toString())
        val oldState = state
        switchState(State.STORE_FILE)
        switchState(oldState)
    }

    override def loadFile: State.state = {
        desk = fileIO.load
        switchState(State.LOAD_FILE)
        switchState(State.TURN)
    }

    override def loadGameFromDB: State.state = {
        val deskAsString = database.getGameFromDb().get
        desk = fileIO.deskFromJson(deskAsString)
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

}

class UpdateEvent extends Event