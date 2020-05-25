package ninja

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.scaladsl.Source
import play.api.libs.json.Json
import player.{EnumJsonConverter, Player, StateOfPlayer}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

object Test extends SprayJsonSupport with DefaultJsonProtocol{

//trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val enumConverter = new EnumJsonConverter(StateOfPlayer)
    implicit val itemFormat: RootJsonFormat[Player] = jsonFormat3(Player)
//}
    def main(args: Array[String]): Unit= {
        implicit val system: ActorSystem = ActorSystem()
        implicit val materializer: ActorMaterializer = ActorMaterializer()
        // needed for the future flatMap/onComplete in the end
        implicit val executionContext: ExecutionContextExecutor = system.dispatcher

        val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = "http://localhost:8080/players"))

        responseFuture
            .onComplete {
                case Success(res: HttpResponse) => {

                    val eventualString: Future[Player] = Unmarshal(res.entity).to[Player]
                    println(eventualString)
                    println(res.entity)
                }
                case Failure(_)   => sys.error("something wrong")
            }
    }
}
