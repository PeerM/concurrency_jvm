package de.hs_augsburg.nlp.five.actor

import akka.actor.{Actor, ActorRef, Props}


class Subscriber(val identification: Int, val observable: ActorRef) extends Actor {
  //  val logger = LoggerFactory.getLogger(this.getClass)
  observable ! Subscribe()

  override def receive: Receive = {
    case message => println(identification + ": received message <<" + message + ">>")
  }
}

object Subscriber {
  def props(identification: Int, observable: ActorRef): Props = Props(classOf[Subscriber], identification, observable)
}
