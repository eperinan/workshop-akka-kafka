lazy val Versions = new {
  val akkaHttpVersion = "10.0.9"
  val akkaVersion     = "2.5.3"
  val scalatest       = "3.0.1"
}

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "com.workshop-akka-kafka",
      scalaVersion    := "2.12.2",
      version         := "0.0.1"
    )),
    name := "workshop-akka-kafka",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"         % Versions.akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-xml"     % Versions.akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream"       % Versions.akkaVersion,
      "com.typesafe.akka" %% "akka-http-testkit" % Versions.akkaHttpVersion % Test,
      "org.scalatest"     %% "scalatest"         % Versions.scalatest % Test
    )
  )
