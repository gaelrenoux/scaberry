package bismuth.core

trait Update[Target] extends Function1[Target, Target] {

  def update(t: Target): Target

  def apply(t: Target): Target = update(t)

}


object Update {

  private class NoChangeImpl[T] extends Update[T] {
    override def update(t: T): T = t
  }

  /** Def masquerading as an object */
  def Identity[T]: Update[T] = new NoChangeImpl[T]

  implicit class NewValue[T](value: T) extends Update[T] {
    override def update(target: T): T = value
  }

  implicit class Operation[T](op: T => T) extends Update[T] {
    override def update(target: T): T = op(target)
  }

  /** Trait for Updates created for case objects through the macro. */
  trait Composite[T] extends Update[T]

}