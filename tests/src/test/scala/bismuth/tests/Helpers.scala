package bismuth.tests

import bismuth.core.Field
import Models._

trait Helpers {
  object force {
    /** Readable field */
    def rf[Source, Type](a: Any): Field.Readable[Source, Type] = a.asInstanceOf[Field.Readable[Source, Type]]

    /** Copyable field */
    def cf[Source, Type](a: Any): Field.Copyable[Source, Type] = a.asInstanceOf[Field.Copyable[Source, Type]]

    /** Animal readable field */
    def arf(a: Any): Field.Readable[Animal, Any] = rf[Animal, Any](a)

    /** Animal readable field */
    def dcf[A](a: Any): Field.Copyable[Dog, A] = cf[Dog, A](a)
  }
}
