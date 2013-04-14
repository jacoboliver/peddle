import com.typesafe.startscript.StartScriptPlugin

seq(StartScriptPlugin.startScriptForClassesSettings: _*)

name := "peddle"

version := "1.0"

scalaVersion := "2.9.2"

resolvers += "twitter-repo" at "http://maven.twttr.com"

libraryDependencies += "com.twitter" %% "finagle-http" % "6.2.0"

libraryDependencies += "net.liftweb" %% "lift-json" % "2.5-RC2"
