import sbt._

object Dependencies {
  // Runtime
  lazy val scopt               = "com.github.scopt"  %% "scopt"             % "4.0.1"
  lazy val jsonoid             = "edu.rit.cs"        %% "jsonoid-discovery" % "0.5.4"
  lazy val xeger               = "com.github.bluezio" % "xeger"             % "v1.0.1"

  // Test
  lazy val scalaTest           = "org.scalatest" %% "scalatest"         % "3.2.10"
}
