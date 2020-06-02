package ninja.model.fileIO.xml

import com.google.inject.Inject
import controller.component.State
import ninja.model.DeskInterface
import ninja.model.component.Desk
import ninja.model.component.component.component.component.{Cell, Field, Ninja, Weapon}
import ninja.model.component.component.component.{CellInterface, FieldInterface, NinjaInterface}
import ninja.model.fileIO.FileIOInterface
import player.{Player, PlayerInterface, StateOfPlayer}

import scala.util.{Failure, Success}
import scala.xml.PrettyPrinter

class FileIO @Inject() extends FileIOInterface {

    override def load: DeskInterface = {
        val file = scala.xml.XML.loadFile("target/desk.xml")
        val player1_name = (file \\ "desk" \ "player1" \ "player" \ "@name").text
        val player1_state = (file \\ "desk" \ "player1" \ "player" \ "@state").text
        val player1_id = (file \\ "desk" \ "player1" \ "player" \ "@id").text.toInt
        val player1 = Player(player1_name, StateOfPlayer.stringToStateOfPlayer(player1_state), player1_id )
        val player2_name = (file \\ "desk" \ "player2" \ "player" \ "@name").text
        val player2_state = (file \\ "desk" \ "player2" \ "player" \ "@state").text
        val player2_id = (file \\ "desk" \ "player2" \ "player" \ "@id").text.toInt
        val player2 = Player(player2_name, StateOfPlayer.stringToStateOfPlayer(player2_state), player2_id )

        var field: FieldInterface = Field(Array.ofDim[Cell](6,6))

        for {i <- field.matrix.indices
             j <- field.matrix.indices} {
            field.matrix(i)(j) = Cell(None)
        }
        for (cell <- file \\ "desk" \ "field" \ "cell") {
            val row = (cell \ "@row").text.toInt
            val col = (cell \ "@col").text.toInt

            if((cell \\ "ninja").nonEmpty) {
                val ninja_obj = (cell \ "ninja")
                val ninja_weapon = (ninja_obj \ "@weapon").text
                val ninja_playerId = (ninja_obj \ "@playerId").text.toInt
                val ninja_ninjaId = (ninja_obj \ "@ninjaId").text.toInt
                val ninja = Ninja(Weapon.stringToWeapon(ninja_weapon), ninja_playerId, ninja_ninjaId)
                field = field.+(ninja, (row, col))
            }
        }
        Desk(field, player1, player2)
    }

    def save(grid: DeskInterface, state: State.state): Unit = saveString(grid)

    private def saveString(desk: DeskInterface): Unit = {
        import java.io._
        val pw = new PrintWriter(new File("target/desk.xml"))
        val prettyPrinter = new PrettyPrinter(120, 4)
        val xml = prettyPrinter.format(deskToXml(desk))
        pw.write(xml)
        pw.close()
    }


    private def deskToXml(desk: DeskInterface) = {
        <desk>
            <player1>
                {playerToXml(desk.player1)}
            </player1>
            <player2>
                {playerToXml(desk.player2)}
            </player2>
            <field>
                {for {
                    row <- desk.field.matrix.indices
                    col <- desk.field.matrix.indices
                } yield {

                    cellToXml(desk.field.matrix(row)(col), row, col)
                }}
            </field>
        </desk>
    }

    private def playerToXml(player: PlayerInterface) = {
        <player name={player.name.toString}
                state={player.state.toString}
                id={player.id.toString}>
        </player>
    }

    def cellToXml(cell: CellInterface, row: Int, col: Int) = {
        <cell row={ row.toString } col={ col.toString } >
            {
                val tryNinja = cell.getNinja()
                tryNinja match {
                    case Success(ninja) => ninjaToXml(ninja)
                    case Failure(e) =>
                }
            }
        </cell>
    }

    def ninjaToXml(ninja: NinjaInterface) = {
        <ninja weapon={ ninja.weapon.toString } playerId={ ninja.playerId.toString } ninjaId={ ninja.ninjaId.toString } >
        </ninja>
    }
}
