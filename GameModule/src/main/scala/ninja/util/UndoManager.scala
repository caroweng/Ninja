package ninja.util

import controller.component.State

class UndoManager {
  private var undoStack: List[Command]= Nil
  private var redoStack: List[Command]= Nil
  def doStep(command: Command): State.state = {
    undoStack = command::undoStack
    command.doStep
  }

  def undoStep: Option[State.state]  = {
    undoStack match {
      case Nil => None
      case head::stack => {
        val state = head.undoStep
        undoStack=stack
        redoStack= head::redoStack
        Some(state)
      }
    }
  }

  def redoStep: Option[State.state] = {
    redoStack match {
      case Nil => None
      case head::stack => {
        val state = head.redoStep
        redoStack=stack
        undoStack=head::undoStack
        Some(state)
      }
    }
  }
}