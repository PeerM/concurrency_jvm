package de.hs_augsburg.nlp.five.actor

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

object Scenario extends App {
  val actorSystem = ActorSystem("myActors")
  val observable = actorSystem.actorOf(Props(classOf[ObservableActor]), "Observable")
  val subscribers = List(
    actorSystem.actorOf(Subscriber.props(0, observable)),
    actorSystem.actorOf(Subscriber.props(1, observable)),
    actorSystem.actorOf(Subscriber.props(2, observable))
  )
  implicit val timeout = Timeout(1.second)
  subscribers.foreach(subscriber => subscriber ! "start")
  // TODO proper synchronization
  Thread.sleep(200)
  observable ! Update("hello")
  Thread.sleep(200)
  actorSystem.terminate()
}
