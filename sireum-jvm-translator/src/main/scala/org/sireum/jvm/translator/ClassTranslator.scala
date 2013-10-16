package org.sireum.jvm.translator

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream

import org.objectweb.asm.ClassReader
import org.sireum.util.FileResourceUri
import scala.collection.JavaConversions._

object ClassTranslator {
  val cl = getClass.getClassLoader()
  def translate(cr: ClassReader) = {
    val output = new StringBuilder()
    val lcv: LocalVariableClassVisitor = new LocalVariableClassVisitor();
    cr.accept(lcv, 0)

    val methodLocalVariableMap = lcv.methodLocalVariableMap.toMap map (x => x._1 -> x._2)

    val bcv: BytecodeClassVisitor = new BytecodeClassVisitor(methodLocalVariableMap);
    cr.accept(bcv, 0);

    output ++= bcv.stRecord.render()
    bcv.record.innerClasses.foreach(ic => {
      val cp = ic.replace('.', File.separatorChar) + ".class"
      try {
        val cr: ClassReader = new ClassReader(cl.getResourceAsStream(cp))
        val lcv: LocalVariableClassVisitor = new LocalVariableClassVisitor();
        cr.accept(lcv, 0)

        val methodLocalVariableMap = lcv.methodLocalVariableMap.toMap map (x => x._1 -> x._2)
        val bcv: BytecodeClassVisitor = new BytecodeClassVisitor(methodLocalVariableMap);
        cr.accept(bcv, 0)
        output ++= bcv.stRecord.render()
      } catch {
        case io: IOException => {}
      }
    })
    output.toString
  }

  def translate(is: InputStream): String = {
    translate(new ClassReader(is))
  }

  def translate(source: Either[String, FileResourceUri]): String = {
    source match {
      case Left(e1) => {
        val output = new StringBuilder()
        val is = cl.getResourceAsStream(e1.replace('.', File.separatorChar) + ".class")
        output ++= translate(is)
        output.toString
      }
      case Right(e2) => {
        val is: InputStream = new FileInputStream(e2)
        translate(is)
      }
    }
  }
}