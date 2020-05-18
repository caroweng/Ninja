package player

case class Player(name: String, state: StateOfPlayer.stateOfPlayer, id: Int) extends PlayerInterface {
  def changeState(newState: StateOfPlayer.stateOfPlayer): PlayerInterface = this.copy(state = newState)
  def setName(newName: String): PlayerInterface = this.copy(name = newName)
}
