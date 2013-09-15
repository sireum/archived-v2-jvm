package org.sireum.jvm.translator

import scala.tools.asm.MethodVisitor
import scala.tools.asm.Opcodes
import scala.tools.asm.Label
import scala.collection.mutable.Map

class LocalVariableMethodVisitor(api: Int, mv: MethodVisitor, val localMap: Map[Int, String]) extends MethodVisitor(api, mv) {
	def this(localMap: Map[Int, String]) = this(Opcodes.ASM4, null, localMap)
	
	override def visitLocalVariable(name: String, desc: String, signature: String, start: Label, end: Label, index: Int) = {
	  localMap += (index -> name)
	}
	
	
}