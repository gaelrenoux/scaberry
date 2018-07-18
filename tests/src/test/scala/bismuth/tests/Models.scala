package bismuth.tests

object Models {

  case class Person(name: String, age: Long)

  trait Animal {
    val weight: Long
    val color: String
    val name: Option[String]

    protected val animate = true
    def otherName: Option[String] = name
    var whatever = 0
  }

  case class Dog(color: String, weight: Long = 1, name: Some[String]) extends Animal {
    val owner: String = "Unknown"
    private val genus = "canis"

    def otherOtherName: Option[String] = name
    var whatever2 = 0

    def this(other: Dog) = this(other.color, other.weight, other.name)
  }

}
