import Dependencies._
import sbt.Keys.libraryDependencies

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "net.leibman"
ThisBuild / organizationName := "leibman"

lazy val root = (project in file("."))
  .settings(
    name := "zio-slick",
    libraryDependencies ++= Seq(
      "com.typesafe.slick" %% "slick" % "3.3.2" withSources(),
      "dev.zio" %% "zio" % "1.0.0-RC8-12" withSources(),
      "com.h2database"       % "h2"                % "1.4.199" % Test,
      scalaTest % Test
    ),
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.



