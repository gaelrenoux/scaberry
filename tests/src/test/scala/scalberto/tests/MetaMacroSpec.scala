package scalberto.tests

import org.scalatest.{FlatSpec, Matchers}
import scalberto.core.{CopyableField, Field}
import scalberto.macros.MetaMacro
import scalberto.tests.data._

import scala.reflect.ClassTag

class MetaMacroSpec extends FlatSpec with Matchers with Helpers {

  /** Unused. Makes sure the Field import is kept even on automatic refactoring. */
  val f: (Field[Any, Any], CopyableField[Any, Any]) = null

  "from" should "not work on non-case classes" in {
    "MetaMacro.from[Animal]" shouldNot compile
  }

  it should "not return other vals, vars and defs" in {
    val fieldsKeys = MetaMacro.from[Dog].fieldsMap.keySet
    fieldsKeys shouldNot contain('animate)
    fieldsKeys shouldNot contain('animate)
    fieldsKeys shouldNot contain('owner)
    fieldsKeys shouldNot contain('genus)
    fieldsKeys shouldNot contain('whatever)
    fieldsKeys shouldNot contain('whatever2)
    fieldsKeys shouldNot contain('unary)
    fieldsKeys shouldNot contain('parameterized)
    fieldsKeys shouldNot contain('unary2)
    fieldsKeys shouldNot contain('parameterized2)
    fieldsKeys shouldNot contain('otherName)
    fieldsKeys shouldNot contain('otherOtherName)
  }

  it should "not return fields from another constructor" in {
    val fieldsKeys = MetaMacro.from[Dog].fieldsMap.keySet
    fieldsKeys shouldNot contain('other)
  }

  "Definitions" should "have the correct class" in {
    Dog.meta.fieldsMap('name) shouldBe a[CopyableField[Dog, Some[String]]]
    Dog.meta.fieldsMap('color) shouldBe a[CopyableField[Dog, String]]
    Dog.meta.fieldsMap('weight) shouldBe a[CopyableField[Dog, Long]]
  }

  they should "carry the correct name" in {
    Dog.meta.fieldsMap('name).name.name should be("name")
    Dog.meta.fieldsMap('color).name.name should be("color")
    Dog.meta.fieldsMap('weight).name.name should be("weight")
  }

  they should "carry the correct class tags" in {
    Dog.meta.fieldsMap('name).typeClassTag should be(implicitly[ClassTag[Some[String]]])
    Dog.meta.fieldsMap('color).typeClassTag should be(implicitly[ClassTag[String]])
    Dog.meta.fieldsMap('weight).typeClassTag should be(implicitly[ClassTag[Long]])
  }

  val rex: Dog = Dog("brown", 9L, Some("Rex"))

  "Getters" should "return the correct value" in {
    Dog.meta.fieldsMap('name)(rex).get should be(Some("Rex"))
    Dog.meta.fieldsMap('color)(rex).get should be("brown")
    Dog.meta.fieldsMap('weight)(rex).get should be(9L)
  }

  "Copiers" should "return the correct value" in {
    Dog.meta.fieldsMap('name).asInstanceOf[CopyableField[Dog, Some[String]]](rex).copy(Some("Medor")) should be(rex.copy(name = Some("Medor")))
    Dog.meta.fieldsMap('color).asInstanceOf[CopyableField[Dog, String]](rex).copy("black") should be(rex.copy(color = "black"))
    Dog.meta.fieldsMap('weight).asInstanceOf[CopyableField[Dog, Long]](rex).copy(12L) should be(rex.copy(weight = 12L))
  }

}

