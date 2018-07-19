package bismuth.tests

import bismuth.core.{Field, Fields}
import bismuth.tests.Models._
import org.scalatest.{FlatSpec, Matchers}

import scala.reflect.ClassTag

class FieldsSpec extends FlatSpec with Matchers with Helpers {

  val animalValFields = Fields.fromPublic[Animal]
  val dogValFields = Fields.fromPublic[Dog]
  val dogConstructorFields = Fields.fromConstructor[Dog]

  "fromPublic" should "return the public vals, vars, and nullary defs" in {
    "animalValFields.name" should compile
    "animalValFields.weight" should compile
    "animalValFields.color" should compile
    "animalValFields.otherName" should compile
    "animalValFields.whatever" should compile
    "dogValFields.name" should compile
    "dogValFields.weight" should compile
    "dogValFields.color" should compile
    "dogValFields.owner" should compile
    "dogValFields.otherName" should compile
    "dogValFields.otherOtherName" should compile
    "dogValFields.whatever" should compile
    "dogValFields.whatever2" should compile
  }

  it should "not return the non-public vals" in {
    "animalValFields.animate" shouldNot typeCheck
    "dogValFields.animate" shouldNot typeCheck
    "dogValFields.genus" shouldNot typeCheck
  }

  it should "not return the non-nullary defs" in {
    "animalValFields.unary" shouldNot typeCheck
    "animalValFields.parameterized" shouldNot typeCheck
    "dogValFields.unary" shouldNot typeCheck
    "dogValFields.parameterized" shouldNot typeCheck
    "dogValFields.unary2" shouldNot typeCheck
    "dogValFields.parameterized2" shouldNot typeCheck
  }

  "fromConstructor" should "return the fields in the primary constructor" in {
    "dogConstructorFields.name" should compile
    "dogConstructorFields.weight" should compile
    "dogConstructorFields.color" should compile
  }

  it should "not return fields from another constructor" in {
    "dogConstructorFields.other" shouldNot typeCheck
  }

  it should "not return other vals" in {
    "dogConstructorFields.animate" shouldNot typeCheck
    "dogConstructorFields.owner" shouldNot typeCheck
    "dogConstructorFields.genus" shouldNot typeCheck
  }

  it should "not return other vars" in {
    "dogConstructorFields.whatever" shouldNot typeCheck
    "dogConstructorFields.whatever2" shouldNot typeCheck
  }

  it should "not return the defs" in {
    "dogConstructorFields.otherName" shouldNot typeCheck
    "dogConstructorFields.otherOtherName" shouldNot typeCheck
    "dogConstructorFields.unary" shouldNot typeCheck
    "dogConstructorFields.parameterized" shouldNot typeCheck
    "dogConstructorFields.unary2" shouldNot typeCheck
    "dogConstructorFields.parameterized2" shouldNot typeCheck
  }

  "Definitions" should "have the correct class" in {
    def f : Field[Any, Any] = ??? // to ensuite Field is in the scope
    "val x: Field[Animal, Option[String]] = animalValFields.name" should compile
    "val x: Field[Animal, Long] = animalValFields.weight" should compile
    "val x: Field[Dog, Some[String]] = dogValFields.name" should compile
    "val x: Field[Dog, Long] = dogValFields.weight" should compile
    "val x: Field[Dog, String] = dogValFields.owner" should compile
    "val x: Field[Dog, Some[String]] = dogConstructorFields.name" should compile
    "val x: Field[Dog, Long] = dogConstructorFields.weight" should compile
  }

  they should "carry the correct name" in {
    force.af(animalValFields.name).name should be("name")
    force.af(animalValFields.weight).name should be("weight")
    force.af(animalValFields.otherName).name should be("otherName")
    force.af(dogValFields.name).name should be("name")
    force.af(dogValFields.weight).name should be("weight")
    force.af(dogValFields.owner).name should be("owner")
    force.af(dogValFields.otherName).name should be("otherName")
    force.af(dogValFields.otherOtherName).name should be("otherOtherName")
    force.af(dogConstructorFields.name).name should be("name")
    force.af(dogConstructorFields.weight).name should be("weight")
  }

  they should "carry the correct class tags" in {
    force.af(animalValFields.name).sourceClassTag should be(implicitly[ClassTag[Animal]])
    force.af(animalValFields.name).typeClassTag should be(implicitly[ClassTag[Option[String]]])
    force.af(animalValFields.weight).sourceClassTag should be(implicitly[ClassTag[Animal]])
    force.af(animalValFields.weight).typeClassTag should be(implicitly[ClassTag[Long]])
    force.af(animalValFields.otherName).sourceClassTag should be(implicitly[ClassTag[Animal]])
    force.af(animalValFields.otherName).typeClassTag should be(implicitly[ClassTag[Option[String]]])
    force.af(dogValFields.name).sourceClassTag should be(implicitly[ClassTag[Dog]])
    force.af(dogValFields.name).typeClassTag should be(implicitly[ClassTag[Some[String]]])
    force.af(dogValFields.weight).sourceClassTag should be(implicitly[ClassTag[Dog]])
    force.af(dogValFields.weight).typeClassTag should be(implicitly[ClassTag[Long]])
    force.af(dogValFields.owner).sourceClassTag should be(implicitly[ClassTag[Dog]])
    force.af(dogValFields.owner).typeClassTag should be(implicitly[ClassTag[String]])
    force.af(dogValFields.otherName).sourceClassTag should be(implicitly[ClassTag[Dog]])
    force.af(dogValFields.otherName).typeClassTag should be(implicitly[ClassTag[Option[String]]])
    force.af(dogValFields.otherOtherName).sourceClassTag should be(implicitly[ClassTag[Dog]])
    force.af(dogValFields.otherOtherName).typeClassTag should be(implicitly[ClassTag[Option[String]]])
    force.af(dogConstructorFields.name).sourceClassTag should be(implicitly[ClassTag[Dog]])
    force.af(dogConstructorFields.name).typeClassTag should be(implicitly[ClassTag[Some[String]]])
    force.af(dogConstructorFields.weight).sourceClassTag should be(implicitly[ClassTag[Dog]])
    force.af(dogConstructorFields.weight).typeClassTag should be(implicitly[ClassTag[Long]])
  }


  val casimir: Animal = new Animal {
    override val weight: Long = 42L
    override val color: String = "orange"
    override val name: Option[String] = Some("Casimir")
  }
  val rex: Dog = Dog("brown", 9L, Some("Rex"))

  "Getters" should "return the correct value" in {
    force.af(animalValFields.name)(casimir).get should be(Some("Casimir"))
    force.af(animalValFields.weight)(casimir).get should be(42L)
    force.af(animalValFields.otherName)(casimir).get should be(Some("Casimir"))
    force.af(animalValFields.name)(rex).get should be(Some("Rex"))
    force.af(animalValFields.weight)(rex).get should be(9L)
    force.af(animalValFields.otherName)(rex).get should be(Some("Rex"))
    force.af(dogValFields.name)(rex).get should be(Some("Rex"))
    force.af(dogValFields.weight)(rex).get should be(9L)
    force.af(dogValFields.owner)(rex).get should be("Unknown")
    force.af(dogValFields.otherName)(rex).get should be(Some("Rex"))
    force.af(dogValFields.otherOtherName)(rex).get should be(Some("Rex"))
    force.af(dogConstructorFields.name)(rex).get should be(Some("Rex"))
    force.af(dogConstructorFields.weight)(rex).get should be(9L)
  }
}
