package ninja.game

import com.google.inject.AbstractModule
import model.DeskInterface
import model.component.Desk
import model.component.component.component.component.{Cell, Field}
import model.component.component.component.{FieldInterface, Player, StateOfPlayer}
import net.codingwell.scalaguice.ScalaModule
import ninja.controller.ControllerInterface
import ninja.controller.component.Controller
import ninja.fileIO.FileIOInterface

class NinjaModule extends AbstractModule with ScalaModule {
    var player1 = Player("Spieler1", StateOfPlayer.go, 1)
    var player2 = Player("Spieler2", StateOfPlayer.pause, 2)
    var field = Field(Array.ofDim[Cell](6,6))
    val desk = Desk(field, player1, player2)

    def configure() = {
        bind[FieldInterface].toInstance(field)
        bind[DeskInterface].toInstance(Desk(field, player1, player2))
        bind[ControllerInterface].toInstance(new Controller(desk))

        bind[FileIOInterface].to[ninja.fileIO.json.FileIO]
//        bind[FileIOInterface].to[ninja.ninja.fileIO.xml.FileIO]

    }

}