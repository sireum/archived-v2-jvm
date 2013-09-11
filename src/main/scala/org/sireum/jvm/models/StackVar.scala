package org.sireum.jvm.models

class StackVar(val typ: String, val value: String) {
  override def toString() = typ + " " + value
}