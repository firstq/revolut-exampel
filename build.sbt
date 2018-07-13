
version       := "0.0.1"

scalaVersion  := "2.11.8"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.4.17"
  val akkaHttpV = "10.0.9"
  val slickV = "3.2.0"
  Seq(
    "com.typesafe.akka"   %%  "akka-http"    % akkaHttpV,
    "com.typesafe.akka"   %%  "akka-http-core"    % akkaHttpV,
    "com.typesafe.akka"   %%  "akka-http-testkit" % akkaHttpV,
    "com.typesafe.akka"	  %%  "akka-http-spray-json"	% akkaHttpV,
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.byteslounge" %% "slick-repo" % "1.4.3",
    "com.github.swagger-akka-http" %% "swagger-akka-http" % "0.11.0",
    "ch.megard" %% "akka-http-cors" % "0.2.1",
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"   % "2.3.11" % "test",
    "org.specs2"          %%  "specs2-mock"   % "2.3.11"  ,
    "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test",
    "junit" % "junit" % "4.11" % "test",
    "com.typesafe.slick" %% "slick" % slickV,
    "com.typesafe.slick" %% "slick-hikaricp" % slickV,
    "com.typesafe" % "config" % "1.2.1",
    "com.h2database" % "h2" % "1.3.175",
    "org.postgresql" % "postgresql" % "9.3-1100-jdbc41",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
    "ch.qos.logback" % "logback-classic" % "1.1.3",
    "org.slf4j" % "slf4j-nop" % "1.6.4"
  )
}
