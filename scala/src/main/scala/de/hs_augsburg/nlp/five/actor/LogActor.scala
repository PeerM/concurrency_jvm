package de.hs_augsburg.nlp.five.actor

import akka.actor.Actor
import org.slf4j.LoggerFactory


class LogActor extends Actor {
  override def receive: Receive = {
    // type Actor.Receive, alias of PartialFunction[Any,Unit]
    case message => LoggerFactory.getLogger(this.getClass).info("received message <<" + message + ">>")
  }
}
