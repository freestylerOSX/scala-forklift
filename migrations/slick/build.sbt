name := "scala-forklift-slick"

libraryDependencies ++= List(
  "com.typesafe.slick" %% "slick" % "3.0.0"
  ,"com.typesafe.slick" %% "slick-codegen" % "3.0.0"
  ,"org.scala-lang" % "scala-compiler" % "2.11.6"
  ,"com.typesafe" % "config" % "1.3.0"
  ,"org.slf4j" % "slf4j-nop" % "1.6.4" // <- disables logging
  ,"org.scalatest" %% "scalatest" % "2.2.4" % "test"
  ,"com.lihaoyi" %% "ammonite-ops" % "0.4.8" % "test"
  ,"commons-io" % "commons-io" % "2.4" % "test"
  ,"com.zaxxer" % "HikariCP" % "2.4.1" % "test"
  ,"com.h2database" % "h2" % "1.3.166" % "test"
  ,"org.xerial" % "sqlite-jdbc" % "3.8.11.2" % "test"
  ,"mysql" % "mysql-connector-java" % "5.1.36" % "test"
  ,"org.postgresql" % "postgresql" % "9.4-1203-jdbc42" % "test"
  ,"org.hsqldb" % "hsqldb" % "2.3.3" % "test"
  ,"org.apache.derby" % "derby" % "10.11.1.1" % "test"
)

parallelExecution in Test := false