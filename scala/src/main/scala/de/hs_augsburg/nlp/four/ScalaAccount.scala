package de.hs_augsburg.nlp.four

import java.time.Instant

import de.hs_augsburg.nlp.two.BasicMonitor.{Entry, EntryType}


case class ScalaAccount(balance: Int = 0, lastModified: Instant = Instant.now(), entries: Vector[Entry] = Vector()) {
  def deposit(amount: Int, text: String) = ScalaAccount(balance + amount, entries = entries :+ new Entry(text, amount, EntryType.DEPOSIT))

  def withdraw(amount: Int, text: String) = ScalaAccount(balance - amount, entries = entries :+ new Entry(text, amount, EntryType.WITHDRAW))
}
