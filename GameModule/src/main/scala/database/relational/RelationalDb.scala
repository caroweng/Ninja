package database.relational

import database.DaoInterface

object RelationalDb extends DaoInterface {
    private val gameMapping: GameMapping.type = GameMapping

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