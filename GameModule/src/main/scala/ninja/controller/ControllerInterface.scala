package ninja.controller

import model.DeskInterface
import model.component.PlayerInterface
import model.component.component.component.FieldInterface
import model.component.component.component.component.Direction
import ninja.controller.component.State

import scala.swing.Publisher

trait ControllerInterface extends Publisher {
    var desk: DeskInterface
    var state: State.state

    def newDesk(player1: PlayerInterface, player2: PlayerInterface, field: FieldInterface): DeskInterface
    def newGame(): DeskInterface
    def currentPlayer: PlayerInterface
    def setName(name: String): State.state
    def setFlag(row: Int, col: Int): State.state
    def wonOrTurn(input: String): State.state
    def walk(row: Int, col: Int, d: Direction.direction): State.state
    def changeTurns(): State.state
    def loadFile: State.state
    def loadGameFromRelDB: State.state
    def loadGameFromMongoDB: State.state
    def storeFile: State.state
    def storeGameInRelDB: State.state
    def storeGameInMongoDB: State.state
    def switchState(newState: State.state): State.state
    def undo: State.state
    def redo: State.state
}
