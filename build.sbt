
name := "puertorico"

scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  "io.unsecurity"     %% "unsecurity-auth0" % "3.0.1",
  "ch.qos.logback"    % "logback-classic"   % "1.2.3",
  "org.scalatest"     %% "scalatest"        % "3.2.0" % Test
)
