
name := "puertorico"

scalaVersion := "2.13.4"

libraryDependencies ++= Seq(
  "io.unsecurity"     %% "unsecurity-core"  % "3.0.1",
  "com.lihaoyi"       %% "scalatags"        % "0.8.6",
  "ch.qos.logback"    % "logback-classic"   % "1.2.3",
  "org.scalatest"     %% "scalatest"        % "3.2.6" % Test
)
