package bismuth.tests

import bismuth.core.Field
import bismuth.macros.FieldsMacro
import bismuth.tests.Models._
import org.scalatest.{FlatSpec, Matchers}

import scala.reflect.ClassTag

class FieldsMacroSpec extends FlatSpec with Matchers with Helpers {

  private val animalValFields = FieldsMacro.fromPublic[Animal]
  private val dogValFields = FieldsMacro.fromPublic[Dog]
  private val dogConstructorFields = FieldsMacro.fromConstructor[Dog]

  /** Unused. Makes sure the Field import is kept even on automatic refactoring. */
  val f: Field.Readable[Any, Any] = null

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
    "val x: Field.Readable[Animal, Option[String]] = animalValFields.name" should compile
    "val x: Field.Readable[Animal, Long] = animalValFields.weight" should compile
    "val x: Field.Copyable[Dog, Some[String]] = dogValFields.name" should compile
    "val x: Field.Copyable[Dog, Long] = dogValFields.weight" should compile
    "val x: Field.Readable[Dog, String] = dogValFields.owner" should compile
    "val x: Field.Copyable[Dog, Some[String]] = dogConstructorFields.name" should compile
    "val x: Field.Copyable[Dog, Long] = dogConstructorFields.weight" should compile
  }

  they should "carry the correct name" in {
    force.arf(animalValFields.name).name should be("name")
    force.arf(animalValFields.weight).name should be("weight")
    force.arf(animalValFields.otherName).name should be("otherName")
    force.arf(dogValFields.name).name should be("name")
    force.arf(dogValFields.weight).name should be("weight")
    force.arf(dogValFields.owner).name should be("owner")
    force.arf(dogValFields.otherName).name should be("otherName")
    force.arf(dogValFields.otherOtherName).name should be("otherOtherName")
    force.arf(dogConstructorFields.name).name should be("name")
    force.arf(dogConstructorFields.weight).name should be("weight")
  }

  they should "carry the correct class tags" in {
    force.arf(animalValFields.name).sourceClassTag should be(implicitly[ClassTag[Animal]])
    force.arf(animalValFields.name).typeClassTag should be(implicitly[ClassTag[Option[String]]])
    force.arf(animalValFields.weight).sourceClassTag should be(implicitly[ClassTag[Animal]])
    force.arf(animalValFields.weight).typeClassTag should be(implicitly[ClassTag[Long]])
    force.arf(animalValFields.otherName).sourceClassTag should be(implicitly[ClassTag[Animal]])
    force.arf(animalValFields.otherName).typeClassTag should be(implicitly[ClassTag[Option[String]]])
    force.arf(dogValFields.name).sourceClassTag should be(implicitly[ClassTag[Dog]])
    force.arf(dogValFields.name).typeClassTag should be(implicitly[ClassTag[Some[String]]])
    force.arf(dogValFields.weight).sourceClassTag should be(implicitly[ClassTag[Dog]])
    force.arf(dogValFields.weight).typeClassTag should be(implicitly[ClassTag[Long]])
    force.arf(dogValFields.owner).sourceClassTag should be(implicitly[ClassTag[Dog]])
    force.arf(dogValFields.owner).typeClassTag should be(implicitly[ClassTag[String]])
    force.arf(dogValFields.otherName).sourceClassTag should be(implicitly[ClassTag[Dog]])
    force.arf(dogValFields.otherName).typeClassTag should be(implicitly[ClassTag[Option[String]]])
    force.arf(dogValFields.otherOtherName).sourceClassTag should be(implicitly[ClassTag[Dog]])
    force.arf(dogValFields.otherOtherName).typeClassTag should be(implicitly[ClassTag[Option[String]]])
    force.arf(dogConstructorFields.name).sourceClassTag should be(implicitly[ClassTag[Dog]])
    force.arf(dogConstructorFields.name).typeClassTag should be(implicitly[ClassTag[Some[String]]])
    force.arf(dogConstructorFields.weight).sourceClassTag should be(implicitly[ClassTag[Dog]])
    force.arf(dogConstructorFields.weight).typeClassTag should be(implicitly[ClassTag[Long]])
  }


  val casimir: Animal = new Animal {
    override val weight: Long = 42L
    override val color: String = "orange"
    override val name: Option[String] = Some("Casimir")
  }
  val rex: Dog = Dog("brown", 9L, Some("Rex"))

  "Getters" should "return the correct value" in {
    force.arf(animalValFields.name)(casimir).get should be(Some("Casimir"))
    force.arf(animalValFields.weight)(casimir).get should be(42L)
    force.arf(animalValFields.otherName)(casimir).get should be(Some("Casimir"))
    force.arf(animalValFields.name)(rex).get should be(Some("Rex"))
    force.arf(animalValFields.weight)(rex).get should be(9L)
    force.arf(animalValFields.otherName)(rex).get should be(Some("Rex"))
    force.arf(dogValFields.name)(rex).get should be(Some("Rex"))
    force.arf(dogValFields.weight)(rex).get should be(9L)
    force.arf(dogValFields.owner)(rex).get should be("Unknown")
    force.arf(dogValFields.otherName)(rex).get should be(Some("Rex"))
    force.arf(dogValFields.otherOtherName)(rex).get should be(Some("Rex"))
    force.arf(dogConstructorFields.name)(rex).get should be(Some("Rex"))
    force.arf(dogConstructorFields.weight)(rex).get should be(9L)
  }

  "Copiers" should "return the correct value" in {
    force.dcf[Some[String]](dogValFields.name)(rex).copy(Some("Medor")) should be(rex.copy(name = Some("Medor")))
    force.dcf[Long](dogValFields.weight)(rex).copy(12L) should be(rex.copy(weight = 12L))
    force.dcf[Some[String]](dogConstructorFields.name)(rex).copy(Some("Medor")) should be(rex.copy(name = Some("Medor")))
    force.dcf[Long](dogConstructorFields.weight)(rex).copy(12L) should be(rex.copy(weight = 12L))
  }

  they should "expect the correct type" in {
    "dogValFields.name(rex).copy(None)" shouldNot typeCheck
    "dogValFields.weight(rex).copy(\"42\")" shouldNot typeCheck
  }

  they should "not compile when not available" in {
    "animalValFields.name(casimir).copy(None)" shouldNot typeCheck
    "animalValFields.weight(casimir).copy(42L)" shouldNot typeCheck
  }
}
