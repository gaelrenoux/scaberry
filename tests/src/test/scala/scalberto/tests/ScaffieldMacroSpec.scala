package scalberto.tests

import org.scalatest.{FlatSpec, Matchers}
import scalberto.core.{CopyableField, Field}
import scalberto.macros.MetaMacro
import scalberto.tests.data._

import scala.reflect.ClassTag

class ScaffieldMacroSpec extends FlatSpec with Matchers with Helpers {

  /** Unused. Makes sure the Field import is kept even on automatic refactoring. */
  val f: (Field[Any, Any], CopyableField[Any, Any]) = null

  "scaffield" should "not work on non-case classes" in {
    "@Metadata class Test(val x: String)" shouldNot compile
  }

  it should "return fields" in {
    "Cat.meta.fields.name" should compile
    "Cat.meta.fields.color" should compile
    "Cat.meta.fields.weight" should compile
  }

}

