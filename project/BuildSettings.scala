import sbt._
import Keys._

object BuildSettings {

  lazy val basicSettings = Seq(
    version := "1.0.0-SNAPSHOT",
    name := "deliverypipeline",
    organization := "org.kaloz",
    description := "Pet project for deliverypipeline test with Werker + Docker + EC2 + Elastic Beanstalk",
    scalaVersion := "2.11.4",
    scalacOptions := Seq(
      "-encoding", "utf8",
      "-feature",
      "-unchecked",
      "-deprecation",
      "-target:jvm-1.7",
      "-language:postfixOps",
      "-language:implicitConversions"
    )
  ) 
}