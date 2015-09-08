package com.liyaos.forklift.slick

import java.io._
import scala.language.postfixOps
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import slick.codegen.SourceCodeGenerator

trait SlickCodegen {

  val generatedDir =
    System.getProperty("user.dir") + "/generated_code/src/main/scala"

  val container = "tables"

  val fileName = "schema.scala"

  def pkgName(version: String) = "datamodel." + version + ".schema"

  def tableNames: Seq[String] = List()

  def genCode(mm: SlickMigrationManager) {
    import mm.dbConfig.driver.api._
    if (mm.notYetAppliedMigrations.size > 0) {
      println("Your database is not up to date, code generation denied for compatibility reasons. Please update first.")
      return
    }
    val driver = mm.dbConfig.driver
    val action = driver.createModel(Some(
      driver.defaultTables.map { s =>
        s.filter { t =>
          tableNames.contains(t.name.name)
        }
      })) flatMap { case m =>
        DBIO.from {
          Future {
            val latest = mm.latest
            List( "v" + latest, "latest" ).foreach { version =>
              val generator = new SourceCodeGenerator(m) {
                override def packageCode(
                  profile: String, pkg: String,
                  container: String, parentType: Option[String]) : String =
                  super.packageCode(profile, pkg, container, None) + s"""
object Version{
  def version = $latest
}
"""
              }
              generator.writeToFile(s"slick.driver.${driver}",
                generatedDir, pkgName(version), container, fileName)
            }
          }
        }
    }
    val f = mm.db.run(action)
    Await.result(f, Duration.Inf)
  }

  def remove() {
    try {
      val f = (Glob.glob((f: File) => !f.isDirectory && f.getName == fileName)
        (List(generatedDir)))
      f.foreach(_.delete)
    } catch {
      case e: FileNotFoundException =>
    }
  }
}

object Glob{
  // taken from: http://kotakanbe.blogspot.ch/2010/11/scaladirglobsql.html
  def glob(filter: (File) => Boolean)(dirs: List[String]): List[File] = {
    def recursive(dir: File, acc: List[File]): List[File] =
      Option(dir.listFiles) match {
        case None => throw new FileNotFoundException(dir.getAbsolutePath)
        case Some(lists) =>
          val filtered = lists.filter{ c =>  filter(c) }.toList
          val childDirs = lists.filter{ c => c.isDirectory && !c.getName.startsWith(".") }
          return ( (acc ::: filtered) /: childDirs){ (a, dir) => recursive(dir, a)}
      }
    dirs.flatMap{ d => recursive(new File(d), Nil)}
  }
}
