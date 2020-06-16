
package database

trait DaoInterface {

    def putGameInDb(desk: String): Boolean

    def getGameFromDb(): Option[String]

}