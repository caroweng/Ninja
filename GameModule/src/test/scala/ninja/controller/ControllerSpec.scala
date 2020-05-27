package ninja.controller

import ninja.controller.component.{Controller, State}
import ninja.model.component.Desk
import ninja.model.component.component.component.component._
import org.scalatest.{Matchers, WordSpec}
import player.{Player, StateOfPlayer}

import scala.util.{Failure, Success}

class ControllerSpec extends WordSpec with Matchers{
    "A ninja.controller" when {
        val player1: Player = Player("helen", StateOfPlayer.go, 1)
        val player2: Player = Player("caro", StateOfPlayer.pause, 1)
        val field: Field = Field(Array.ofDim[Cell](6, 6))
        val desk: Desk = Desk(field, player1, player2).setNewGame()
        val controller = new Controller(desk)
        controller.newGame()

        "create a new Desk" in {
            val newPlayer: Player = Player("caro2", StateOfPlayer.pause, 1)
            val newDesk = controller.newDesk(player1, newPlayer, field)
            controller.desk should be(newDesk)
            desk should not be (newDesk)
        }

//        "set a players name" in {
//            ninja.controller.switchState(State.INSERTING_NAME_1)
//            ninja.controller.currentPlayer.name should be("helen")
//            ninja.controller.setName("newName")
//            ninja.controller.currentPlayer.name should be("newName")
//            ninja.controller.desk.changeTurns()
//            ninja.controller.switchState(State.INSERTING_NAME_2)
//            ninja.controller.currentPlayer.name should be("caro")
//            ninja.controller.setName("otherName")
//            ninja.controller.currentPlayer.name should be("otherName")
//        }

        "changeTurns" in {
            controller.newGame()
            controller.desk.player1.state should be(StateOfPlayer.pause)
            controller.desk.player2.state should be(StateOfPlayer.go)
            controller.desk = controller.changeTurnsRequest()
            controller.desk.player1.state should be(StateOfPlayer.go)
            controller.desk.player2.state should be(StateOfPlayer.pause)
        }

        "set a flag" in {
            controller.newGame()
            controller.switchState(State.SET_FLAG_1)
            controller.setFlag(1,1)
            val tryNinja = controller.desk.field.getCellAtPosition(0,0).getNinja()
            tryNinja match {
                case Success(ninja) => ninja.ninjaId should be (0)
                case Failure(e) =>
            }
        }

        "switch state" in {
            controller.switchState(State.SET_FLAG_2)
            controller.state should be (State.SET_FLAG_2)
        }

        "not be a valid ninja" in {
            controller.newGame()
            controller.walk(3,3, Direction.up) should be(State.No_NINJA_OR_NOT_VALID)
        }

        "not be a valid direction" in {
            controller.newGame()
            controller.walk(0,0, Direction.up) should be(State.DIRECTION_DOES_NOT_EXIST)
        }

        "walk a ninja" in {
            controller.newGame()
            controller.walk(1,1, Direction.down) should be(State.WALKED)
        }
    }
}
