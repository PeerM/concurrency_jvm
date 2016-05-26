package de.hs_augsburg.nlp.four

import java.lang.Long
import java.util
import java.util.concurrent.atomic.{AtomicLong, AtomicReference}

//implicit class for conversion of scala function to java UnaryOperator
import de.hs_augsburg.nlp.four.UnaryOpScala.UnaryOpScala
import de.hs_augsburg.nlp.two.BasicMonitor.Entry

import scala.collection.JavaConverters._

class AtomicBank {
  val state = new AtomicReference(Map[Long, ScalaAccount]())
  val accNoGen = new AtomicLong()

  def deposit(accNo: Long, amount: Int): Unit =
    state.updateAndGet((last: Map[Long, ScalaAccount]) => last + (accNo -> last.get(accNo).get.deposit(amount, "withdraw")))

  def getBalance(accNo: Long): Int = state.get().get(accNo).get.balance

  def createAccount(): Long = {
    val accNo = accNoGen.getAndIncrement()
    state.updateAndGet(new UnaryOpScala[Map[Long, ScalaAccount]]((last) => last updated(accNo, new ScalaAccount())))
    accNo
  }

  def transfer(from: Long, to: Long, amount: Int): Unit =
    state.updateAndGet((last: Map[Long, ScalaAccount]) => last +
      (from -> last.get(from).get.withdraw(amount, "transfer-> withdraw"),
        to -> last.get(to).get.deposit(amount, "transfer-> deposit")))


  def withdraw(accNo: Long, amount: Int): Unit =
    state.updateAndGet((last: Map[Long, ScalaAccount]) => last + (accNo -> last.get(accNo).get.withdraw(amount, "withdraw")))

  def getAccountEntries(accNo: Long): util.List[Entry] = state.get().get(accNo).get.entries.asJava

  def getAccountEntries(accNos: util.List[Long]): util.List[Entry] = {
    val localState = state.get()
    accNos.asScala.flatMap(e => localState.get(e).get.entries).asJava
  }
}
