# Scaberry fields forever (WIP)

Scaberry is a library to reify fields on case classes. This allows for
the creation of filter or update objects without any boilerplate. It
also allows for easy access to the annotations on your fields, in case
you want to do something with that.

The base element is the simple ```@berry``` annotation. Just put it on
your case class and voilà:

```scala
@berry
case class Dog(name: String, owner: Option[String], good: Boolean = true)
```

You now have access on the companion object to a ```meta``` value with
all the fields of the class. Those fields can be applied on instance,
combined to form filters or updates…

```scala
val inouk = Dog("Inouk", Some("Gael"))
// inouk: scaberry.tests.models.Dog = Dog(Inouk,Some(Gael),true)

val dogName = Dog.meta.fields.name
// dogName: scaberry.tests.models.Dog.meta.fields.name.type = ...

dogName(inouk).get
// res0: String = Inouk

dogName(inouk).copy("Rex")
// res1: scaberry.tests.models.Dog = Dog(Rex,Some(Gael),true)

val filterName = dogName.filterEq("Inouk")
// filterName: scaberry.core.Filter.Field[scaberry.tests.models.Dog,String] = <function1>

filterName(inouk)
//res2: Boolean = true

val filterMultiple = filterName |@| Dog.meta.fields.owner.filterWith(_.isEmpty)
// filterMultiple: scaberry.core.Filter[scaberry.tests.models.Dog] = <function1>

filterMultiple(inouk)
//res3: Boolean = false

val updateName = dogName.updateSet("Rex")
// updateName: scaberry.core.Update.Field[scaberry.tests.models.Dog,String] = <function1>

updateName(inouk)
// res4: scaberry.tests.models.Dog = Dog(Rex,Some(Gael),true)

val updateMultiple = updateName |@| Dog.meta.fields.good.updateWith(!_)
// updateMultiple: scaberry.core.Update[scaberry.tests.models.Dog] = <function1>

updateMultiple(inouk)
// res5: scaberry.tests.models.Dog = Dog(Rex,Some(Gael),false)
```

## Filter

Let's say you have a case class:

Let's say you want a filter for it, to pass as an argument to your
```search``` function. A few common approaches are:
- Using a different argument in the ```search``` method for each field,
all of them optional. This is highly boilerplatey, scales badly and is
hard to maintain, especially when you realize all functions calling
```search``` are going to need the exact same arguments. Next!
- Using the class ```Dog``` itself as your filter. This is way better,
but you run into another problem: how not to filter on some field you
want to ignore ?
- Using an anonymous function as your filter. This works and is very
flexible, but how do you serialize that to JSON or whatever exchange
format you use ?
- Using a specific ```Filter``` class. It works, is a lot of boilerplate
and is what Scaberry does for you !

JSON ser/deserialization in progress…


## Known problems

### IntelliJ integration

A few things are not well handled by IntelliJ and appear as compile
errors, although they do actually compile:
- renaming the meta value on the companion object;
- access to the BerryProps declared on fields;
- secondary constructors on the class annotated with @berry.

