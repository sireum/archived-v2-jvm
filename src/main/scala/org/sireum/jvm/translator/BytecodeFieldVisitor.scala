package org.sireum.jvm.translator

import scala.tools.asm.FieldVisitor
import scala.tools.asm.Opcodes
import org.sireum.jvm.models.Field

class BytecodeFieldVisitor(api:Int, fv:FieldVisitor, f: Field) extends FieldVisitor(api, fv) {
  def this(f: Field) = this(Opcodes.ASM4, null, f)
  
  override def visitAnnotation(desc: String, visible: Boolean) = {
    val bav: BytecodeAnnotationVisitor = new BytecodeAnnotationVisitor(f)
    bav
  }

}