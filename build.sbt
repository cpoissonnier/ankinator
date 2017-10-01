name := """ankinator"""
organization := "com.cpoissonnier"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.2"

herokuAppName in Compile := "ankinator"

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
resolvers += "Central Maven repository" at "http://central.maven.org/maven2/"

libraryDependencies ++= {
  Seq(
    guice,
    ws,
    javaForms,
    ehcache,
    "org.assertj" % "assertj-core" % "3.8.0" % "test",
    "org.apache.commons" % "commons-text" % "1.1",
    "org.jsoup" % "jsoup" % "1.7.2"
  )
}
