package org.sireum.jvm.translator

import scala.tools.asm.signature.SignatureVisitor

class BytecodeSignatureVisitor(api: Int) extends SignatureVisitor(api) {
  override def visitArrayType() = ???
  override def visitBaseType(descriptor: Char) = ???
  override def visitClassBound() = ???
  override def visitClassType(name: String) = ???
  override def visitEnd() = ???
  override def visitExceptionType() = ???
  override def visitFormalTypeParameter(name: String) = ???
  override def visitInnerClassType(name: String) = ???
  override def visitInterface() = ???
  override def visitInterfaceBound() = ???
  override def visitParameterType() = ???
  override def visitReturnType() = ???
  override def visitSuperclass() = ???
  override def visitTypeArgument() = ???
  override def visitTypeArgument(wildcard: Char) = ???
  override def visitTypeVariable(name: String) = ???
}