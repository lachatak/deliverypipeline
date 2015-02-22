resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("io.spray" % "sbt-revolver" % "0.7.2")

addSbtPlugin("se.marcuslonnberg" % "sbt-docker" % "0.5.2")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.3.2")

resolvers += "jgit-repo" at "http://download.eclipse.org/jgit/maven"

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.6.4")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.0.4")