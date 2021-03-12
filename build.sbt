name := "scala-macros-example"

version := "0.1"

scalaVersion := "2.11.12"

lazy val macros = (project in file("macros"))
  .settings(
    scalaVersion := "2.11.12",
    libraryDependencies ++=
      Seq(
        "org.apache.spark" %% "spark-sql" % "2.3.0",
        "org.scala-lang" % "scala-compiler" % scalaVersion.value,
        "org.scala-lang" % "scala-reflect" % scalaVersion.value,
        "com.chuusai" %% "shapeless" % "2.3.2"
      )
  )

lazy val macrosExamples = (project in file("macros-examples"))
  .settings(
    scalaVersion := "2.11.12",
    libraryDependencies+="org.scalatest" %% "scalatest" % "3.0.4" % Test
  )
  .dependsOn(macros)


lazy val akkaKafkaStreamExample = (project in file(".")).
  aggregate(macros, macrosExamples)
