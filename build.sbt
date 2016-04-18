organization  := "tptfc"

version       := "0.1"

scalaVersion  := "2.11.7"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  Seq()
}

libraryDependencies <+= (scalaVersion) { sv => "org.scala-lang" % "scala-reflect" % sv}
