package org.sireum.jvm.translator

import scala.tools.asm.MethodVisitor
import scala.tools.asm.Opcodes
import scala.tools.asm.Label
import scala.collection.mutable
import org.sireum.jvm.models.LocalVariable
import org.sireum.jvm.models.LocalInfo

class LocalVariableMethodVisitor(api: Int, mv: MethodVisitor, val localMap: LocalInfo) extends MethodVisitor(api, mv) {
  def this(localMap: LocalInfo) = this(Opcodes.ASM4, null, localMap)

  val labelMap = mutable.Map[Label, Int]()

  def getLabelId(l: Label) = {
    val i = labelMap.getOrElseUpdate(l, labelMap.size)
    f"L$i%05d"
  }
  override def visitLocalVariable(name: String, desc: String, signature: String, start: Label, end: Label, index: Int) = {
    localMap.localVariable += (index -> (new LocalVariable(name, getLabelId(start), getLabelId(end)) :: localMap.localVariable.getOrElse(index, List())))
  }
  override def visitLabel(l : Label) = { localMap.labelLineMap += l.toString }
  
  override def visitTryCatchBlock(start: Label, end: Label, handler: Label, typ: String) = {
    getLabelId(start) 
    getLabelId(end)
    getLabelId(handler)
  }
  

}