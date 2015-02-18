import sbt.Keys._
import sbt._
import sbtbuildinfo.Plugin._
import spray.revolver.RevolverPlugin._

object Build extends Build {

  import BuildSettings._

  lazy val deliverypipeline = Project("deliverypipeline", file("."), settings = buildInfoSettings)
    .settings(basicSettings: _*)
    .settings(libraryDependencies ++= Dependencies.deliverypipeline)
    .settings(
      sourceGenerators in Compile <+= buildInfo,
      buildInfoKeys := Seq[BuildInfoKey](name, version, BuildInfoKey.action("compiled") {
        System.currentTimeMillis
      }),
      buildInfoPackage := "org.kaloz.deliverypipeline"
    )
  Revolver.settings
}