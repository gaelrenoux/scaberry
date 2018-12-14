package scaberry.core.filter

/** Phantom type, marking serializability. */
sealed trait Ser

object Ser {
  trait Complex extends Ser
  trait Simple extends Complex
}