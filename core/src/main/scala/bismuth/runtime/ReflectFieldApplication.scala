package bismuth.runtime

import bismuth.core.FieldApplication

class ReflectFieldApplication[Target, Type](
                                           _get: => Type,
                                           _copy: Type => Target
                                           ) extends FieldApplication[Target, Type] {

  def get: Type = _get

  def copy(a: Type): Target = _copy(a)
}
