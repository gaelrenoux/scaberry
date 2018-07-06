import bismuth.`macro`.FieldsMacro
import com.sun.tools.javac.code.TypeTag

package object bismuth {

  def fields[A](implicit a: TypeTag[A]) = macro FieldsMacro.impl

}
