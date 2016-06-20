package de.hs_augsburg.nlp.five.actor

import akka.actor.{Actor, ActorRef, Props}


class Subscriber(val identification: Int, val observable: ActorRef) extends Actor {
  //  val logger = LoggerFactory.getLogger(this.getClass)
  var starter: ActorRef = null

  override def receive: Receive = {
    case "start" => {
      observable ! Subscribe()
      starter = sender()
    }
    case ObservableActor.subscribeFinished => starter ! "finished"
    case message => {
      println(identification + ": received message <<" + message + ">>")
      sender() ! "finished"
    }
  }
}

object Subscriber {
  def props(identification: Int, observable: ActorRef): Props = Props(classOf[Subscriber], identification, observable)
}
