package org.kaloz.deliverypipeline

import java.util.Date

import akka.actor.ActorLogging
import akka.persistence.{PersistentActor, SnapshotOffer}
import spray.http.MediaTypes._
import spray.routing._

case class ServiceState(calls: Int = 0, deployHistory: List[Date] = List(new Date)) {
  def call = copy(calls = calls + 1)

  def deployment = copy(deployHistory = new Date :: deployHistory)
}

class DeliverypipelineService extends PersistentActor with ActorLogging with DeliverypipelineRoute {

  override val persistenceId: String = "deliverypipeline"

  var state = ServiceState()

  def deployHistory = state.deployHistory

  def calls() = {
    state = state.call
    log.info(s"save state: $state")
    saveSnapshot(state)
    state.calls
  }

  def actorRefFactory = context

  override def receiveRecover: Receive = {
    case SnapshotOffer(_, offeredSnapshot: ServiceState) =>
      log.info(s"offer state: $offeredSnapshot")
      state = offeredSnapshot.deployment
  }

  override def receiveCommand: Receive = runRoute(myRoute)

}

trait DeliverypipelineRoute extends HttpService {

  def deployHistory: List[Date]

  def calls(): Int

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
              </body>
            </html>
          }
        }
      }
    }
}
