package org.sireum.test.jvm.translator

import org.sireum.jvm.translator.ClassTranslator

object BytecodeTranslator {
	def main(args: Array[String]) {
	  val output = ClassTranslator.translate(ClassLoader.getSystemClassLoader(), "org.sireum.jvm.samples.HelloWorld2")
	  val pw = new java.io.PrintWriter(new java.io.File("output"))
	  pw.println(output)
	  pw.close()
	}
}