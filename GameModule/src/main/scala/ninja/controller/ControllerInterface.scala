package ninja.controller

import ninja.controller.component.State
import ninja.model.DeskInterface
import ninja.model.component.component.component.FieldInterface
import ninja.model.component.component.component.component.Direction
import player.PlayerInterface

import scala.swing.Publisher

trait ControllerInterface extends Publisher {
    var desk: DeskInterface
    var state: State.state

    def newDesk(player1: PlayerInterface, player2: PlayerInterface, field: FieldInterface): DeskInterface
    def newGame(): DeskInterface
    def currentPlayer: PlayerInterface
    def getDirection(input: String): Direction.direction
    def setName(name: String): State.state
    def setFlag(row: Int, col: Int): State.state
    def wonOrTurn(input: String): State.state
    def walk(row: Int, col: Int, d: Direction.direction): State.state
    def changeTurns(): State.state
    def loadFile: State.state
    def storeFile: State.state
    def switchState(newState: State.state): State.state
    def undo: State.state
    def redo: State.state
}
