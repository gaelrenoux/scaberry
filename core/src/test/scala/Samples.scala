import scalberto.core.{CopyableField, Filter, Update}

object Samples {

  case class Person(name: String, age: Long)

  val nameCopier: (Person, String) => Person =  { (p, n) => p.copy(name = n) }
  val ageCopier: (Person, Long) => Person =  { (p, a) => p.copy(age = a) }

  object PersonFields {
    val name = new CopyableField[Person, String]('name, _.name, nameCopier)
    val age = new CopyableField[Person, Long]('age, _.age, ageCopier)
  }

  val aFilter: Filter[Person] = PersonFields.name.filterEq("Roger") |@| PersonFields.age.filterWith(_ > 18)

  val anUpdate: Update[Person] = PersonFields.name.updateSet("Roger") |@| PersonFields.age.updateWith(_ * 2)

  case class PersonFilter(
                           name: Filter[String] = Filter.None,
                           age: Filter[Long] = Filter.None
                         ) extends Filter.Custom[Person] {
    override def verify(p: Person): Boolean = name(p.name) && age(p.age)
  }


  case class PersonUpdate(
                           name: Update[String] = Update.identity,
                           age: Update[Long] = Update.identity
                         )


}
