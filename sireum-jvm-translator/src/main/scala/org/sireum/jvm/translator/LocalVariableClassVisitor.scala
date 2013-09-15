package org.sireum.jvm.translator

import scala.tools.asm.ClassVisitor
import scala.collection.mutable.Map
import scala.tools.asm.Opcodes

class LocalVariableClassVisitor(api: Int, cv: ClassVisitor) extends ClassVisitor(api, cv) {
	val methodLocalVariableMap = Map[String, Map[Int, String]]()
    
	def this() = this(Opcodes.ASM4, null)
	override def visitMethod(access: Int, name: String, desc: String, signature: String, exceptions: Array[String]) = {
	  val localVariables = Map[Int, String]()
	  val lmv = new LocalVariableMethodVisitor(localVariables)
	  
	  methodLocalVariableMap += (name -> localVariables)
	  lmv
	}
}