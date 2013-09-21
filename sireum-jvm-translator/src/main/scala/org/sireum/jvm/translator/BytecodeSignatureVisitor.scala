package org.sireum.jvm.translator

import org.objectweb.asm.signature.SignatureVisitor
import org.objectweb.asm.Opcodes

class BytecodeSignatureVisitor(api: Int) extends SignatureVisitor(api) {
	def this() = this(Opcodes.ASM4)
	override def visitClassType(name:String) = println("ClassType:"+name)
	override def visitInnerClassType(name: String) = println("InnerClassType:"+name)
	override def visitFormalTypeParameter(name:String) = println("FormalTypeParameter:"+name)
	override def visitTypeVariable(name: String) = println("TypeVariable:"+name)
}