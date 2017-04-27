name := "jarvis-pure-scala"

version := "1.0"

scalaVersion := "2.11.8"

resolvers += "scalac repo" at "https://raw.githubusercontent.com/ScalaConsultants/mvn-repo/master/"

libraryDependencies += "io.scalac" %% "slack-scala-bot-core" % "0.2.1"
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.0.5"
libraryDependencies +=  "org.scalaj" %% "scalaj-http" % "2.3.0"