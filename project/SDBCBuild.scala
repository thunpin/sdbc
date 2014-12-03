import sbt._
import Keys._

object SDBCBuild extends Build {
  lazy val sdbc = Project(
    id = "sdbc",
    base = file("."),
    settings = (Project.defaultSettings ++ Seq(
      name := "sdbc",
      organization := "br.tptfc",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.11.2",
      resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
      resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
      libraryDependencies += "com.jolbox" % "bonecp" % "0.8.0.RELEASE"
    )).toSeq
  )
}
