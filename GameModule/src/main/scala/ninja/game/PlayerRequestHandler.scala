package ninja.game

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import model.component.component.component.{Player, StateOfPlayer}
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor, Future}

class PlayerRequestHandler extends SprayJsonSupport with DefaultJsonProtocol {
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

    implicit val enumConverter = new EnumJsonConverter(StateOfPlayer)
    implicit val itemFormat: RootJsonFormat[Player] = jsonFormat3(Player)


    def changeTurns(): (Player, Player) = {
        implicit val system: ActorSystem = ActorSystem()
        implicit val materializer: ActorMaterializer = ActorMaterializer()
        implicit val executionContext: ExecutionContextExecutor = system.dispatcher

//        val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = "http://localhost:9000/players/changeTurn"))
        val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = "http://myplayer:9000/players/changeTurn"))
        val k: HttpResponse = Await.result(responseFuture, 7.second)
        val l: Future[(Player,Player)] = Unmarshal(k.entity).to[(Player,Player)]
        val players: (Player,Player) = Await.result(l, 7.second)
        players
    }

    def setName(name: String, id: String): Player = {
        implicit val system: ActorSystem = ActorSystem()
        implicit val materializer: ActorMaterializer = ActorMaterializer()
        implicit val executionContext: ExecutionContextExecutor = system.dispatcher

        val params = Map(("name", name), ("id", id))

//        val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = Uri("http://localhost:9000/players/setName").withQuery(Query(params))))
        val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = Uri("http://myplayer:9000/players/setName").withQuery(Query(params))))

        val k: HttpResponse = Await.result(responseFuture, 7.second)
        val l: Future[Player] = Unmarshal(k.entity).to[Player]
        val player: Player = Await.result(l, 7.second)
        player
    }


}
