package ninja.model.component.component.component.component

import com.google.inject.Inject
import ninja.model.component.component.component.{CellInterface, NinjaInterface}

import scala.util.{Failure, Success, Try}

case class Cell (optNinja: Option[NinjaInterface]) extends CellInterface{
  def exists(): Boolean = optNinja.isDefined
  def getNinja(): Try[NinjaInterface] = {
    val res = Try(optNinja.get)
    res match {
      case Success(value) => Success(value)
      case Failure(exception) => Failure(exception)
    }
  }
  def removeNinja(): CellInterface = Cell(None)
}
