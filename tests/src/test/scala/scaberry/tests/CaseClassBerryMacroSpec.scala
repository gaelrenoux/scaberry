package scaberry.tests

import org.scalatest.{FlatSpec, Matchers}
import scaberry.tests.models._

import scala.reflect.ClassTag

/** Tests on the @berry macro when applied on a case class. */
class CaseClassBerryMacroSpec extends FlatSpec with Matchers {

  val inouk: Dog = Dog("Inouk", Some("Gael"))

  "the meta object" should "be created with the default name if none is given" in {
    "Dog.meta" should compile
  }

  it should "be created with a custom name if one is given" in {
    "Fox.burrow" should compile
    "Fox.meta" shouldNot typeCheck
  }

  "the meta fields object" should "contain the arguments of the primary constructor" in {
    "Dog.meta.fields.name" should compile
    "Dog.meta.fields.owner" should compile
    "Dog.meta.fields.good" should compile
    "Dog.meta.fields.childrenCount" should compile
  }

  it should "not contain the arguments of another constructor" in {
    "Wolf.meta.fields.color" should compile
    "Wolf.meta.fields.other" shouldNot typeCheck
  }

  it should "not contain other vals" in {
    "Dog.meta.fields.fullName" shouldNot typeCheck
    "Dog.meta.fields.owned" shouldNot typeCheck
  }

  it should "not contain the vars" in {
    "Dog.meta.fields.weight" shouldNot typeCheck
  }

  it should "not contain the defs" in {
    "Dog.meta.fields.compliment" shouldNot typeCheck
  }

  "the meta fields" should "be copyable fields" in {
    "val x: scaberry.core.CopyableField[Dog, String] = Dog.meta.fields.name" should compile
    "val x: scaberry.core.CopyableField[Dog, Option[String]] = Dog.meta.fields.owner" should compile
    "val x: scaberry.core.CopyableField[Dog, Boolean] = Dog.meta.fields.good" should compile
    "val x: scaberry.core.CopyableField[Dog, Long] = Dog.meta.fields.childrenCount" should compile
  }

  they should "carry the correct name" in {
    Dog.meta.fields.name.name.toString should be("'name")
    Dog.meta.fields.owner.name.toString should be("'owner")
    Dog.meta.fields.good.name.toString should be("'good")
    Dog.meta.fields.childrenCount.name.toString should be("'childrenCount")
  }

  they should "carry the correct class tags" in {
    Dog.meta.fields.name.typeClassTag should be(implicitly[ClassTag[String]])
    Dog.meta.fields.owner.typeClassTag should be(implicitly[ClassTag[Option[String]]])
    Dog.meta.fields.good.typeClassTag should be(implicitly[ClassTag[Boolean]])
    Dog.meta.fields.childrenCount.typeClassTag should be(implicitly[ClassTag[Long]])
  }

  "the meta fields getters" should "return the correct value" in {
    Dog.meta.fields.name(inouk).get should be("Inouk")
    Dog.meta.fields.owner(inouk).get should be(Some("Gael"))
    Dog.meta.fields.good(inouk).get should be(true)
    Dog.meta.fields.childrenCount(inouk).get should be(0L)
  }

  "the meta fields copiers" should "return the correct value" in {
    Dog.meta.fields.name(inouk).copy("Rex") should be(inouk.copy(name = "Rex"))
    Dog.meta.fields.owner(inouk).copy(None) should be(inouk.copy(owner = None))
    Dog.meta.fields.good(inouk).copy(false) should be(inouk.copy(good = false))
    Dog.meta.fields.childrenCount(inouk).copy(12L) should be(inouk.copy(childrenCount = 12L))
  }

  they should "expect the correct type" in {
    "Dog.meta.fields.name(inouk).copy(42)" shouldNot typeCheck
    "Dog.meta.fields.owner(inouk).copy(\"Me\")" shouldNot typeCheck
    "Dog.meta.fields.good(inouk).copy(0)" shouldNot typeCheck
    "Dog.meta.fields.childrenCount(inouk).copy(true)" shouldNot typeCheck
  }

  "the meta fields annotations" should "be found when declared" in {
    Dog.meta.fields.name.annotations.get[label].map(_.value) should be(Some("Nom"))
    Dog.meta.fields.name.annotations.get[priority] should be(None)
    Dog.meta.fields.good.annotations.get[label] should be(None)
  }

  they should "have all occurrences displayed by getList" in {
    Dog.meta.fields.good.annotations.getList[priority].map(_.level) should be(Seq(10, 12))
  }

  they should "have the first occurrence displayed by get" in {
    Dog.meta.fields.good.annotations.get[priority].map(_.level) should be(Some(10))
  }

  "the berryProp annotations" should "be integrated into the field" in {
    "Dog.meta.fields.owner.label" should compile
    "Dog.meta.fields.name.label" shouldNot typeCheck
  }

  they should "contain the correct value" in {
    Dog.meta.fields.owner.label should be("Master")
  }

  they should "prioritize the first value" in {
    Dog.meta.fields.childrenCount.masked should be("yes")
  }



}

