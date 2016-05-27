package de.hs_augsburg.nlp.four.mnemonic

import scala.collection.JavaConverters._

class CoderAdapter(dictionary: java.util.List[String]) {
  val coder = new ScalaCoder(dictionary.asScala.toList)

  /** return all ways to encode a number as a list of words */
  def encode(number: String) = coder.encode(number).map((list)=>list.asJava).asJava

  /** return all word phrases separated by blank that represent the number */
  def translate(number: String) = coder.translate(number).asJava
}


