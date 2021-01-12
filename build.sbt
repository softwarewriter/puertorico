
name := "puertorico"

scalaVersion := "2.13.4"

libraryDependencies ++= Seq(
  "io.unsecurity"     %% "unsecurity-auth0" % "3.0.1",
  "com.lihaoyi"       %% "scalatags"        % "0.8.6",
  "ch.qos.logback"    % "logback-classic"   % "1.2.3",
  "org.scala-lang"    % "scala-reflect"     % scalaVersion.value,
  "org.scalatest"     %% "scalatest"        % "3.2.3" % Test
)
