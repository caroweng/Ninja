package database.mongo

import database.DaoInterface
import model.DeskInterface
import org.mongodb.scala._
import _root_.model.component.PlayerInterface
import _root_.model.component.component.component.Player

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, FiniteDuration}

class MongoDB extends DaoInterface {
    val DURATION: FiniteDuration = Duration.fromNanos(1000000000)
    // To directly connect to the default server localhost on port 27017
    val mongoClient: MongoClient = MongoClient("mongodb://mymongo:27017")
    val database: MongoDatabase = mongoClient.getDatabase("ninja")
    val playerCollection: MongoCollection[Document] = database.getCollection("players")
    val deskCollection: MongoCollection[Document] = database.getCollection("desk")

    override def putGameInDb(deskAsJsonString: String): Boolean = {
        Await.result(deskCollection.insertOne(Document("desk" -> deskAsJsonString)).toFuture(), DURATION)
        true
    }

    override def getGameFromDb(): Option[String] = {
        val x = Await.result(deskCollection.find().toFuture(), DURATION)
        val desk: Seq[String] = x
            .map(doc => doc.get("desk"))
            .map(desk => desk.getOrElse(return None).asString().getValue)

        desk.headOption
    }
}