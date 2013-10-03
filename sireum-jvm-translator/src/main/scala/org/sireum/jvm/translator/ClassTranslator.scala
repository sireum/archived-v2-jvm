package org.sireum.jvm.translator

import java.io.InputStream
import java.io.PrintWriter
import org.objectweb.asm.ClassReader
import org.objectweb.asm.util.TraceClassVisitor
import org.sireum.util.FileResourceUri
import java.io.FileInputStream
import org.sireum.util.Either3
import com.google.common.reflect.ClassPath

object ClassTranslator {
  def translate(cr: ClassReader) = {
    val output = new StringBuilder()
    val lcv: LocalVariableClassVisitor = new LocalVariableClassVisitor();
    cr.accept(lcv, 0)

    val methodLocalVariableMap = lcv.methodLocalVariableMap.toMap map (x => x._1 -> x._2)

    val bcv: BytecodeClassVisitor = new BytecodeClassVisitor(methodLocalVariableMap);
    cr.accept(bcv, 0);
    
    output ++= bcv.stRecord.render()
    val it = bcv.record.innerClasses.iterator()
    while (it.hasNext()) {
      val cr: ClassReader = new ClassReader(it.next())
      val lcv: LocalVariableClassVisitor = new LocalVariableClassVisitor();
      cr.accept(lcv, 0)

      val methodLocalVariableMap = lcv.methodLocalVariableMap.toMap map (x => x._1 -> x._2)
      val bcv: BytecodeClassVisitor = new BytecodeClassVisitor(methodLocalVariableMap);
      cr.accept(bcv, 0)
      output ++= bcv.stRecord.render()
    }
    output.toString
  }
  
  def translate(source: Either3[String, Array[Byte], FileResourceUri]): String = {
    source match {
      case Either3.First(e1) => translate(new ClassReader(e1))
      case Either3.Second(e2) => translate(new ClassReader(e2))
      case Either3.Third(e3) => {
        val is: InputStream = new FileInputStream(e3)
        translate(new ClassReader(is))
      }
    }
  }

}