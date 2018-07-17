package bismuth.core

trait FieldApplication [Target, Type] {

  def get: Type

  def copy(a: Type): Target
}
