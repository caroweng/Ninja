package ninja

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model._
import akka.stream.{ActorMaterializer, Materializer}
import player.{EnumJsonConverter, Player, StateOfPlayer}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}
import akka.http.scaladsl.unmarshalling.Unmarshal

import scala.concurrent.Await
import scala.concurrent.duration._
import akka.http.scaladsl.model.Uri.Query

import scala.async.Async.{async, await}
import scala.xml.Null


class PlayerRequestHandler extends SprayJsonSupport with DefaultJsonProtocol {

    implicit val enumConverter = new EnumJsonConverter(StateOfPlayer)
    implicit val itemFormat: RootJsonFormat[Player] = jsonFormat3(Player)


    def changeTurns(): (Player, Player) = {
        implicit val system: ActorSystem = ActorSystem()
        implicit val materializer: ActorMaterializer = ActorMaterializer()
        implicit val executionContext: ExecutionContextExecutor = system.dispatcher

        val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = "http://localhost:8080/players/changeTurn"))
        val k: HttpResponse = Await.result(responseFuture, 1.second)
        val l: Future[(Player,Player)] = Unmarshal(k.entity).to[(Player,Player)]
        val players: (Player,Player) = Await.result(l, 1.second)
        players
    }

    def setName(name: String, id: String): Player = {
        implicit val system: ActorSystem = ActorSystem()
        implicit val materializer: ActorMaterializer = ActorMaterializer()
        implicit val executionContext: ExecutionContextExecutor = system.dispatcher

        val params = Map(("name", name), ("id", id))

        val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = Uri("http://localhost:8080/players/setName").withQuery(Query(params))))

        val k: HttpResponse = Await.result(responseFuture, 1.second)
        val l: Future[Player] = Unmarshal(k.entity).to[Player]
        val player: Player = Await.result(l, 1.second)
        player
    }


}
