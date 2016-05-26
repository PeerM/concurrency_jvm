package de.hs_augsburg.nlp.four

import java.lang.Long
import java.util

//implicit class for conversion of scala function to java UnaryOperator
import de.hs_augsburg.nlp.two.BasicMonitor.Entry

import scala.collection.JavaConverters._

case class ValBank(state: Map[Long, ScalaAccount] = Map(), currentAccNo: Long = 0L) {

  def deposit(accNo: Long, amount: Int) =
    ValBank(state + (accNo -> state.get(accNo).get.deposit(amount, "withdraw")), currentAccNo)

  def getBalance(accNo: Long) = state.get(accNo).get.balance

  def createAccount() = {
    ValBank(state + (currentAccNo -> new ScalaAccount()), currentAccNo + 1)
  }

  def transfer(from: Long, to: Long, amount: Int) =
    ValBank(state +
      (from -> state.get(from).get.withdraw(amount, "transfer-> withdraw"),
        to -> state.get(to).get.deposit(amount, "transfer-> deposit")), currentAccNo)

  def withdraw(accNo: Long, amount: Int) =
    ValBank(state + (accNo -> state.get(accNo).get.withdraw(amount, "withdraw")), currentAccNo)

  def getAccountEntries(accNo: Long): util.List[Entry] = state.get(accNo).get.entries.asJava

  def getAccountEntries(accNos: util.List[Long]): util.List[Entry] = {
    accNos.asScala.flatMap(e => state.get(e).get.entries).asJava
  }
}

object ValBank {
  val empty = ValBank()
}

