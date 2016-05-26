package de.hs_augsburg.nlp.four

import java.util

import de.hs_augsburg.nlp.two.BasicMonitor.Entry

import scala.collection.JavaConverters._
import scala.concurrent.stm.{Ref, TMap, atomic}

class STMBank {
  val accounts = TMap[Long, ScalaAccount]()
  val accountsView = accounts.single
  val currentAccNo = Ref(0L)

  def deposit(accNo: Long, amount: Int): Unit =
    atomic { implicit txn =>
      accounts += (accNo -> accounts.get(accNo).get.deposit(amount, "withdraw"))
    }

  def getBalance(accNo: Long): Int = accountsView.get(accNo).get.balance

  def createAccount(): Long = {
    atomic { implicit txn =>
      val accNo = currentAccNo()
      currentAccNo() = accNo + 1
      accounts += (accNo -> new ScalaAccount())
      accNo
    }
  }

  def transfer(from: Long, to: Long, amount: Int): Unit =
    atomic { implicit txn =>
      accounts +=
        (from -> accounts.get(from).get.withdraw(amount, "transfer-> withdraw"),
          to -> accounts.get(to).get.deposit(amount, "transfer-> deposit"))
    }


  def withdraw(accNo: Long, amount: Int): Unit =
    atomic { implicit txn =>
      accounts += (accNo -> accounts.get(accNo).get.withdraw(amount, "withdraw"))
    }

  def getAccountEntries(accNo: Long): util.List[Entry] = accountsView.get(accNo).get.entries.asJava

  def getAccountEntries(accNos: util.List[java.lang.Long]): util.List[Entry] = {
    atomic { implicit txn =>
      accNos.asScala.flatMap(e => accounts.get(e).get.entries).asJava
    }
  }
}
