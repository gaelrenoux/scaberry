package bismuth.core

trait Patch[Target] extends Function1[Target, Target] {

  def update(t: Target): Target

  def apply(t: Target): Target = update(t)

}


object Patch {

  private class NoChangeImpl[T] extends Patch[T] {
    override def update(t: T): T = t
  }

  /** Def masquerading as an object */
  def NoChange[T]: Patch[T] = new NoChangeImpl[T]

  implicit class NewValue[T](value: T) extends Patch[T] {
    override def update(target: T): T = value
  }

  implicit class Operation[T](op: T => T) extends Patch[T] {
    override def update(target: T): T = op(target)
  }


}