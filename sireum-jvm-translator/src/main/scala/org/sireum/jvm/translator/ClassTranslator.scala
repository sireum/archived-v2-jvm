package org.sireum.jvm.translator

import java.io.InputStream
import java.io.PrintWriter
import org.objectweb.asm.ClassReader
import org.objectweb.asm.util.TraceClassVisitor
import org.sireum.util.FileResourceUri

object ClassTranslator {
  def translate(cr : ClassReader) = {
    val output = new StringBuilder()
    println(cr.getClassName())
    val lcv: LocalVariableClassVisitor = new LocalVariableClassVisitor();
    cr.accept(lcv, 0)

    val methodLocalVariableMap = lcv.methodLocalVariableMap.toMap map (x => x._1 -> x._2)

    val tcv: TraceClassVisitor = new TraceClassVisitor(new PrintWriter(System.out))
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
  
  def translate(source : Either[String, FileResourceUri]): String = {
    source match {
      case Left(l) => { translate(new ClassReader(l)) }
      case Right(r) => {
        val is: InputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(r)
        translate(new ClassReader(is))
      }
    }

  }
}