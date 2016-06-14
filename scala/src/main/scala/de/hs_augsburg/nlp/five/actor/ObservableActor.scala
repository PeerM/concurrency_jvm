package de.hs_augsburg.nlp.five.actor

import akka.actor.{Actor, ActorRef}


class ObservableActor extends Actor {

  var subscribers = scala.collection.mutable.ArrayBuffer.empty[ActorRef]

  override def receive: Receive = {
    case m: Subscribe =>
      subscribers += sender()
    case m: Update => subscribers.foreach(subscriber => subscriber ! m)
  }

}
