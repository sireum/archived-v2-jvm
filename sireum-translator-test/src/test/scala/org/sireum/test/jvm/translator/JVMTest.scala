package org.sireum.test.jvm.translator

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class JVMTest extends JVMTestFramework {
	val classNames = List("scala.collection.immutable.Map", "java.util.HashMap", "java.io.File", 
	    "java.util.Set", "scala.Predef", "java.lang.String", "java.lang.System", "java.util.ArrayList")
	
	 def forceGenerate = false
	 //classNames foreach { className => Translating className className }
	 Translating className "org.sireum.test.jvm.samples.SimpleScala"
}