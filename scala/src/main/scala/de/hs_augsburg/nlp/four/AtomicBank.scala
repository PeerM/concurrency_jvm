package de.hs_augsburg.nlp.four

import java.lang.Long
import java.util
import java.util.concurrent.atomic.AtomicReference

import de.hs_augsburg.nlp.two.BasicMonitor.Entry
import de.hs_augsburg.nlp.two.Functional.{AtomicNumberGenerator, ImmutableAccount}
import de.hs_augsburg.nlp.two.IBank

class AtomicBank {
  val state = new AtomicReference(Map[Long, ImmutableAccount]())
  val accnoGen = new AtomicNumberGenerator()

  def deposit(accNo: Long, amount: Int): Unit =
    state.updateAndGet(new UnaryOpScala[Map[Long, ImmutableAccount]](
    (last) => last updated(accNo, last.getOrElse(accNo, new ImmutableAccount()).deposit(amount, "withdraw"))))

  def getBalance(accNo: Long): Int = state.get().getOrElse(accNo, new ImmutableAccount()).currentBalance()

  def createAccount(): Long = {
    val accno = accnoGen.getNext
    state.updateAndGet(new UnaryOpScala[Map[Long, ImmutableAccount]]((last) => last updated(accno, new ImmutableAccount())))
    //    state.updateAndGet(new UnaryOperator[Map[Long, ImmutableAccount]] {
    //      override def apply(t: Map[Long, ImmutableAccount]): Map[Long, ImmutableAccount] = {
    //        t updated(accno, new ImmutableAccount())
    //      }
    //    })
    accno
  }

  def transfer(from: Long, to: Long, amount: Int): Unit = return

  def withdraw(accNo: Long, amount: Int): Unit =
    state.updateAndGet(new UnaryOpScala[Map[Long, ImmutableAccount]](
      (last) => last updated(accNo, last.getOrElse(accNo, new ImmutableAccount()).withdraw(amount, "withdraw"))))

  def getAccountEntries(accNo: Long): util.List[Entry] = new util.LinkedList[Entry]

  def getAccountEntries(accNo: util.List[Long]): util.List[Entry] = new util.LinkedList[Entry]
}
