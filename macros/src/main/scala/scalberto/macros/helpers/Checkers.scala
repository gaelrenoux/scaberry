package scalberto.macros.helpers

import scala.meta._

trait Checkers {

  def checking[A](test: => Boolean, message: String, pos: Position = null)(block: => A): A = if (test) block else {
    if (pos == null) abort(pos, message) else abort(message)
  }

}