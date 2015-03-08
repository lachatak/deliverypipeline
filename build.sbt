import com.typesafe.sbt.SbtGit._
import sbtdocker.ImageName
import sbtdocker.Plugin.DockerKeys._
import sbtdocker.mutable.Dockerfile

versionWithGit

git.baseVersion := "1.0.0"

dockerSettings

mainClass in assembly := Some("org.kaloz.deliverypipeline.Boot")

docker <<= (docker dependsOn assembly)

dockerfile in docker := {
  val artifact = (outputPath in assembly).value
  val artifactTargetPath = s"/app/${artifact.name}"
  new Dockerfile {
    from("dockerfile/java")
    expose(8080)
    add(artifact, artifactTargetPath)
    entryPoint("java", "-jar", "-Dlogback.configurationFile=/app/logback.xml", "-Dakka.config=/app/application.conf", artifactTargetPath)
  }
}

imageName in docker := {
  ImageName(
    namespace = Some("lachatak"),
    repository = name.value,
    tag = Some(version.value))
}
