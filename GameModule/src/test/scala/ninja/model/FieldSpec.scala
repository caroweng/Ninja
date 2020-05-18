package ninja.model

import ninja.model.component.component.component.NinjaInterface
import ninja.model.component.component.component.component._
import org.scalatest.{Matchers, WordSpec}
import player.{Player, StateOfPlayer}

import scala.util.{Failure, Success}

class FieldSpec extends WordSpec with Matchers {
    "A field" when {
        var matrix = Array.ofDim[Cell](3, 3)
        val ninja: Ninja = Ninja(Weapon.paper, 1, 1)
        matrix(0)(0) = Cell(Some(ninja))
        val field = Field(matrix).setInitial()
        val field5 = Field

        "be a field" in {
            field5 should be(Field)
        }

        "be set with ninjas" in {
            val field2 = field.setInitial()

            field2.getCellAtPosition(0,0).getNinja() match {
                case Success(n1) => n1.playerId should be(1)
                case Failure(e) =>
            }

            field2.getCellAtPosition(2,2).getNinja() match {
                case Success(n2) => n2.playerId should be(2)
                case Failure(e) =>
            }
        }

        "be set empty with initial ninja rows" in {
            val field2 = field.setInitial()
            field2.getCellAtPosition(0,0).optNinja should not be(None)
        }

        "be set with a flag" in {
            val newField = field.setFlag(1, 0, 0)
            newField.getCellAtPosition((0, 0)).optNinja.get.weapon should be(Weapon.flag)
        }


        "have a ninja at position" in {
            val ninja: NinjaInterface = field.getCellAtPosition(0, 0).optNinja.get
            field.getPosition(ninja) should be(0, 0)
        }

//        "not have a ninja at position" in {
//            val ninja = Ninja(Weapon.paper, 1, 20)
//            an [NoSuchElementException] should be thrownBy field.getPosition(ninja)
//        }

        "have a ninja of player.Player at position" in {
            val player = Player("caro", StateOfPlayer.pause, 1)
            field.isNinjaOfPlayerAtPosition(player, 0, 0) should be(true)
        }

        "not have a ninja of player.Player at position" in {
            val player = Player("caro", StateOfPlayer.pause, 2)
            field.isNinjaOfPlayerAtPosition(player, 0, 0) should be(false)
        }

//        "be a correct walk" in {
//            val ninja = field.matrix(0)(1).getNinja()
//            val newField = field.checkWalk(ninja, Direction.down)
////            newField should not equal(field)
//            field should equal(field)
//        }

        "remove a ninja" in {
            val newField = field.-(0, 1)
            newField should not equal(field)
        }

        "add a ninja" in {
            val newNinja = Ninja(Weapon.paper, 1, 20)
            val newField = field.+(newNinja, (1, 1))
            newField should not equal(field)
        }

        "be weighted" in {
            field.weaponWeight(Weapon.rock, Weapon.paper) should be(false)
            field.weaponWeight(Weapon.scissors, Weapon.rock) should be(false)
            field.weaponWeight(Weapon.paper, Weapon.scissors) should be(false)
        }

        "fight" in {
            val ninjaRock = Ninja(Weapon.rock, 2, 2)
            field.fight(ninja, ninjaRock) should be(ninja)
        }

//        "have a cell at" in {
//            field.cellExists(0, 0, Direction.down) should be(true)
//        }
//
//        "not have a cell at" in {
//            field.cellExists(0, 0, Direction.up) should be(false)
//        }

        "add a direction" in {
            field.addDirection((0, 0), (0, 1)) should be((0, 1))
        }

    }
}