package ninja.model

import ninja.model.component.component.component.component.{Ninja, Weapon}
import org.scalatest.{Matchers, WordSpec}
import player.{Player, StateOfPlayer}

class NinjaSpec extends WordSpec with Matchers{
  "A Ninja" when {
    "be constructed" should {
      val player1 = Player("helen", StateOfPlayer.pause, 1)
      val ninja = Ninja(Weapon.scissors, 1, 1)
      val ninja5 = Ninja

      "be a ninja" in {
        ninja5 should be (Ninja)
      }

      "have a name and a weapon" in {
        ninja.playerId should be(1)
        ninja.weapon should be(Weapon.scissors)
      }

      "change its weapon" in {
        val n2 = ninja.changeWeapon(Weapon.paper)
        n2.weapon should be(Weapon.paper)
      }
    }
  }
}