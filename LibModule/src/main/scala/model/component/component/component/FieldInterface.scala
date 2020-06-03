package model.component.component.component

import model.component.PlayerInterface
import model.component.component.component.component.{Cell, Direction, Weapon}


trait FieldInterface {
    val matrix: Array[Array[Cell]]

    def getCellAtPosition(tupel: (Int, Int)): CellInterface
    def setInitial(): FieldInterface
    def setFlag(player: Int, row: Int, col: Int): FieldInterface
    def getAmountOfNinjaRows(): Int
    def getPosition(n1: NinjaInterface): (Int, Int)
    def isNinjaOfPlayerAtPosition(player: PlayerInterface, row: Int, col: Int): Boolean
    def walkNinja(ninja: NinjaInterface, direction: Direction.direction): FieldInterface
    def -(pos: (Int,Int)): FieldInterface
    def +(ninja: NinjaInterface, pos: (Int,Int)): FieldInterface
    def fight(n1: NinjaInterface, n2: NinjaInterface): NinjaInterface
    def weaponWeight(w1: Weapon.weapon, w2: Weapon.weapon): Boolean
    def walkAtCellPossible(row: Int, col: Int, direction: Direction.direction): Boolean
    def addDirection(base: (Int, Int), amount: (Int, Int)): (Int, Int)
    def inBounds(tuple: (Int, Int)): Boolean
}
