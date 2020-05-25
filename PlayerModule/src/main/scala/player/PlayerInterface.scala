package player

trait PlayerInterface {
    val name: String
    val state: StateOfPlayer.stateOfPlayer
    val id: Int

    def changeState(newState: StateOfPlayer.stateOfPlayer): PlayerInterface
    def setName(newName: String): PlayerInterface
}