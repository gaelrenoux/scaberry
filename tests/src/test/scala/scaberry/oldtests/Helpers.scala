package scaberry.oldtests

import scaberry.core.{CopyableField, Field}

trait Helpers {

  object force {
    /** Readable field */
    def rf[Source, Type](a: Any): Field[Source, Type] = a.asInstanceOf[Field[Source, Type]]

    /** Copyable field */
    def cf[Source, Type](a: Any): CopyableField[Source, Type] = a.asInstanceOf[CopyableField[Source, Type]]

    /** Animal readable field */
    def arf(a: Any): Field[data.Animal, Any] = rf[data.Animal, Any](a)

    /** Animal readable field */
    def dcf[A](a: Any): CopyableField[data.Dog, A] = cf[data.Dog, A](a)
  }

}
