package bismuth.tests

import bismuth.core.Field
import Models._

trait Helpers {
  object force {
    def field[Source, Type](a: Any): Field[Source, Type] = a.asInstanceOf[Field[Source, Type]]
    def af(a: Any): Field[Animal, Any] = field[Animal, Any](a)
  }
}
