package de.hs_augsburg.nlp.five.actor

import akka.actor.{ActorSystem, Props}

object Scenario extends App {
  val actorSystem = ActorSystem("myActors")
  val observable = actorSystem.actorOf(Props(classOf[ObservableActor]), "Observable")
  actorSystem.actorOf(Subscriber.props(0, observable))
  actorSystem.actorOf(Subscriber.props(1, observable))
  actorSystem.actorOf(Subscriber.props(2, observable))
  observable ! Update("hello")
  actorSystem.terminate()
}
