import sbt._

object Aliases {

  lazy val exportVersionNumber = addCommandAlias("exportVersionNumber", ";runMain org.kaloz.deliverypipeline.ExportAppVersion")
}