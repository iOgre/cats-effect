val scala3Version = "3.2.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "cats-effect",
    version := "0.1.0",

    scalaVersion := scala3Version,
  scalacOptions ++= Seq(
    "-encoding",
    "UTF-8",
    "-deprecation",
    "-feature",
    "-unchecked",
    "-language:implicitConversions",
    "-language:higherKinds",
    "-language:postfixOps",
  
  ),
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "3.2.0",
    )
  )
