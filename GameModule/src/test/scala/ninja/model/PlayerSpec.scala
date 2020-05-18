package ninja.model

import org.scalatest.{Matchers, WordSpec}
import player.{Player, StateOfPlayer}

class PlayerSpec extends WordSpec with Matchers{
    "A player.Player" when {

        val player1 = Player("caro", StateOfPlayer.go, 1)
        val player2 = Player("helen", StateOfPlayer.pause, 2)
        val player = Player

        "be a player.Player" in {
            player should be (Player)
        }

        "be constructed with a name" in {
            player1.name should be("caro")
            player2.name should be("helen")
            player1.state should be(StateOfPlayer.go)
            player2.state should be(StateOfPlayer.pause)
        }

        "a turn gets changed" should {
            val newPlayer1 = player1.changeState(player2.state)
            val newPlayer2 = player2.changeState(player1.state)
            "have a new turn" in {
                newPlayer1.state should be(StateOfPlayer.pause)
                newPlayer2.state should be(StateOfPlayer.go)
            }
        }
    }
}
