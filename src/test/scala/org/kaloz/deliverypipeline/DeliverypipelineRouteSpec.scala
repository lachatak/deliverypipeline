package org.kaloz.deliverypipeline

import java.util.Date

import org.specs2.mutable.Specification
import spray.http.StatusCodes._
import spray.testkit.Specs2RouteTest

class DeliverypipelineRouteSpec extends Specification with Specs2RouteTest with DeliverypipelineRoute {
  def actorRefFactory = system

  override def deployHistory: List[Date] = List(new Date())

  override def calls(): Int = 3

  override val hostname = "localhost"

  "DeliverypipelineRoute" should {

    "return a the deploy history for GET requests to the root path" in {
      Get() ~> myRoute ~> check {
        responseAs[String] must contain("Deploy history")
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/nothing") ~> myRoute ~> check {
        handled must beFalse
      }
    }

    "return a MethodNotAllowed error for PUT requests to the root path" in {
      Put() ~> sealRoute(myRoute) ~> check {
        status === MethodNotAllowed
        responseAs[String] === "HTTP method not allowed, supported methods: GET"
      }
    }
  }

}
