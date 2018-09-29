package scaberry.oldtests.data

import scaberry.oldmacros.scaberry

@scaberry('berry)
case class Cat(color: String) {

}

object CatTest {
  val m = Cat.meta
}
