package org.kaloz.deliverypipeline

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import spray.can.Http

import scala.collection.JavaConverters._
import scala.concurrent.duration._

object Boot extends App {

  val config: String = System.getenv().asScala.getOrElse("DELIVERY_CONF", "application.conf")

  val factory = ConfigFactory.load(config)

  println(System.getenv())
  println(System.getenv().get("DELIVERY_CONF"))
  println(config)

  if (config.startsWith("/app")) {
    io.Source.fromFile(config).foreach {
      print
    }
  }

  implicit val system = ActorSystem("deliverypipeline", factory)

  val service = system.actorOf(Props[DeliverypipelineService], "deliverypipeline-service")

  implicit val timeout = Timeout(5.seconds)
  IO(Http) ? Http.Bind(service, interface = "0.0.0.0", port = 8080)
}
