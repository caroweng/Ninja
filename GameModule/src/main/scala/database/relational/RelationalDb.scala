package database.relational

import database.DaoInterface
import model.DeskInterface
import model.component.{Desk, PlayerInterface}

object RelationalDb extends DaoInterface {
    private val playerMapping: PlayerMapping.type = PlayerMapping
    private val gameMapping: GameMapping.type = GameMapping

    def putPlayerInDb(desk: DeskInterface): Option[DeskInterface] = {
        try {
            val tryPlayer = playerMapping.putPlayerInDb(desk.player1)
            if(tryPlayer) {
                println("Player was saved in database.")
                Some(desk)
            } else {
                println("Error: Player could not be saved in database.")
                None
            }
        } catch {
            case _: Throwable => None
        }
    }

    def getPlayerFromDb(): Option[PlayerInterface] = {
        try {
            playerMapping.getPlayerFromDb()
        } catch {
            case _: Throwable => None
        }
    }

    override def putGameInDb(desk: String): Boolean = {
        try {
            val tryGame = gameMapping.putGameInDb(desk)
            if (tryGame) {
                println("Game was saved in database.")
                true
            } else  {
                println("Error: Game could not be saved in database.")
                false
            }
        } catch {
            case _: Throwable => false
        }

    }

    override def getGameFromDb(): Option[String] = {
        try {
            gameMapping.getGameFromDb()
        } catch {
            case _: Throwable => None
        }
    }

}