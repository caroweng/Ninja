package ninja.model

import ninja.model.component.component.component.component.Weapon
import org.scalatest.{Matchers, WordSpec}

class WeaponSpec extends WordSpec with Matchers {
    "A weapon" when {
        val weapon0: Weapon.weapon = Weapon.createWeapon(0)
        val weapon1 = Weapon.createWeapon(1)
        val weapon2 = Weapon.createWeapon(2)
        val weapon3 = Weapon.flag
        val weapon5 = Weapon

        "be a weapon" in {
            weapon5 should be(Weapon)
        }

        "be constructed as" in {
            weapon0 should be(Weapon.scissors)
            weapon1 should be(Weapon.rock)
            weapon2 should be(Weapon.paper)
        }
        "be presented as string" in {
            weapon0.toString should be("s")
            weapon1.toString should be("r")
            weapon2.toString should be("p")
            weapon3.toString should be("f")
        }
    }
}