package org.kaloz.deliverypipeline

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import spray.can.Http

import scala.concurrent.duration._

object Boot extends App {

  val conf = System.getProperty("DELIVERY_CONF", "application.conf")

  println(System.getenv())
  println(conf)
  implicit val system = ActorSystem("deliverypipeline", ConfigFactory.load(System.getProperty("DELIVERY_CONF", "application.conf") ))

  val service = system.actorOf(Props[DeliverypipelineService], "deliverypipeline-service")

  implicit val timeout = Timeout(5.seconds)
  IO(Http) ? Http.Bind(service, interface = "0.0.0.0", port = 8080)
}
