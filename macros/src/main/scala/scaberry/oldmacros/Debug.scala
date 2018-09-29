package scaberry.oldmacros

import scala.reflect.macros.whitebox

private[oldmacros] trait Debug {

  val c: whitebox.Context

  lazy val debugEnabled: Boolean =
    Option(System.getProperty("scaberry.macro.debug")).filterNot(_.isEmpty).map(_.toLowerCase).exists("true".equals)

  def debug(msg: => String): Unit = {
    if (debugEnabled) {
      c.info(c.enclosingPosition, msg, force = false)
    }
  }

}
