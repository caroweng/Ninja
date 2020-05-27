package ninja.model

import ninja.model.component.Desk
import ninja.model.component.component.component.component._
import org.scalatest.{Matchers, WordSpec}
import player.{Player, StateOfPlayer}

class DeskSpec extends WordSpec with Matchers{
    "A desk" when {
        val player1: Player = Player("helen", StateOfPlayer.go, 1)
        val player2: Player =  Player("caro", StateOfPlayer.pause, 1)
        val field: Field = Field(Array.ofDim[Cell](6,6))
        val desk : Desk = component.Desk(field, player1, player2).setNewGame()
        val desk5 = Desk

        "be a desk" in {
            desk5 should be(Desk)
        }

        "be constructed with" in{
            desk.player1 should be (Player("helen", StateOfPlayer.go, 1))
            desk.player2 should be (Player("caro", StateOfPlayer.pause, 1))
        }

        "set new game" in {
            val newDesk = desk.setNewGame()
//            newDesk.field should be(Field)
        }

//        "change turns" in {
//            val newDesk = changeTurnsRequest()
//            newDesk.player1.state should be(desk.player2.state)
//        }

        "win a game" in {
            desk.win(0, 1, Direction.down) should be(false)
        }

    }
}
