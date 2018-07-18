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
    val nameField: Field[Person, String] = new Field.Impl[Person, String]("name", _.name)
    val ageField: Field[Person, Long] = new Field.Impl[Person, Long]("age", _.age)
  }


}
