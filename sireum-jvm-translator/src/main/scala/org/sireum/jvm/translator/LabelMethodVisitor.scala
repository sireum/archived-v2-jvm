package org.sireum.jvm.translator


import scala.collection.mutable
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Label

class LabelMethodVisitor(api: Int, mv: MethodVisitor) extends MethodVisitor(api, mv) {
  val labelMap = mutable.Map[Label, Int]()
  val labelList = mutable.MutableList[String]()

}