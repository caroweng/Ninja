package ninja.model.fileIO

import controller.component.State
import ninja.model.DeskInterface
import play.api.libs.json.JsObject

trait FileIOInterface {
  def load: DeskInterface
  def save(desk: DeskInterface, state: State.state): Unit
//  def deskToString(desk: DeskInterface, state: State.state): JsObject
}
