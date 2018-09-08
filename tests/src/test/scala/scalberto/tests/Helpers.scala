package scalberto.tests

import scalberto.tests.data._
import scalberto.core.{CopyableField, Field}

trait Helpers {
  object force {
    /** Readable field */
    def rf[Source, Type](a: Any): Field[Source, Type] = a.asInstanceOf[Field[Source, Type]]

    /** Copyable field */
    def cf[Source, Type](a: Any): CopyableField[Source, Type] = a.asInstanceOf[CopyableField[Source, Type]]

    /** Animal readable field */
    def arf(a: Any): Field[Animal, Any] = rf[Animal, Any](a)

    /** Animal readable field */
    def dcf[A](a: Any): CopyableField[Dog, A] = cf[Dog, A](a)
  }
}
