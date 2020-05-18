package ninja.model.fileIO.json

import com.google.inject.Inject
import ninja.controller.component.State
import ninja.model.DeskInterface
import ninja.model.component.Desk
import ninja.model.component.component.component.component.{Cell, Field, Ninja, Weapon}
import ninja.model.component.component.component.{CellInterface, FieldInterface, NinjaInterface}
import ninja.model.fileIO.FileIOInterface
import play.api.libs.json._
import player.{Player, PlayerInterface, StateOfPlayer}

import scala.io.Source
import scala.util.{Failure, Success}

class FileIO @Inject() extends FileIOInterface {

    override def load: DeskInterface = {
        val json: JsValue = Json.parse(Source.fromFile("target/desk.json").getLines.mkString)

        val name_1 = ((json \ "desk" \ "player1") \ "name").as[String]
        val state_1_string = ((json \ "desk" \ "player1") \ "state").as[String]
        val state_1 = StateOfPlayer.stringToStateOfPlayer(state_1_string)
        val id_1 = ((json \ "desk" \ "player1") \ "id").as[Int]
        val player1: PlayerInterface = Player(name_1, state_1, id_1)

        val name_2 = ((json \ "desk" \ "player2") \ "name").as[String]
        val state_2_string: String = ((json \ "desk" \ "player2") \ "state").as[String]
        val state_2 = StateOfPlayer.stringToStateOfPlayer(state_2_string)
        val id_2 = ((json \ "desk" \ "player2") \ "id").as[Int]
        val player2: PlayerInterface = Player(name_2, state_2, id_2)

        var field: FieldInterface = Field(Array.ofDim[Cell](6,6))

        field.matrix.indices flatMap(i =>
            field.matrix.indices map (j =>
                field.matrix(i)(j) = Cell(None)
                ))

//        for {i <- field.matrix.indices
//             j <- field.matrix.indices} {
//            field.matrix(i)(j) = Cell(None)
//        }


        for (index <- 0 until 6 * 6) {
            val row = (json \\ "row")(index).as[Int]
            val col = (json \\ "col")(index).as[Int]

            val cell = (json \\ "cell")(index)

            val ninja_i = (cell \ "ninja")
            println(ninja_i.get)
            if((ninja_i \ "weapon").isDefined) {
                val weapon_string = (ninja_i \ "weapon").as[String]
                val weapon = Weapon.stringToWeapon(weapon_string)
                val ninjaId = (ninja_i \ "ninjaId").as[Int]
                val playerId = (ninja_i \ "playerId").as[Int]
                val ninja: NinjaInterface = Ninja(weapon, playerId, ninjaId)
                field = field.+(ninja, (row, col))
            }
        }

        Desk(field, player1, player2)
    }

    override def save(grid: DeskInterface, state: State.Value): Unit = {
        import java.io._
        val pw = new PrintWriter(new File("target/desk.json"))
        pw.write(Json.prettyPrint(deskToString(grid, state)))
        pw.close()
    }

    implicit val ninjaWrites = new Writes[NinjaInterface] {
        def writes(ninja: NinjaInterface): JsObject = Json.obj(
            "weapon" -> ninja.weapon.toString,
            "playerId" -> ninja.playerId,
            "ninjaId" -> ninja.ninjaId
        )
    }

    implicit val cellWrites = new Writes[CellInterface] {
        def writes(cell: CellInterface): JsObject = {
            val ninja = cell.getNinja()
            ninja match {
                case Success(value) =>
                     Json.obj(
                    "ninja" -> Json.toJson(value)
                )
                case Failure(value) =>
                Json.obj("ninja" -> "")
            }

//            if(cell.exists()) {
//                val ninja: NinjaInterface = cell.getNinja()
//                Json.obj(
//                    "ninja" -> Json.toJson(ninja)
//                )
//            } else {
//                Json.obj("ninja" -> "")
//            }
        }
    }

    implicit val playerWrites = new Writes[PlayerInterface] {
        def writes(player: PlayerInterface) = Json.obj(
            "name" -> player.name,
            "state" -> player.state,
            "id" -> player.id,
        )
    }

    def deskToString(desk: DeskInterface, state: State.Value) = {
        Json.obj(
            "desk" -> Json.obj(
                "player1" -> Json.toJson(desk.player1),
                "player2" -> Json.toJson(desk.player2),
                "field" -> Json.toJson(
                    for {
                        row <- desk.field.matrix.indices;
                        col <- desk.field.matrix.indices
                    } yield {
                        Json.obj(
                            "row" -> row,
                            "col" -> col,
                            "cell" -> Json.toJson(desk.field.matrix(row)(col))
                        )
                    }
                )
            ),
            "state" -> Json.toJson(state)
        )
    }
}
