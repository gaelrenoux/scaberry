package scaberry.core


case class Person(name: String, age: Long)

object Person {

  val nameCopier: (Person, String) => Person = {
    (p, n) => p.copy(name = n)
  }

  val ageCopier: (Person, Long) => Person = {
    (p, a) => p.copy(age = a)
  }

  object Fields {
    val name = new CopyableField[Person, String]('name, _.name, nameCopier)
    val age = new CopyableField[Person, Long]('age, _.age, ageCopier)
  }

  case class CustomFilter(
                           name: Filter[String] = Filter.None,
                           age: Filter[Long] = Filter.None
                         ) extends Filter.Custom[Person] {
    override def verify(p: Person): Boolean = name(p.name) && age(p.age)
  }


  case class CustomUpdate(
                           name: Update[String] = Update.identity,
                           age: Update[Long] = Update.identity
                         )

}