package org.sireum.jvm.translator

import java.io.InputStream;
import java.io.PrintWriter
import scala.tools.asm.ClassReader
import scala.tools.asm.util.TraceClassVisitor

object ClassTranslator {
  def translate(cl : ClassLoader, fullyQualifiedClassName : String) = {
    val output = new StringBuilder()
    val is:InputStream = cl.getResourceAsStream(fullyQualifiedClassName.replace(".", System.getProperty("file.separator"))
        + ".class");
    val cr:ClassReader = new ClassReader(is);
    
    val lcv: LocalVariableClassVisitor = new LocalVariableClassVisitor();
    cr.accept(lcv, ClassReader.EXPAND_FRAMES)
    
    val methodLocalVariableMap = lcv.methodLocalVariableMap.toMap map (x => x._1 -> x._2)
    
    val bcv: BytecodeClassVisitor = new BytecodeClassVisitor(methodLocalVariableMap);
    cr.accept(bcv, ClassReader.EXPAND_FRAMES);
    output ++= bcv.stRecord.render()
    val it = bcv.record.innerClasses.iterator()
    while(it.hasNext()) {
      val is:InputStream = cl.getResourceAsStream(it.next().replace(".", System.getProperty("file.separator"))
        + ".class");
      val cr:ClassReader = new ClassReader(is)
      val lcv: LocalVariableClassVisitor = new LocalVariableClassVisitor();
      cr.accept(lcv, ClassReader.EXPAND_FRAMES)
      
      val methodLocalVariableMap = lcv.methodLocalVariableMap.toMap map (x => x._1 -> x._2)
      val bcv: BytecodeClassVisitor = new BytecodeClassVisitor(methodLocalVariableMap);
      cr.accept(bcv, ClassReader.EXPAND_FRAMES)
      output ++= bcv.stRecord.render()
    }
    output.toString
  }
}