package de.hs_augsburg.nlp.five.actor

import akka.actor.{Actor, ActorRef, Props}


class Subscriber(val identification: Int, val observable: ActorRef) extends Actor {
  //  val logger = LoggerFactory.getLogger(this.getClass)

  override def receive: Receive = {
    case "start" => observable ! Subscribe()
    case message => println(identification + ": received message <<" + message + ">>")
  }
}

object Subscriber {
  def props(identification: Int, observable: ActorRef): Props = Props(classOf[Subscriber], identification, observable)
}
