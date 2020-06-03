package ninja.model

import ninja.model.component.component.component.component.Direction
import org.scalatest.{Matchers, WordSpec}

class DirectionSpec extends WordSpec with Matchers{
    "A direction" when {
        val direction5 = Direction
        val right = Direction.right
        val left = Direction.left
        val up = Direction.up
        val down = Direction.down

        "be a direction" in {
            direction5 should be (Direction)
        }

        "get index" in {
            Direction.getDirectionIndex(right) should be((0,1))
            Direction.getDirectionIndex(left) should be((0,-1))
            Direction.getDirectionIndex(up) should be((-1,0))
            Direction.getDirectionIndex(down) should be((1,0))
        }

    }
}
