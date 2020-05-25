package player

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{complete, get, pathPrefix}
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import play.api.libs.json.{Json, Writes}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.parsing.json.JSON

import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

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

//        implicit val anyValWriter = Writes[Player] (a => {
//            Json.toJson(a.name, a.state, a.id)
//        })

    lazy val apiRoutes: Route = pathPrefix("players") {
        get {
            complete {
                Player("helen", StateOfPlayer.go, 1)
//                Json.stringify(Json.toJson( Player("helen", StateOfPlayer.go, 1)))
            }
        }
    }

    Http().bindAndHandle(apiRoutes, "localhost", 8080)
    Await.result(actorSystem.whenTerminated, Duration.Inf)
}
