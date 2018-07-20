import bismuth.core._

object Samples {

  case class Person(name: String, age: Long)

  trait Animal {
    val weight: Long
    val color: String
    val name: Option[String]
  }

  case class Dog(color: String, weight: Long, name: Some[String]) extends Animal


  val personFields = new Fields[Person] {
    val nameField = new Field[Person, String, Field.Copier[Person, String], Any]("name", _.name, (p, n) => p.copy(name = n))
    val ageField = new Field[Person, Long, Any, Any]("age", _.age)
  }


}
