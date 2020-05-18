package ninja.model.component.component.component.component

object Direction extends Enumeration {
  type direction = Value
  val right, left, up, down = Value

  def getDirectionIndex(direction: Direction.direction): (Int, Int) = {
    direction match{
      case Direction.right => (0,1)
      case Direction.left => (0,-1)
      case Direction.up => (-1,0)
      case Direction.down => (1,0)
    }
  }
}
