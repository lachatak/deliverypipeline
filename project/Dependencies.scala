import sbt._

object Dependencies {

lazy val deliverypipeline = Seq(
     akka.actor,
     akka.persistence,
     akka.testkit,
     spray.can,
     spray.routing,
     spray.testkit
//     scalaz.core,
//     scalatest,
//     scalacheck
)

object akka {
    val version = "2.3.6"
    // Core Akka
    val actor                 = "com.typesafe.akka"      %% "akka-actor"                    % version
    val persistence           = "com.typesafe.akka"      %% "akka-persistence-experimental" % version
    val testkit               = "com.typesafe.akka"      %% "akka-testkit"                  % version % "test"
  }

  object spray {
    val version = "1.3.2"
    val can       = "io.spray" %% "spray-can"       % version
    val routing   = "io.spray" %% "spray-routing"   % version
    val testkit   = "io.spray" %% "spray-testkit"   % version  % "test"
  }

//  object scalaz {
//    val core = "org.scalaz" %% "scalaz-core"  % "7.1.0"
//  }

  // Testing
//  val scalatest      = "org.scalatest"      %% "scalatest"       % "2.2.1"      % "test"
//  val scalacheck     = "org.scalacheck"     %% "scalacheck"      % "1.11.6"     % "test"
  val spec2          = "org.specs2"         %%  "specs2-core"    % "2.3.11"     % "test"
}