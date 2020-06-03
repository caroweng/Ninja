package ninja.view

import ninja.controller.component.Controller

import scala.swing._
import scala.util.{Failure, Success}

class CellPanel(row: Int, column: Int, controller: Controller) extends FlowPanel {

    val givenCellColor = new Color(200, 200, 255)
    val cellColor = new Color(224, 224, 255)
    val highlightedCellColor = new Color(192, 255, 192)

    def myCell = controller.desk.field.getCellAtPosition(row, column)

    def cellText(row: Int, col: Int): String = {
        val cell = controller.desk.field.getCellAtPosition(row, column)
        val tryNinja = cell.getNinja()
        tryNinja match {
            case Success(ninja) => ninja.weapon.toString
            case Failure(e) => "ninja"
        }
    }

    val label =
        new Label {
            text = cellText(row, column)
            font = new Font("Verdana", 1, 36)
        }
//
//    val cell = new BoxPanel(Orientation.Vertical) {
////        val value = new TextField("hall")
////        value.editable = false
////        contents += value
//        contents += label;
////        preferredSize = new Dimension(51, 51)
////        background = cellColor
//        border = Swing.BeveledBorder(Swing.Raised)
//        listenTo(mouse.clicks)
////        listenTo(controller)
//        reactions += {
////            case _ =>  {
////                label.text = cellText(row, column)
////                repaint
////            }
//            case MouseClicked(src, pt, mod, clicks, pops) => {
////                controller.showCandidates(row, column)
//                repaint
//            }
//        }
//    }

//
//    def redraw = {
//        //label.text = cellText(row, column)
//        setBackground(cell)
//        contents += cell
//        repaint
//    }

    def setBackground(p: Panel) = p.background = cellColor

}