package bismuth.macros.checks

import scala.reflect.macros.whitebox

trait Checkers {

  val c: whitebox.Context

  def checking[A](test: => Boolean, message: String)(block: => A): A = if (test) block else {
    c.abort(c.enclosingPosition, message)
  }

}