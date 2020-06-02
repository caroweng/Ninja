package ninja.model.component.component.component

import scala.util.Try


trait CellInterface {
    val optNinja: Option[NinjaInterface]

    def exists(): Boolean
    def getNinja(): Try[NinjaInterface]
    def removeNinja(): CellInterface

}
