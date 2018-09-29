package scaberry.oldmacros

import scaberry.oldmacros.impl.ScaberryImpl

import scala.annotation.{StaticAnnotation, compileTimeOnly}

@compileTimeOnly("enable macro paradise to expand macro annotations")
class scaberry(name: Symbol) extends StaticAnnotation {
  def macroTransform(annottees: Any*) = macro ScaberryImpl.impl
}