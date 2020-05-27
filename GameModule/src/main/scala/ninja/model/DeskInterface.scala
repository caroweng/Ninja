package ninja.model

import ninja.model.component.component.component.component.Direction.direction
import ninja.model.component.component.component.FieldInterface
import player.PlayerInterface

trait DeskInterface {

    val field: FieldInterface
    val player1: PlayerInterface
    val player2: PlayerInterface

    def setNewGame(): DeskInterface
    def win(row: Int, col: Int, d: direction): Boolean
    def copyWithNewPlayer(playerId: Int, newPlayer: PlayerInterface): DeskInterface
    def copyWithNewField(field: FieldInterface): DeskInterface
    def copyDesk(): DeskInterface
}
