import sbtdocker.ImageName
import sbtdocker.Plugin.DockerKeys._
import sbtdocker.mutable.Dockerfile

dockerSettings

//val gitHeadCommitSha = settingKey[String]("current git commit SHA")
//
//gitHeadCommitSha in ThisBuild := Process("git rev-parse HEAD").lines.head
//
//version in ThisBuild := gitHeadCommitSha.value

mainClass in assembly := Some("org.kaloz.deliverypipeline.Boot")

docker <<= (docker dependsOn assembly)

dockerfile in docker := {
  val artifact = (outputPath in assembly).value
  val artifactTargetPath = s"/app/${artifact.name}"
  new Dockerfile {
    from("dockerfile/java")
    expose(8080)
    volume("/tmp/deliverypipeline/snapshots")
    add(artifact, artifactTargetPath)
    entryPoint("java", "-jar", artifactTargetPath)
  }
}

imageName in docker := {
ImageName(
  namespace = Some("lachatak"),
repository = name.value,
tag = Some(version.value))
}
