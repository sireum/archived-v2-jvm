package org.sireum.jvm.models

import scala.collection.mutable
import scala.tools.asm.Label

class LocalInfo {
  val localVariable = mutable.Map[Int, List[LocalVariable]]()
  val labelLineMap = mutable.MutableList[String]()
  
  override def toString() = labelLineMap.toString
}

class LocalVariable(val name: String,val start: String,val end: String) {
  override def toString() = s"$name $start $end"
}