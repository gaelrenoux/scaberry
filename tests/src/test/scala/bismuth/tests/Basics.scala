package bismuth.tests

import bismuth.core.Field

class Basics {
/*
  sealed trait Animal { val weight: Long; val color: String; val name: Option[String] }
  case class Dog(color: String, weight: Long, name: Some[String]) extends Animal

  val animalWeight: Field[Animal, Long] = ???
  val animalColor: Field[Animal, String] = ???
  val animalName: Field[Animal, Option[String]] = ???

  val dogWeight: Field[Dog, Long] = ???
  val dogColor: Field[Dog, String] = ???
  val dogName: Field[Animal, Some[String]] = ???

  val dogOwner1: Field[Dog, Some[String]]
  val dogOwner2: Field[Dog, Option[String]]

  val casimir: Animal = ???
  val rex: Dog = ???

  val awc = animalWeight(casimir).get
  //val dnc = dogName(casimir).get

  val awr = animalWeight(rex).get
  val dnr = dogColor(rex).get

  val awcc = animalWeight(casimir).copy(42L)
  //val dncc = dogName(casimir).copy("")

  val awrc = animalWeight(rex).copy(42L)
  val dnrc = dogColor(rex).copy("")

  def expectsDogStringField(f: Field[Dog, String]) = ???

  expectsDogStringField(animalColor)
  expectsDogStringField(dogColor)

  def expectsDogOptionStringField(f: Field[Dog, Option[String]]) = {
    val stringOpt = f(rex).get
    f(rex).copy(None)
    f(rex).copy(Some(""))
  }

  expectsDogOptionStringField(animalName)
  expectsDogOptionStringField(dogName)*/






}
