package org.sireum.jvm.translator

import scala.tools.asm.MethodVisitor
import scala.collection.mutable
import scala.tools.asm.Label

class LabelMethodVisitor(api: Int, mv : MethodVisitor) extends MethodVisitor(api,mv) {
	val labelMap = mutable.Map[Label, Int]()
	val labelList = mutable.MutableList[String]()
	
	
}