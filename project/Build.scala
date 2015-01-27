import sbt.Keys._
import sbt._
import spray.revolver.RevolverPlugin._

object Build extends Build {

  import BuildSettings._

  lazy val deliverypipeline = Project("deliverypipeline", file("."))
    .settings(basicSettings: _*)
    .settings(libraryDependencies ++= Dependencies.deliverypipeline)

  Revolver.settings
}