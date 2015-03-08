package org.kaloz.deliverypipeline

import java.net.InetAddress
import java.util.Date

import akka.actor.ActorLogging
import akka.persistence.{PersistentActor, SnapshotOffer}
import spray.http.MediaTypes._
import spray.routing._

case class ServiceState(calls: Int = 0, deployHistory: List[Date] = List(new Date)) {
  def call = copy(calls = calls + 1)

  def deployment = copy(deployHistory = new Date :: deployHistory.take(9))
}

class DeliverypipelineService extends PersistentActor with ActorLogging with DeliverypipelineRoute {

  override val persistenceId: String = "deliverypipeline"

  var state = ServiceState()

  val hostname: String = InetAddress.getLocalHost.getHostName

  def deployHistory = state.deployHistory

  def calls(): Int = {
    state = state.call
    saveSnapshot(state)
    val calls = state.calls
    log.info(s"Called: $calls")
    calls
  }

  def actorRefFactory = context

  override def receiveRecover: Receive = {
    case SnapshotOffer(_, offeredSnapshot: ServiceState) =>
      state = offeredSnapshot.deployment
      log.info(s"Restarted: $offeredSnapshot")
  }

  override def receiveCommand: Receive = runRoute(myRoute)


}

trait DeliverypipelineRoute extends HttpService {

  def deployHistory: List[Date]

  def calls(): Int

  def hostname: String

  val myRoute =
    path("") {
      get {
        respondWithMediaType(`text/html`) {
          complete {
            <html>
              <body>
                <h1>Deploy history:
                  <ul>
                    {deployHistory.map(d => <li>
                    {d}
                  </li>)}
                  </ul>
                </h1>
                <h1>Aggregated calls
                  <i>
                    {calls()}
                  </i>
                </h1>
                <h1>Current Revision
                  <i>
                    {BuildInfo.version}
                  </i>
                </h1>
                <h1>Host name
                  <i>
                    {hostname}
                  </i>
                </h1>
              </body>
            </html>
          }
        }
      }
    }
}
