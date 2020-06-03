package model.component.component.component.component

object Weapon extends Enumeration {
    type weapon = Value
    val scissors: Weapon.Value = Value("s")
    val rock: Weapon.Value = Value("r")
    val paper: Weapon.Value = Value("p")
    val flag: Weapon.Value = Value("f")

    def createWeapon(number: Int): Weapon.weapon = {
        number match {
            case 0 => Weapon.scissors
            case 1 => Weapon.rock
            case 2 => Weapon.paper
        }
    }

    def stringToWeapon(string: String): Weapon.weapon = {
        string match {
            case "s" => Weapon.scissors
            case "p" => Weapon.paper
            case "r" => Weapon.rock
            case "f" => Weapon.flag
        }
    }

    override def toString(): String = {
        "weapon"
    }
}
