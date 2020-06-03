package ninja.fileIO

import model.DeskInterface
import ninja.controller.component.State

trait FileIOInterface {
  def load: DeskInterface
  def save(desk: DeskInterface, state: State.state): Unit
//  def deskToString(desk: DeskInterface, state: State.state): JsObject
}
