package org.kaloz.deliverypipeline

import java.io.File

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import spray.can.Http

import scala.collection.JavaConverters._
import scala.concurrent.duration._

object Boot extends App {

  val config: String = System.getProperty("akka.config", "")

  val factory = ConfigFactory.parseFile(new File(config)).withFallback(ConfigFactory.load)

  implicit val system = ActorSystem("deliverypipeline", factory)

  val service = system.actorOf(Props[DeliverypipelineService], "deliverypipeline-service")

  implicit val timeout = Timeout(5.seconds)
  IO(Http) ? Http.Bind(service, interface = "0.0.0.0", port = 8080)
}
