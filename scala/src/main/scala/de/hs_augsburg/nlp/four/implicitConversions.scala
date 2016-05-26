package de.hs_augsburg.nlp.four

import java.util.function.UnaryOperator


object implicitConversions {

  /***
    * implicit class for conversion of scala function to java UnaryOperator
    * @param function the function to map to a UnaryOperator
    * @tparam T type of the operator
    */
  implicit class UnaryOpScala[T](val function: (T) => T) extends UnaryOperator[T] {
    override def apply(t: T): T = function(t)
  }
}
