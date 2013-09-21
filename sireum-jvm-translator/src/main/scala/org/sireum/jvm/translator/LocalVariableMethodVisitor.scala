package org.sireum.jvm.translator

import scala.collection.mutable

import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.sireum.jvm.models.LocalInfo
import org.sireum.jvm.models.LocalVariable

class LocalVariableMethodVisitor(api: Int, mv: MethodVisitor, val localMap: LocalInfo) extends MethodVisitor(api, mv) {
  def this(localMap: LocalInfo) = this(Opcodes.ASM4, null, localMap)

  val labelMap = mutable.Map[Label, Int]()

  def getLabelId(l: Label) = {
    val i = labelMap.getOrElseUpdate(l, labelMap.size)
    f"L$i%05d"
  }

  override def visitJumpInsn(opcode: Int, l: Label) = getLabelId(l)
  
  override def visitLocalVariable(name: String, desc: String, signature: String, start: Label, end: Label, index: Int) = {
    localMap.localVariable += (index -> (new LocalVariable(name, getLabelId(start), getLabelId(end)) :: localMap.localVariable.getOrElse(index, List())))
  }
  override def visitLabel(l: Label) = { localMap.labelLineMap += getLabelId(l) }
  
  override def visitLookupSwitchInsn(dflt: Label, keys: Array[Int], labels: Array[Label]) = {
    getLabelId(dflt)
    labels foreach (l => getLabelId(l))
  }
  
  override def visitTryCatchBlock(start: Label, end: Label, handler: Label, typ: String) = {
    getLabelId(start)
    getLabelId(end)
    getLabelId(handler)
  }
  override def visitTableSwitchInsn(min: Int, max: Int, dflt: Label, labels: Label*) = {
    getLabelId(dflt)
    labels foreach (l => getLabelId(l))
  }

}