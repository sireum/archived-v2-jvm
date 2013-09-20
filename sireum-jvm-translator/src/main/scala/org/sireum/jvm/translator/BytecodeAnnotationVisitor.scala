package org.sireum.jvm.translator

import java.lang.reflect.Array

import scala.tools.asm.AnnotationVisitor
import scala.tools.asm.Opcodes
import scala.tools.asm.Type
import scala.collection.mutable

import org.apache.commons.lang3.StringEscapeUtils
import org.sireum.jvm.models.BaseModel
import org.sireum.jvm.util.Util

class BytecodeAnnotationVisitor(api: Int, av: AnnotationVisitor, parentName: String, oldValues: mutable.MutableList[String], baseModel: BaseModel) extends AnnotationVisitor(api, av) {
  val values = mutable.MutableList[String]()
  def this(parentName:String, baseModel: BaseModel) = this(Opcodes.ASM4, null, parentName, null, baseModel)
  def this(parentName:String, oldValues: mutable.MutableList[String]) = this(Opcodes.ASM4, null, parentName, oldValues, null)

  override def visit(name: String, value: Object) {
    if (value.isInstanceOf[Type]) {
      values += (name +"="+ Util.getPilarClassName(Util.convertType(value.asInstanceOf[Type])))
    } else if (value.getClass().isArray) {
      val l = Array.getLength(value)

      var elements = List[Object]()
      0 until l foreach (i => elements = elements :+ Array.get(value, i))
      values += (name+"=`[" + elements.mkString(", ") + "]")
    } else {
      values += (name+"="+Util.getTextString(value.toString))
    }
  }
  override def visitAnnotation(name: String, desc: String) = {
    if (name != null)
      new BytecodeAnnotationVisitor(name + " (@"+Util.getPilarClassName(desc), values)
    else 
      new BytecodeAnnotationVisitor("(@"+Util.getPilarClassName(desc), values)
  }
  override def visitArray(name: String) = {
    new BytecodeAnnotationVisitor(name+"= (", values)
  }
  override def visitEnum(name: String, desc: String, value: String) {
    baseModel.annotations.put("Enum", name + " " +
      Util.getPilarClassName(Util.convertType(desc)) + " " + value)
  }
      
  override def visitEnd() {
    if (baseModel!=null && values.size!=0) {
      baseModel.annotations.put(Util.getPilarClassName(parentName), "("+values.mkString(", ")+")")
    } else if (baseModel!=null) {
      baseModel.annotations.put(Util.getPilarClassName(parentName), "")
    } else if (oldValues!=null && values.size!=0) {
      oldValues += (Util.getPilarClassName(parentName) + "(" + values.mkString(", ")+"))")
    } else {
      oldValues += (parentName)
    }
  }
}