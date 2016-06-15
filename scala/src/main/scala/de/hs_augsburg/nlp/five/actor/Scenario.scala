package de.hs_augsburg.nlp.five.actor

import akka.actor.{ActorSystem, PoisonPill, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object Scenario extends App {
  val actorSystem = ActorSystem("myActors")
  val observable = actorSystem.actorOf(Props(classOf[ObservableActor]), "Observable")
  val subscribers = Range(1,5).map(i => actorSystem.actorOf(Subscriber.props(i, observable)))
  implicit val timeout = Timeout(1.second)
  private val futures: Seq[Future[Any]] = subscribers.map(subscriber => ask(subscriber, "start"))
  private val results: Seq[Any] = futures.map(f => Await.result(f, 1.second))

  observable ! Update("hello")
  val pill = PoisonPill.getInstance
//  observable ! pill
//  subscribers.foreach(f => f ! pill)
  actorSystem.terminate()
}
