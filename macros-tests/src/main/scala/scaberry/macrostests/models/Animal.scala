package scaberry.macrostests.models

trait Animal {

  val color: String

  lazy val itself: Animal = this

  protected val animate = true

  var whatever = 0

  def unary() = 42

  def parameterized[A]: Nothing = ???
}