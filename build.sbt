
name := "service-p2p"
organization := "com.revolut.service"
version := "0.0.1"
scalaVersion := "2.12.7"

lazy val compilerOptions = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Xfuture",
  "-Xlint"
)

lazy val versions = new {
  val finatra = "18.12.0"
  val finagle = "18.12.0"
}

libraryDependencies ++= Seq(
  "com.twitter" %% "finatra-http" % versions.finatra,
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "org.scalikejdbc" %% "scalikejdbc" % "3.3.2",
  "com.h2database" % "h2" % "1.4.197",

//  "com.twitter" %% "finatra-http" % versions.finatra % "test" classifier "tests",
//  "com.twitter" %% "finatra-http" % versions.finatra % "test",
//  "com.twitter" %% "inject-core" % versions.finatra % "test" classifier "tests",
//  "com.twitter" %% "inject-core" % versions.finatra % "test",
//  "com.twitter" %% "inject-modules" % versions.finatra % "test",
//  "com.twitter" %% "inject-modules" % versions.finatra % "test" classifier "tests",
//  "com.twitter" %% "inject-app" % versions.finatra % "test",
//  "com.twitter" %% "inject-app" % versions.finatra % "test" classifier "tests",
//  "com.twitter" %% "inject-server" % versions.finatra % "test",
//  "com.twitter" %% "inject-server" % versions.finatra % "test" classifier "tests",

  "org.scalacheck" %% "scalacheck" % "1.14.0",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)

assemblyJarName in assembly := name.value + ".jar"
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_ *) => MergeStrategy.discard
  case _ => MergeStrategy.first
}
