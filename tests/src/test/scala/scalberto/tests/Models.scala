package scalberto.tests

object Models {

  case class Person(name: String, age: Long)

  trait Animal {
    val weight: Long
    val color: String
    val name: Option[String]

    lazy val itself: Animal = this

    protected val animate = true
    def otherName: Option[String] = name
    var whatever = 0

    def unary() = 42
    def parameterized[A]: Nothing = ???
  }

  case class Dog(color: String, weight: Long = 1, name: Some[String]) extends Animal {
    val owner: String = "Unknown"

    private val genus = "canis" //unused but needed for test

    def otherOtherName: Option[String] = name
    var whatever2 = 0

    def unary2() = 42
    def parameterized2[A]: Nothing = ???

    def this(other: Dog) = this(other.color, other.weight, other.name)
  }

}
