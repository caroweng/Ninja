package database.relational


import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import slick.driver.H2Driver.api._


object GameMapping {

    val db = Database.forConfig("h2mem1")

    val desks = TableQuery[Desks]

    Await.result(db.run(DBIO.seq(
        desks.schema.create,
    )), Duration.Inf)

    def putGameInDb(deskAsJsonString: String): Boolean = {
        try {
            Await.result(db.run(DBIO.seq(
                desks += DBDesk(deskAsJsonString),
            )), Duration.Inf)
            true
        } catch {
            case err: Exception =>
                println("Error in database", err)
                false;
        }
    }

    def getGameFromDb(): Option[String] = {
        var desk: Option[String] = None
        Await.result(db.run(DBIO.seq(
            desks.result.map(value => {
                println(value)
                desk = Some(value.head.desk)
            }))), Duration.Inf)
        desk
    }
}



case class DBDesk(desk: String, id: Option[Int] = None)


class Desks(tag: Tag) extends Table[DBDesk](tag, "DESKS") {
    // Auto Increment the id primary key column
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    // The name can't be null
    def desk = column[String]("desk")

    // the * projection (e.g. select * ...) auto-transforms the tupled
    // column values to / from a User
    def * = (desk, id.?) <> (DBDesk.tupled, DBDesk.unapply)
}
