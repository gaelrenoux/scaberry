package bismuth.tests

object Models {

  case class Person(name: String, age: Long)

  sealed trait Animal {
    val weight: Long
    val color: String
    val name: Option[String]
  }

  case class Dog(color: String, weight: Long, name: Some[String]) extends Animal

}
