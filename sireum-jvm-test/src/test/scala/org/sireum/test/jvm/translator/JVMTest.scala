package org.sireum.test.jvm.translator

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.google.common.reflect.ClassPath
import scala.collection.JavaConversions._
import org.sireum.jvm.translator.ClassTranslator

@RunWith(classOf[JUnitRunner])
class JVMTest extends JVMTestFramework {
	val classNames = List("scala.collection.immutable.Map", "java.util.HashMap", "java.io.File", 
	    "java.util.Set", "scala.Predef", "java.lang.String", "java.lang.System", "java.util.ArrayList",
	    "org.sireum.test.jvm.samples.Conditions", "java.lang.StringBuilder", "java.lang.StringBuffer")
	
	 def forceGenerate = true
	 def compareFiles = false
	 
	 val cp: ClassPath = ClassPath.from(getClass.getClassLoader())
	 cp.getTopLevelClassesRecursive("org.sireum.core").foreach(x=>Translating className x.getName())
	 cp.getTopLevelClassesRecursive("org.sireum.jvm").foreach(x=>Translating className x.getName())
	 //cp.getTopLevelClassesRecursive("scala.collection").foreach(x=>Translating className x.getName())

	 //println(cp.getTopLevelClassesRecursive("scala.collection").size())
	 //cp.getTopLevelClassesRecursive("scala.collection").foreach(x=>println(x.getName()))
	 
//	      val cl = getClass.getClassLoader()
//        val output = new StringBuilder()
//        val is = cl.getResourceAsStream(e1.replace('.', File.separatorChar)+".class")
//        if (isClass(e1)) {
//	      output ++= translate(is)
//        } else {
//          val cp : ClassPath = ClassPath.from(cl)
//          val clazzs = cp.getTopLevelClassesRecursive(e1)
//          clazzs.foreach(clazz => {
//            println(clazz.getName())
//            val is = cl.getResourceAsStream(clazz.getName().replace('.', File.separatorChar)+".class")
//            output ++= translate(is)
//          })
//        }
//	   
//  def isClass(name: String): Boolean = {
//    try {
//      Class.forName(name, false, getClass.getClassLoader())
//      true
//    } catch  {
//      case ce : ClassNotFoundException => false
//    }
//  }
	 
	 
	 
}
