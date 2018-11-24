package scaberry.macrostests

import org.scalatest.{FlatSpec, Matchers}
import scaberry.core.{Filter, Update}
import scaberry.macrostests.models._

class UpdateSpec extends FlatSpec with Matchers {

  //private val animalFields = Animal.meta.fields
  private val dogFields = Dog.meta.fields

  val inouk: Dog = Dog("Inouk", Some("Gael"))
  val rex: Dog = Dog("Rex", None, good = false, 2)

  "identity" should "not change any value" in {
    Update.identity[Dog].update(inouk) should be(inouk)
    Update.identity[Dog].update(rex) should be(rex)
  }

  "value" should "set the expected value" in {
    Update.value(42).update(56) should be(42)
    Update.value("hello").update("goodbye") should be("hello")
    Update.value(rex).update(inouk) should be(rex)
  }

  "operation" should "set the expected value" in {
    Update.operation[Int](_*2).update(21) should be(42)
    Update.operation[String](_.reverse).update("olleh") should be("hello")
    Update.operation[Dog](_.copy(name = "Bernard")).update(inouk) should be(inouk.copy(name = "Bernard"))
  }

  "Field update" should "work" in {
    dogFields.name.updateVal("Bernard").update(inouk) should be(inouk.copy(name = "Bernard"))
    dogFields.name.updateWith(_.reverse).update(inouk) should be(inouk.copy(name = "kuonI"))
  }

}
