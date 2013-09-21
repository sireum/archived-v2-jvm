package org.sireum.jvm.translator

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.signature.SignatureReader
import org.objectweb.asm.util.TraceSignatureVisitor
import org.sireum.jvm.models.Field
import org.sireum.jvm.models.LocalInfo
import org.sireum.jvm.models.Procedure
import org.sireum.jvm.models.Record
import org.sireum.jvm.util.Util
import org.stringtemplate.v4.STGroupFile

class BytecodeClassVisitor(api: Int, cv: ClassVisitor, methodLocalMap: Map[String, LocalInfo]) extends ClassVisitor(api, cv) {
  val stg = new STGroupFile("pilar.stg")
  val stRecord = stg.getInstanceOf("recorddef")

  var record: Record = null

  def this() = this(Opcodes.ASM4, null, null)
  def this(methodLocalMap: Map[String, LocalInfo]) = this(Opcodes.ASM4, null, methodLocalMap)

  def addAnnotations(key: String, value: String): Unit =
    addAnnotations(key, value, false)

  def addAnnotations(key: String, value: String, quote: Boolean) =
    if (quote) record.annotations.put(key, "\"" + value + "\"")
    else record.annotations.put(key, value)

  override def visit(version: Int, access: Int, name: String, signature: String, supername: String, interfaces: Array[String]) = {
    record = new Record(access, name, supername, signature, interfaces, methodLocalMap)
    if (signature != null) {
      println(signature)
      val stClassSigDef = stg.getInstanceOf("classsigdef")
      stClassSigDef.add("text", signature)

      val sr: SignatureReader = new SignatureReader(signature)
      val tsv = new BytecodeSignatureVisitor
      sr.acceptType(tsv)

      addAnnotations("Signature", stClassSigDef.render)
    }
    stRecord.add("record", record)
  }

  override def visitSource(source: String, debug: String) = {
    if (source != null) addAnnotations("source", source, true)
    if (debug != null) addAnnotations("debug", debug, true)
  }

  override def visitAnnotation(desc: String, visible: Boolean) = {
    val bav = new BytecodeAnnotationVisitor(desc, record)
    //addAnnotations(Util.getTypeString(desc), "")
    //super.visitAnnotation(desc, visible)
    bav
  }

  override def visitInnerClass(name: String, outername: String, innername: String, access: Int) = {
    val stInnerClass = stg.getInstanceOf("innerclassdef")
    stInnerClass.add("name", Util.getPilarClassName(name))
    if (outername!=null) stInnerClass.add("outerName", Util.getPilarClassName(outername))
    stInnerClass.add("innerName", innername)
    if (access != 0) stInnerClass.add("access", Util.getAccessFlag(access))

    addAnnotations("InnerClass", stInnerClass.render)
    record.innerClasses.add(name)
  }

  override def visitOuterClass(owner: String, name: String, desc: String) = {
    val stOuterClass = stg.getInstanceOf("outerclassdef")
    stOuterClass.add("owner", owner)
    if (name != null) {
      stOuterClass.add("method", name + desc)
    }

    addAnnotations("OuterClass", stOuterClass.render)
    record.outerClasses.add(name)
  }

  override def visitField(access: Int, name: String, desc: String, signature: String, value: Object) = {
    val field = new Field(access, record.getClassName + "." + name, desc, signature, value)
    val bfv = new BytecodeFieldVisitor(field)

    if (signature != null) {
      val stType = stg.getInstanceOf("typesigdef")
      stType.add("text", signature)

      val sr: SignatureReader = new SignatureReader(signature)
      val tsv = new BytecodeSignatureVisitor
      sr.acceptType(tsv)

      field.annotations.put("Signature", stType.render)
    }
    record.addFields(field)
    bfv
  }

  override def visitMethod(access: Int, name: String, desc: String, signature: String, exceptions: Array[String]) = {
    val procedure = new Procedure(access, name, desc, signature, exceptions, record)
    val bmv = new BytecodeMethodVisitor(procedure)

    if (signature != null) {
      val stMethod = stg.getInstanceOf("methodsigdef")
      stMethod.add("text", signature)

      val sr: SignatureReader = new SignatureReader(signature)
      val tsv = new BytecodeSignatureVisitor
      sr.acceptType(tsv)

      procedure.annotations.put("Signature", stMethod.render)
    }
    record.procedures.add(procedure)
    bmv
  }
}