import bismuth.`macro`.FieldsMacroImpl
import bismuth.core.Fields

import scala.language.experimental.macros

package object bismuth {

  def constructorFields[Source]: Fields[Source] = macro FieldsMacroImpl.constructorFields[Source]

}
