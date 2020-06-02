package model.component

import model.component.component.component.StateOfPlayer

trait PlayerInterface {
    val name: String
    val state: StateOfPlayer.stateOfPlayer
    val id: Int

    def changeState(): PlayerInterface
    def setName(newName: String): PlayerInterface
}
