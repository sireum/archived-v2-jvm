package org.sireum.jvm.translator

import scala.tools.asm.ClassVisitor
import scala.collection.mutable.Map
import scala.tools.asm.Opcodes
import org.sireum.jvm.models.LocalVariable
import org.sireum.jvm.models.LocalInfo

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
	   println(methodLocalVariableMap)
	}
}