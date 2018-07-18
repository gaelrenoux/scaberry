package bismuth.tests

import bismuth.core.Field

trait Helpers {
  object force {
    def field[Source, Type](a: Any): Field[Source, Type] = a.asInstanceOf
  }
}
