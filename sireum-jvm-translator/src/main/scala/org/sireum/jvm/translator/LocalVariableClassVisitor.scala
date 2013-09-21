package org.sireum.jvm.translator

import scala.collection.mutable.Map
import org.sireum.jvm.models.LocalInfo
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes

class LocalVariableClassVisitor(api: Int, cv: ClassVisitor) extends ClassVisitor(api, cv) {
  val methodLocalVariableMap = Map[String, LocalInfo]()

  def this() = this(Opcodes.ASM4, null)
  override def visitMethod(access: Int, name: String, desc: String, signature: String, exceptions: Array[String]) = {
    val localVariables = new LocalInfo()
    val lmv = new LocalVariableMethodVisitor(localVariables)

    methodLocalVariableMap += (name -> localVariables)
    lmv
  }
  override def visitEnd() = {
    //println(methodLocalVariableMap)
  }

}