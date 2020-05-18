package ninja.model.component

import com.google.inject.Inject
import ninja.model._
import ninja.model.component.component.component.component.Direction.direction
import ninja.model.component.component.component.component.{Direction, Weapon}
import ninja.model.component.component.component.{CellInterface, FieldInterface, NinjaInterface}
import player.{PlayerInterface, StateOfPlayer}

import scala.util.{Failure, Success}

case class Desk @Inject()(field: FieldInterface, player1: PlayerInterface, player2: PlayerInterface) extends DeskInterface {
  def setNewGame(): Desk = this.copy(field = field.setInitial())

  def win(row: Int, col: Int, d: direction): Boolean = {
    val cell: CellInterface = this.field.getCellAtPosition(this.field.addDirection((row,col), Direction.getDirectionIndex(d)))
    val tryNinja = cell.getNinja()

    tryNinja match {
      case Success(n1) => {
        val walkingNinja = field.getCellAtPosition(row, col).getNinja()
        walkingNinja match {
          case Success(n2) =>
            if(n1.weapon == Weapon.flag && n1.playerId != n2.playerId)
              true else false

          case Failure(e) => false
        }
      }
      case Failure(e) => false
    }
  }

  def changeTurns(): Desk = {
    val s1: StateOfPlayer.stateOfPlayer = player1.state
    val p1: PlayerInterface = player1.changeState(player2.state)
    val p2: PlayerInterface = player2.changeState(s1)
    this.copy(player1 = p1, player2 = p2)
  }

  def copyWithNewPlayer(playerId: Int, newPlayer: PlayerInterface): Desk =  {
    if(playerId == 1)
      copy(player1 = newPlayer)
    else
      copy(player2 = newPlayer)
  }

  def copyWithNewField(newField: FieldInterface): Desk = {
    copy(field = newField)
  }

  def copyDesk(): Desk = {
    copy()
  }
}
