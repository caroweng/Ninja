package ninja.model.component.component.component.component

import com.google.inject.Inject
import ninja.model.component.component.component.{CellInterface, FieldInterface, NinjaInterface}
import player.PlayerInterface

import scala.util.{Failure, Random, Success, Try}

case class Field (matrix: Array[Array[Cell]]) extends FieldInterface {

    def getCellAtPosition(tupel: (Int, Int)): CellInterface = matrix(tupel._1)(tupel._2)

    def setInitial(): Field = {
        val newMatrix: Array[Array[Cell]] = Array.ofDim[Cell](matrix.length, matrix.length)
        newMatrix.indices flatMap(i =>
            newMatrix.indices map (j =>
                newMatrix(i)(j) = Cell(None)))

        0 until this.getAmountOfNinjaRows() flatMap(row =>
            this.matrix.indices map (col => {
                val r: Random = new Random()
                val n: Int = r.nextInt(3)
                newMatrix(row)(col) = Cell(Some(Ninja(Weapon.createWeapon(n), 1, (row+2*col))))
            }))

        this.matrix.length - this.getAmountOfNinjaRows() until this.matrix.length flatMap(row =>
            this.matrix.indices map (col => {
                val r: Random = new Random()
                val n: Int = r.nextInt(3)
                newMatrix(row)(col) = Cell(Some(Ninja(Weapon.createWeapon(n), 2, (row+2*col))))
            }))

        this.copy(matrix = newMatrix)
    }

    def setFlag(player: Int, row : Int , col : Int): Field = {
        val tryNinja = this.matrix(row)(col).getNinja()
        tryNinja match {
            case Success(ninja) =>
                copy(matrix.updated(row, matrix(row).updated(col, Cell(Some(Ninja(Weapon.flag, ninja.playerId, ninja.ninjaId))))))
            case Failure(e) => this
        }
    }

    def getAmountOfNinjaRows(): Int = if (this.matrix.length / 3 < 2) 1 else 2

    def getPosition(n1: NinjaInterface): (Int, Int) = {
        val value = for{r <- matrix.indices
            c <- matrix.indices
            if matrix(r)(c).exists() && (matrix(r)(c).optNinja.getOrElse("kein Ninja") == Ninja(n1.weapon, n1.playerId, n1.ninjaId))}
             yield (r, c)
        value(0)
    }

    def isNinjaOfPlayerAtPosition(player: PlayerInterface, row: Int, col: Int): Boolean = {
        val tryNinja = getCellAtPosition(row, col).getNinja()
        tryNinja match {
            case Success(ninja) => inBounds(row, col) && matrix(row)(col).exists() && ninja.playerId == player.id
            case Failure(e) =>
                false
        }
    }

    def walkNinja(ninja: NinjaInterface, direction: Direction.direction): Field = {
        val pos: (Int, Int) = getPosition(ninja)
        val newpos: (Int, Int) = addDirection(pos, Direction.getDirectionIndex(direction))

        getCellAtPosition(newpos).optNinja match {
            case None =>  this.-(pos).+(ninja, newpos)
            case Some(n2) => this.-(pos).+(fight(ninja, n2), newpos)
        }
    }

    def -(pos: (Int,Int)): Field = copy(matrix.updated(pos._1, matrix(pos._1).updated(pos._2, Cell(None))))

    def +(ninja: NinjaInterface, pos: (Int,Int)): Field = copy(matrix.updated(pos._1, matrix(pos._1).updated(pos._2, Cell(Some(ninja)))))

    def fight(n1: NinjaInterface, n2: NinjaInterface): NinjaInterface = {
        if(n1.weapon == n2.weapon) { //???? wenn Waffen gleich Spieler neue wählen lassen
            val r: Random = new Random()
            val n: Int = r.nextInt(3)
            val newN: NinjaInterface = n1.copyWithNewWeapon(Weapon.createWeapon(n))
            if(weaponWeight(newN.weapon, n2.weapon)) return newN else return n2
        }
        if(weaponWeight(n1.weapon, n2.weapon)) n1 else n2
    }

    def weaponWeight(w1: Weapon.weapon, w2: Weapon.weapon): Boolean = {
        w1 match {
            case Weapon.rock =>
                if (w2 == Weapon.paper) false else true
            case Weapon.scissors =>
                if (w2 == Weapon.rock) false else true
            case Weapon.paper =>
                if (w2 == Weapon.scissors) false else true
        }
    }

    def walkAtCellPossible(row: Int, col: Int, direction: Direction.direction): Boolean = {
        val add1: (Int, Int) = addDirection((row, col), Direction.getDirectionIndex(direction))
        if(!inBounds(add1)) return false

        val tryWalkingNinja = getCellAtPosition(row, col).getNinja()
        tryWalkingNinja match {
            case Success(n1) =>
                val tryAttackedNinja = getCellAtPosition(add1).getNinja()
                tryAttackedNinja match {
                    case Success(n2) =>
                        n1.playerId != n2.playerId
                    case Failure(e) =>
                        true
                }
            case Failure(e1) => false
        }
    }

    def addDirection(base: (Int, Int), amount: (Int, Int)): (Int, Int) = (base._1 + amount._1, base._2 + amount._2)

    def inBounds(tuple: (Int, Int)): Boolean = tuple._1 < matrix.length && tuple._1 >= 0 && tuple._2 < matrix.length && tuple._2 >= 0
}
