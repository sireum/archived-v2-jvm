package org.sireum.test.jvm.translator

import org.junit.Assert._
import org.junit.Test
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.sireum.jvm.translator.ClassTranslator
import org.sireum.pilar.parser.PilarParser
import org.scalatest.junit.JUnitRunner


@RunWith(classOf[JUnitRunner])
class TranslatorTest extends FunSuite{
  
	test("It should parse") {
	  val reporter = new PilarParser.StringErrorReporter(true)
//      val cl = ClassLoader.getSystemClassLoader()
//      val qname = "org.sireum.jvm.samples.HelloWorld2"
//      PilarParser.apply(Left(ClassTranslator.translate(cl, qname)), reporter)
	  BytecodeTranslator.main(Array("org.sireum.jvm.samples.HelloWorld2"))
	  PilarParser.apply(Right("file:/Users/Vidit/Dropbox/Classes/Spring2013/FinalProject/Sireum2Workspace/sireum-translator/sireum-jvm-translator/output"), reporter)
      //PilarParser.apply(Right("file:/Users/Vidit/Dropbox/AndroidStuff/classes.pilar"), reporter)
      assertTrue(reporter.errorAsString, reporter.errorAsString.isEmpty())
    }
	
}
// <@Frame ("new", 5, `[int, int, int, (|java.lang.Object|)], 0)>
// <@Frame (@New, 9, `[(|java.lang.String|), int, int, int, int, long, (|java.lang.Object|), (|int|), int, int, :top, :top], 0, `[:top, :top, :top, :top])>;
// <@Frame (@New, 9, `[(|java.lang.String|), int, int, int, int, long, (|java.lang.Object|), (|int|), int, int, :top, :top], 0, `[:top, :top, :top, :top])>