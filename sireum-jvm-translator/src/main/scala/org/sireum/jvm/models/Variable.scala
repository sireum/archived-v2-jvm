package org.sireum.jvm.models

class Variable(val typ: String, val value: String) {
  override def toString() = typ + " " + value
  
  def getName = value
  def getTyp = typ
}

object Variable {
  def apply(typ: String, value: String) = new Variable(typ, value)
}