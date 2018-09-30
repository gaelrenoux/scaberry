package scaberry.core

import org.scalatest.{FlatSpec, Matchers}

class TagMapSpec extends FlatSpec with Matchers {

  "TagMap" should "work" in {
    val tm = TagMap("john", "jack", 42L)
    tm.get[String] should be(Some("john"))
    tm.getList[String] should be(Seq("john", "jack"))
    tm.get[Long] should be(Some(42))
    tm.get[Boolean] should be(None)
  }

}
