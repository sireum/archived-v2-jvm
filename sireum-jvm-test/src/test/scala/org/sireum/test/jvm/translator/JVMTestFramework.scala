package org.sireum.test.jvm.translator

import java.io.File
import java.io.FileWriter
import java.net.URI

import org.sireum.jvm.translator.ClassTranslator
import org.sireum.pilar.parser.ChunkingPilarParser
import org.sireum.pilar.parser.PilarParser
import org.sireum.test.framework.TestFramework
import org.sireum.util.FileResourceUri
import org.sireum.util.FileUtil
import org.sireum.util.StringUtil

trait JVMTestFramework extends TestFramework {
  def Translating: this.type = this

  def className(className: String) = JVMConfiguration(Left(className))
  def from_fileName(fileUri : FileResourceUri) = JVMConfiguration(Right(fileUri))
  def forceGenerate: Boolean
  def compareFiles: Boolean
  
  protected var _title = ""

  case class JVMConfiguration(source: Either[String, FileResourceUri]) {
    test(title) {
      import org.sireum.pilar.parser.ChunkingPilarParser
      import org.sireum.pilar.parser.PilarParser

      val reporter = new PilarParser.StringErrorReporter(true)
      val output = ClassTranslator.translate(source)

      // Does it parse?
      val ms = ChunkingPilarParser(Left(output), reporter)
      if (compareFiles) {
        val fname: String = title.toString + ".plr"
        val fUri = (getClass.getResource("").toURI.toString.
          replace("/bin/", "/src/test/resources/") + "expected/" + fname).
          replace("@", "_").replace(" ", "-").replace("[", "-").replace("]", "-")
        val file = new File(new URI(fUri))
        if (forceGenerate || !file.exists) {
          // write
          val fw = new FileWriter(file)
          try fw.write(output)
          finally fw.close
        } else {
          // compare
          val frUri = (getClass.getResource("").toURI.toString.
            replace("/bin/", "/src/test/resources/") + "result/" + fname).
            replace("@", "_").replace(" ", "-").replace("[", "-").replace("]", "-")
          val fw = new FileWriter(new File(new URI(frUri)))
          try fw.write(output)
          finally fw.close
          val resultLines = StringUtil.readLines(output)
          val (expectedLines, _) = FileUtil.readFileLines(fUri.toString)
          resultLines should equal(expectedLines)
        }
      }
      
      reporter.errorAsString should be ("")
    }
    
    private def title = source match {
      case Left(e1) => e1
      case Right(e3) => e3.replace(File.separator, ".")
    }
  }

  
  

}