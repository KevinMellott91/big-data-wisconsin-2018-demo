organization in ThisBuild := "org.wisconsinbigdata2018.demo"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.11.11"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test
val mleap = "ml.combust.mleap" %% "mleap-runtime" % "0.10.1"

lazy val `fraud-model` = (project in file("."))
  .aggregate(`fraud-model-api`, `fraud-model-impl`)

lazy val `fraud-model-api` = (project in file("fraud-model-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `fraud-model-impl` = (project in file("fraud-model-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest,
      mleap
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`fraud-model-api`)