organization  := "tptfc"

version       := "0.1"

scalaVersion  := "2.11.2"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  Seq(
    "com.jolbox" % "bonecp" % "0.8.0.RELEASE"
  )
}