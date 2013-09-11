package org.sireum.test.jvm.translator

import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.sireum.jvm.translator.ClassTranslator
import org.sireum.pilar.parser.PilarParser
import org.scalatest.junit.JUnitRunner


@RunWith(classOf[JUnitRunner])
class TranslatorTest extends FunSuite{
  
	test("It should parse") {
      val cl = ClassLoader.getSystemClassLoader()
      val qname = "org.sireum.jvm.samples.HelloWorld"
      val reporter = new PilarParser.StringErrorReporter(true)
      PilarParser.apply(Left(ClassTranslator.translate(cl, qname)), reporter)
      //PilarParser.apply(Right("file:/Users/Vidit/Dropbox/AndroidStuff/classes.pilar"), reporter)
      assertTrue(reporter.errorAsString.isEmpty())
    }
	
}