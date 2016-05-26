package de.hs_augsburg.nlp.four

import java.util.function.UnaryOperator


class UnaryOpScala[T](val function: (T) => T) extends UnaryOperator[T] {
  override def apply(t: T): T = function(t)
}
