package controller.component

import ninja.model.DeskInterface
import ninja.model.component.Desk
import ninja.model.component.component.component.component.{Direction, Weapon}
import ninja.util.Command

import scala.util.{Failure, Success}

class WalkCommand (row: Int, col: Int, d: Direction.direction, controller: Controller) extends Command {
    val oldDesk: DeskInterface = controller.desk.copyDesk()
    override def doStep: State.state = {
        val cell = controller.desk.field.getCellAtPosition(row, col)
        val ninja = cell.getNinja()
        ninja match {
            case Success(n) =>
                if(n.weapon == Weapon.flag || n.playerId != controller.currentPlayer.id)
                    return controller.switchState(State.No_NINJA_OR_NOT_VALID)
                if (controller.desk.field.walkAtCellPossible(row, col, d)) {
                    controller.desk = controller.desk.copyWithNewField(controller.desk.field.walkNinja(n, d))
                    println("Finished? Press <y> for next player")
                    controller.switchState(State.WALKED)
                } else {
                    controller.switchState(State.DIRECTION_DOES_NOT_EXIST)
                }
            case Failure(e) =>
                controller.switchState(State.No_NINJA_OR_NOT_VALID)
        }
    }

    override def undoStep: State.state = {
        controller.desk = oldDesk;
        controller.switchState(State.TURN)
    }

    override def redoStep: State.state = {
        doStep
//        val cell = controller.desk.field.getCellAtPosition(row, col)
//        val ninja = cell.getNinja()
//        ninja match {
//            case Success(n) =>
//                if(n.weapon == Weapon.flag || n.playerId != controller.currentPlayer.id)
//                    return controller.switchState(State.No_NINJA_OR_NOT_VALID)
//
//                if (controller.desk.field.cellExists(row, col, d)) {
//                    controller.desk = controller.desk.copyWithNewField(controller.desk.field.walkNinja(n, d))
//                    println("Finished? Press <y> for next player")
//                    controller.switchState(State.WALKED)
//                } else {
//                    controller.switchState(State.DIRECTION_DOES_NOT_EXIST)
//                }
//            case Failure(e) =>
//                controller.switchState(State.No_NINJA_OR_NOT_VALID)
//        }
    }
}
