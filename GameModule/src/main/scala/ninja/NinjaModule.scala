package ninja

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import controller.ControllerInterface
import controller.component.Controller
import ninja.model.DeskInterface
import ninja.model.component.Desk
import ninja.model.component.component.component.FieldInterface
import ninja.model.component.component.component.component.{Cell, Field}
import ninja.model.fileIO.FileIOInterface
import player.{Player, StateOfPlayer}

class NinjaModule extends AbstractModule with ScalaModule {
    var player1 = Player("Spieler1", StateOfPlayer.go, 1)
    var player2 = Player("Spieler2", StateOfPlayer.pause, 2)
    var field = Field(Array.ofDim[Cell](6,6))
    val desk = Desk(field, player1, player2)

    def configure() = {
        bind[FieldInterface].toInstance(field)
        bind[DeskInterface].toInstance(Desk(field, player1, player2))
        bind[ControllerInterface].toInstance(new Controller(desk))

        bind[FileIOInterface].to[model.fileIO.json.FileIO]
//        bind[FileIOInterface].to[ninja.model.fileIO.xml.FileIO]

    }

}