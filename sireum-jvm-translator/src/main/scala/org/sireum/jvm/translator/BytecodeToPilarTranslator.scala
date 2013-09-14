package org.sireum.jvm.translator

import com.google.common.reflect.ClassPath

object BytecodeToPilarTranslator {
  def main(args: Array[String]) = translatePackage("org.sireum.jvm.samples")
  def translatePackage(packageName: String) = {
    val classPath = ClassPath.from(ClassLoader.getSystemClassLoader())

    val classes = classPath.getTopLevelClasses(packageName)

    val classesIt = classes.iterator()
    while (classesIt.hasNext()) {
      val cls = classesIt.next()
      val output = ClassTranslator.translate(ClassLoader.getSystemClassLoader(), cls.getName())
      val pw = new java.io.PrintWriter(new java.io.File("result/" + cls.getSimpleName()))
      pw.println(output)
      pw.close()
    }
  }

}