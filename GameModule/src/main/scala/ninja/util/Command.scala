package ninja.util

import controller.component.State

trait Command {
    def doStep: State.state
    def undoStep: State.state
    def redoStep: State.state
}
