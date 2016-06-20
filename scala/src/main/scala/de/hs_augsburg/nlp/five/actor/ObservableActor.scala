package de.hs_augsburg.nlp.five.actor

import akka.actor.{Actor, ActorRef}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._

class ObservableActor extends Actor {

  var subscribers = scala.collection.mutable.ArrayBuffer.empty[ActorRef]
  var nrUpdates = 0
  implicit val timeout = Timeout(1.second)
  implicit val exec = scala.concurrent.ExecutionContext.global

  override def receive: Receive = {
    case m: Subscribe => {
      subscribers += sender()
      sender() ! ObservableActor.subscribeFinished
    }
    case m: Update => {
      val resposeTo = sender()
      val updateNr = nrUpdates
      nrUpdates += 1
      var updatesLeft = subscribers.length
      subscribers.map(subscriber => subscriber ? m)
        .foreach(f => f.onComplete(r => {
          updatesLeft -= 1
          if (updatesLeft == 0) {
            resposeTo ! updateNr
          }
        }))

    }
  }

}

object ObservableActor {
  val subscribeFinished = "Subscribe Finished"
}
