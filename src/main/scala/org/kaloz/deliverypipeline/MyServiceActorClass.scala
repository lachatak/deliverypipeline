package org.kaloz.deliverypipeline

import java.util.Date

import akka.actor.Actor
import spray.http.MediaTypes._
import spray.routing._

class MyServiceActor extends Actor with MyService {

  def actorRefFactory = context

  def receive = runRoute(myRoute)
}


trait MyService extends HttpService {

  val deployTime = new Date

  val myRoute =
    path("") {
      get {
        respondWithMediaType(`text/html`) {
          complete {
            <html>
              <body>
                <h1>Last deploytime is
                  <i>$
                    {deployTime}
                  </i>
                  !</h1>
              </body>
            </html>
          }
        }
      }
    }
}
