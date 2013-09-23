package org.sireum.test.jvm.translator

import org.sireum.jvm.translator.ClassTranslator
import org.sireum.util.Either3

object BytecodeTranslator {
  def main(args: Array[String]) {
    val output = ClassTranslator.translate(Either3.First(args(0)))
    val pw2 = new java.io.PrintWriter(new java.io.File("output"))

    pw2.println(output)
    pw2.close()
  }
}