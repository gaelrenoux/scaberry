package scaberry.macros.helpers

private[macros] object Log {

  lazy val debugEnabled: Boolean = true
    Option(System.getProperty("scaberry.macro.debug")).filterNot(_.isEmpty).map(_.toLowerCase).exists("true".equals)

  def debug(msg: => String): Unit = {
    if (debugEnabled) println(msg)
  }

}
