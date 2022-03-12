import sbt._

object Dependencies {
  // Runtime
  lazy val scopt               = "com.github.scopt"           %% "scopt"             % "4.0.1"
  lazy val javaFaker           = "com.github.javafaker"        % "javafaker"         % "1.0.2"
  lazy val jsonoid             = "edu.rit.cs"                 %% "jsonoid-discovery" % "0.9.6"
  lazy val rgxgen              = "com.github.curious-odd-man"  % "rgxgen"            % "1.3"

  // Test
  lazy val scalaTest           = "org.scalatest" %% "scalatest"         % "3.2.10"
}
