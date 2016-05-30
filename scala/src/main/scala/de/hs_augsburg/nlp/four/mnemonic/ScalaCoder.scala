package de.hs_augsburg.nlp.four.mnemonic

//noinspection ScalaUnnecessaryParentheses,JavaAccessorMethodCalledAsEmptyParen
class ScalaCoder(dictionary: List[String]) {
  private val mnemonics = Map(
    '2' -> "ABC", '3' -> "DEF", '4' -> "GHI", '5' -> "JKL",
    '6' -> "MNO", '7' -> "PQRS", '8' -> "TUV", '9' -> "WXYZ")

  /** invert mnemonic to Map 'A'->'2', 'B'->'2', 'C'->'2','D'->'3', ... */
  private val charCode: Map[Char, Char] =
    for ((digit, str) <- mnemonics; ltr <- str) yield (ltr -> digit)

  /** maps word to the digit string along mnemonics */
  private def wordCode(word: String): String = word.toUpperCase map charCode

  /** the map from digit strings to sets of words they respresent,
    * e. g. "5282" -> Set("Java","Kata","Lava") */
  private val wordsForNum: Map[String, List[String]] =
    (dictionary groupBy wordCode) withDefaultValue List()

  /** return all ways to encode a number as a list of words */
  def encode(number: String): Set[List[String]] =
    if (number.isEmpty()) Set(List())
    else {
      for {
        splitPoint <- 1 to number.length
        word <- wordsForNum(number take splitPoint)
        rest <- encode(number drop splitPoint)
      } yield word :: rest
    }.toSet

  /** return all word phrases separated by blank that represent the number */
  def translate(number: String): Set[String] = encode(number) map (_ mkString " ")
}