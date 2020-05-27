package player

import akka.http.scaladsl.server.Directives.{complete, get, pathPrefix}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

class EnumJsonConverter[T <: scala.Enumeration](enu: T) extends RootJsonFormat[T#Value] {
    override def write(obj: T#Value): JsValue = JsString(obj.toString)

    override def read(json: JsValue): T#Value = {
        json match {
            case JsString(txt) => enu.withName(txt)
            case somethingElse => throw DeserializationException(s"Expected a value from enum $enu instead of $somethingElse")
        }
    }
}

// collect your json format instances into a support trait:
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val enumConverter = new EnumJsonConverter(StateOfPlayer)
    implicit val itemFormat: RootJsonFormat[Player] = jsonFormat3(Player)
}

object Server extends App with JsonSupport {

    implicit val actorSystem: ActorSystem = ActorSystem("AkkaHTTPExampleServices")
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    var player1: PlayerInterface = Player("", StateOfPlayer.go, 1);
    var player2: PlayerInterface = Player("", StateOfPlayer.pause, 2);

    lazy val playerRoutes: Route = pathPrefix("players") {
        path("changeTurn") {
            get {
                val playerInterface: PlayerInterface = player1.changeState()
                val playerInterface2: PlayerInterface = player2.changeState()
                player1 = playerInterface
                player2 = playerInterface2
                complete {
                    (Player(playerInterface.name, playerInterface.state, playerInterface.id),
                    Player(playerInterface2.name, playerInterface2.state, playerInterface2.id))
                }
            }
        } ~
        path("setName") {
            get {
                parameters('name.as[String], 'id.as[Int]) { (name, id) =>
                    complete {
                        if(id == 1) {
                            val playerI: PlayerInterface = player1.setName(name)
                            player1 = playerI
                            Player(playerI.name, playerI.state, playerI.id)
                        } else {
                            val playerI = player2.setName(name)
                            player2 = playerI
                            Player(playerI.name, playerI.state, playerI.id)
                        }
                    }
                }
            }
        }
    }

    val route: Route = concat(playerRoutes)

    Http().bindAndHandle(route, "localhost", 8080)
    Await.result(actorSystem.whenTerminated, Duration.Inf)
}
