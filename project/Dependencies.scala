import sbt._

object Dependencies {
  // Runtime
  lazy val jsonoid             = "edu.rit.cs"    %% "jsonoid-discovery" % "0.5.0"

  // Test
  lazy val scalaTest           = "org.scalatest" %% "scalatest"         % "3.2.10"
}
