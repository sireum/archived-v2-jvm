package org.sireum.jvm.translator

import scala.tools.asm.ClassVisitor
import scala.tools.asm.Attribute
import scala.tools.asm.Opcodes
import org.stringtemplate.v4.STGroupFile

import org.sireum.jvm.util.Util
import org.sireum.jvm.models._

class BytecodeClassVisitor(api:Int, cv:ClassVisitor) extends ClassVisitor(api, cv) {
	val stg = new STGroupFile("pilar.stg")
	val stRecord = stg.getInstanceOf("recorddef")
	
	var record: Record = null
	
	def this() = this(Opcodes.ASM4, null)
	def this(api: Int) = this(api, null)
	
	def addAnnotations(key: String, value: String): Unit = 
	  addAnnotations(key, value, false)
	  
	def addAnnotations(key: String, value: String, quote: Boolean) = 
	  if(quote) record.annotations.put(key, "\""+value+"\"")
	  else record.annotations.put(key, value)
	
	override def visit(version: Int, access: Int, name:String, signature: String, supername:String, interfaces:Array[String]) = {
	  record = new Record(access, name, supername, signature, interfaces)
	  
	  stRecord.add("record", record)
	}
	
	override def visitSource(source: String, debug:String) = {
	  if(source!=null) addAnnotations("source", source, true)
	  if(debug!=null) addAnnotations("debug", debug, true)
	}
	
	override def visitAnnotation(desc: String, visible: Boolean) = {
	  val bav = new BytecodeAnnotationVisitor(record)
	  record.annotations.put(Util.getTypeString(desc), "")
	  bav
	}
	
	override def visitInnerClass(name: String, outername: String, innername: String, access: Int) = {
	  val stInnerClass = stg.getInstanceOf("innerclassdef")
	  stInnerClass.add("name", Util.getPilarClassName(name))
	  stInnerClass.add("outerName", Util.getPilarClassName(outername))
	  stInnerClass.add("innerName", innername)
	  if(access != 0) stInnerClass.add("access", Util.getAccessFlag(access))
	  
	  addAnnotations("InnerClass", stInnerClass.render)
	  record.innerClasses.add(name)
	}
		
	override def visitOuterClass(owner: String, name: String, desc: String) = {
	  val stOuterClass = stg.getInstanceOf("outerclassdef")
	  stOuterClass.add("owner", owner)
	  if(name!=null) {
	    stOuterClass.add("method", name+desc)
	  }
	  
	  addAnnotations("OuterClass", stOuterClass.render)
	  record.outerClasses.add(name)
	}
	
	override def visitField(access:Int, name:String, desc:String, signature:String, value:Object) = {
	  val field = new Field(access, record.getClassName + "." +name, desc, signature, value)
	  val bfv = new BytecodeFieldVisitor(field)
	  record.addFields(field)
	  bfv
	}
	
	override def visitMethod(access: Int, name: String, desc: String, signature: String, exceptions: Array[String]) = {
	  val procedure = new Procedure(access, name, desc, signature, exceptions, record)
	  val bmv = new BytecodeMethodVisitor(procedure)
	  record.procedures.add(procedure)
	  bmv
	}
	
	// override def visitAttribute(attr: Attribute) = ???
	// override def visitEnd() = ???
}