package org.sireum.jvm.translator

import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.Opcodes
import org.sireum.jvm.models.Field
import org.sireum.jvm.util.Util

class BytecodeFieldVisitor(api: Int, fv: FieldVisitor, f: Field) extends FieldVisitor(api, fv) {
  def this(f: Field) = this(Opcodes.ASM4, null, f)

  override def visitAnnotation(desc: String, visible: Boolean) = {
    val bav: BytecodeAnnotationVisitor = new BytecodeAnnotationVisitor(Util.getTypeString(desc), f)
    bav
  }

}