package de.hs_augsburg.nlp.four

import java.lang.Long
import java.util
import java.util.concurrent.atomic.AtomicReference

import de.hs_augsburg.nlp.two.BasicMonitor.Entry
import de.hs_augsburg.nlp.two.Functional.{AtomicNumberGenerator, ImmutableAccount}

import scala.collection.JavaConverters._

class AtomicBank {
  val state = new AtomicReference(Map[Long, ImmutableAccount]())
  val accnoGen = new AtomicNumberGenerator()

  def deposit(accNo: Long, amount: Int): Unit =
    state.updateAndGet(new UnaryOpScala[Map[Long, ImmutableAccount]](
      (last) => last + (accNo -> last.get(accNo).get.deposit(amount, "withdraw"))))

  def getBalance(accNo: Long): Int = state.get().get(accNo).get.currentBalance()

  def createAccount(): Long = {
    val accno = accnoGen.getNext
    state.updateAndGet(new UnaryOpScala[Map[Long, ImmutableAccount]]((last) => last updated(accno, new ImmutableAccount())))
    accno
  }

  def transfer(from: Long, to: Long, amount: Int): Unit =
    state.updateAndGet(new UnaryOpScala[Map[Long, ImmutableAccount]](
      (last) => last +(from -> last.get(from).get.withdraw(amount, "transfer-> withdraw"),
        to -> last.get(to).get.deposit(amount, "transfer-> deposit"))))


  def withdraw(accNo: Long, amount: Int): Unit =
    state.updateAndGet(new UnaryOpScala[Map[Long, ImmutableAccount]](
      (last) => last + (accNo -> last.get(accNo).get.withdraw(amount, "withdraw"))))

  def getAccountEntries(accNo: Long): util.List[Entry] = state.get().get(accNo).get.getEntries

  def getAccountEntries(accNos: util.List[Long]): util.List[Entry] = {
    val localState = state.get()
    accNos.asScala.flatMap(e => localState.get(e).get.getEntries.asScala).asJava
  }
}
