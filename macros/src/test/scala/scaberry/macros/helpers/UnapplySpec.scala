package scaberry.macros.helpers

import org.scalatest.{FlatSpec, Matchers}

import scala.meta._

class UnapplySpec extends FlatSpec with Matchers {

  "Symbol" should "recognise a literal symbol" in {
    q"""'chuck""" should matchPattern {
      case Unapply.Symbol(s) if s == 'chuck =>
    }
  }

  it should "recognise a basic constructor call" in {
    q"""Symbol("chuck")""" should matchPattern {
      case Unapply.Symbol(s) if s == 'chuck =>
    }
    q"""scala.Symbol("chuck")""" should matchPattern {
      case Unapply.Symbol(s) if s == 'chuck =>
    }
  }

  it  should "fail on something else" in {
    q""""chuck"""" shouldNot matchPattern {
      case Unapply.Symbol(s) if s == 'chuck =>
    }
  }

}
