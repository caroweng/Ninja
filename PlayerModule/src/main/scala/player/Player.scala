package player

case class Player(name: String, state: StateOfPlayer.stateOfPlayer, id: Int) extends PlayerInterface {
  def changeState(): PlayerInterface = {
    if(state == StateOfPlayer.go)
      this.copy(state = StateOfPlayer.pause)
    else
      this.copy(state = StateOfPlayer.go)
  }
  def setName(newName: String): PlayerInterface = this.copy(name = newName)
}
