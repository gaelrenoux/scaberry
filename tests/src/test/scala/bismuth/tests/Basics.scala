package bismuth.tests

import bismuth.core.{Field, Fields}
import bismuth.tests.Models.Person
import org.scalatest.FlatSpec
import Models._

class Basics extends FlatSpec {
  val batman = Person("Bruce Wayne", 28)

  val personFields = Fields.fromConstructor[Person]

  "it" should "compile" in {
    println(personFields.name)
    println(personFields.age)

    println(personFields.name(batman).get)
    println(personFields.age(batman).get)
  }


  // TODO fields should not be based on constructor
  val animalFields = Fields.fromVals[Animal]
  val dogFields = Fields.fromConstructor[Dog]

  val animalWeight: Field[Animal, Long] = animalFields.weight
  val animalColor: Field[Animal, String] = animalFields.color
  val animalName: Field[Animal, Option[String]] = animalFields.name

  val dogWeight: Field[Dog, Long] = dogFields.weight
  val dogColor: Field[Dog, String] = dogFields.color
  val dogName: Field[Dog, Some[String]] = dogFields.name

  val casimir: Animal = new Animal {
    override val weight: Long = 42L
    override val color: String = "orange"
    override val name: Option[String] = Some("casimir")
  }
  val rex: Dog = Dog("brown", 9L, Some("Rex"))

  val awc = animalWeight(casimir).get
  //val dnc = dogName(casimir).get

  val awr = animalWeight(rex).get
  val dnr = dogColor(rex).get

  //val awcc = animalWeight(casimir).copy(42L)
  //val dncc = dogName(casimir).copy("")

  //val awrc = animalWeight(rex).copy(42L)
  //val dnrc = dogColor(rex).copy("")

  def expectsDogStringField(f: Field[Dog, String]) = ???

  expectsDogStringField(animalColor)
  expectsDogStringField(dogColor)

  def expectsDogOptionStringField(f: Field[Dog, Some[String]]) = {
    val stringOpt = f(rex).get
    //f(rex).copy(None)
    //f(rex).copy(Some(""))
  }

  //expectsDogOptionStringField(animalName)
  expectsDogOptionStringField(dogName)

}
