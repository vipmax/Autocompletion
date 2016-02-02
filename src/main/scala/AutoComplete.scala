import java.util

import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileInputStream
import java.net.URLClassLoader
import java.util.regex.Pattern
import java.util.zip.ZipInputStream
import scala.collection.mutable

/**
  * Created by max on 31.01.16.
  */
class AutoComplete {

  val classes = new util.HashMap[String, String]()


  def getUserCode: String = FileUtils.readFileToString(new File("src/main/java/Test.java"))


  def init() {
    initJars("target/deps")
    initJars("/usr/lib/jvm/java-8-oracle/jre/lib/")
    initJars("/usr/lib/jvm/java-8-oracle/jre/lib/ext/")

    println("classes size = " + classes.size())
  }

  def initJars(directory: String) {
    val files = new File(directory).listFiles()
    for (file <- files) {
      if (!file.isDirectory) {
        val zip = new ZipInputStream(new FileInputStream(file))
        var entry = zip.getNextEntry
        while (entry != null) {
          if (entry.getName.endsWith(".class") && !entry.isDirectory) {
            val className = new StringBuilder()
            className.append(entry.getName.replace("/", "."))
            className.setLength(className.length - ".class".length())
            classes.put(className.toString(), file.getAbsolutePath)
          }
          entry = zip.getNextEntry
        }
      }
    }
  }

  def test(code: String) {
    val userImportClasses = getUserImportClases(code)
    val userClasses = getUserClasses(code)

    val classesForAnalisys = deleteDublicates(userImportClasses, userClasses)

    for (classs <- classesForAnalisys) {
      val methods = getMethods(classs)
      println("\nFor class = " + classs + " Found " + methods.length + " methods")
      methods.foreach(println)
    }

  }

  def deleteDublicates(userImportClasses: mutable.Buffer[String], userClasses: mutable.Buffer[String]) = {
    val classesForAnalisys = new mutable.HashSet[String]()
    for (userClass <- userClasses) {
      for (userImportClass <- userImportClasses) {
        val split = userImportClass.replace(".", " ").split(" ")
        val userImportType = split(split.length - 1)
        if (userImportType.equals(userClass))
          classesForAnalisys.add(userImportClass)
      }
    }
    classesForAnalisys
  }

  def getMethods(classs: String) = {
    val jar = classes.get(classs)
    val url = Array(new File(jar).toURL)
    val loadClass = new URLClassLoader(url).loadClass(classs)
    loadClass.getDeclaredMethods
  }

  def getUserImportClases(code: String) = {
    val importRegexp = "import (.*);"
    val pattern = Pattern.compile(importRegexp)
    val matcher = pattern.matcher(code)
    val imports = mutable.Buffer.empty[String]

    while (matcher.find())
      imports += matcher.group(1)

    imports
  }

  def getUserClasses(code: String) = {
//    val pattern = """([^\\ ]*) (.*)=""".r
//
//    pattern.findAllIn(code).foreach {
//      case pattern(a,b) => println(a)
//    }
//
//
//    val classes = mutable.Buffer.empty[String]

    val regexp = "(.*) (.*)="
    val pattern = Pattern.compile(regexp)
    val matcher = pattern.matcher(code)
    val classes = mutable.Buffer.empty[String]

    while (matcher.find()) {
      val classs = matcher.group(1).trim().split(" ")(0).trim().split("<")(0)
      classes += classs
    }
    classes
  }
}

object Run {
  def main(args: Array[String]) {
    val autoComplete = new AutoComplete()
    autoComplete.init()

    val startTime = System.nanoTime()
    autoComplete.test(autoComplete.getUserCode)
    println("elapsed = " + (System.nanoTime() - startTime) / 1000000 + " ms")
  }
}
