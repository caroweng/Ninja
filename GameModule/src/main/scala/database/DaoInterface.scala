
package database

import model.DeskInterface
import model.component.PlayerInterface

trait DaoInterface {

    def putPlayerInDb(desk: DeskInterface): Option[DeskInterface]

    def getPlayerFromDb(): Option[PlayerInterface]

    def putGameInDb(desk: String): Boolean

    def getGameFromDb(): Option[String]

}