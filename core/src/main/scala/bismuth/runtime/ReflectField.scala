package bismuth.runtime

import bismuth.core.Field

import scala.reflect.ClassTag
import scala.reflect.runtime.{universe => ru}


case class ReflectField[-Source, Type](
                                        name: String,
                                        //sourceType: TypeTag[Source],
                                        //fieldType: TypeTag[Type]
                                      )(implicit mirror: ru.Mirror) extends Field[Source, Type] {

  def apply[Target <: Source : ClassTag : ru.TypeTag](target: Target): ReflectFieldApplication[Target, Type] = {
    val mTarget = mirror.reflect(target)
    val get = ru.typeOf[Target].decl(ru.TermName(name)).asTerm
    val mGet = mTarget.reflectField(get)
    new ReflectFieldApplication[Target, Type](mGet.get.asInstanceOf[Type], t => ???)
  }

}

