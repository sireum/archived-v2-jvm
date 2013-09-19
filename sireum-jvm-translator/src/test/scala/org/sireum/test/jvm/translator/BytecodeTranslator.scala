package org.sireum.test.jvm.translator

import org.sireum.jvm.translator.ClassTranslator

object BytecodeTranslator {
  def main(args: Array[String]) {
    val output = ClassTranslator.translate(ClassLoader.getSystemClassLoader(), args(0))
    val pw = new java.io.PrintWriter(new java.io.File("result/" + args(0).drop(args(0).lastIndexOf(".") + 1) + ".plr"))
    val pw2 = new java.io.PrintWriter(new java.io.File("output"))

    pw.println(output)
    pw2.println(output)
    pw.close()
    pw2.close()
  }
}