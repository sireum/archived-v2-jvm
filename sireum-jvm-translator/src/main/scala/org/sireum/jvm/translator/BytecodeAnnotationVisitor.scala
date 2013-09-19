package org.sireum.jvm.translator

import java.lang.reflect.Array

import scala.tools.asm.AnnotationVisitor
import scala.tools.asm.Opcodes
import scala.tools.asm.Type

import org.apache.commons.lang3.StringEscapeUtils
import org.sireum.jvm.models.BaseModel
import org.sireum.jvm.util.Util

class BytecodeAnnotationVisitor(api: Int, av: AnnotationVisitor, baseModel: BaseModel) extends AnnotationVisitor(api, av) {
  def this(baseModel: BaseModel) = this(Opcodes.ASM4, null, baseModel)

  override def visit(name: String, value: Object) = {
    if (value.isInstanceOf[Type]) {
      baseModel.annotations.put(name,
        Util.getPilarClassName(Util.convertType(value.asInstanceOf[Type])))
    } else if (value.getClass().isArray) {
      val l = Array.getLength(value)

      var elements = List[Object]()
      0 until l foreach (i => elements = Array.get(value, i) :: elements)
      baseModel.annotations.put("Array", name + "`[" + elements.mkString(", ") + "]")
    } else {
      baseModel.annotations.put(name, "\""+StringEscapeUtils.escapeJava(value.toString)+"\"")
    }
  }
  override def visitAnnotation(name: String, desc: String) = {
    baseModel.annotations.put(name, Util.getPilarClassName(Util.convertType(desc)))
    this
  }
  override def visitArray(name: String) = {
    baseModel.annotations.put("Array", name)
    this
  }
  override def visitEnum(name: String, desc: String, value: String) =
    baseModel.annotations.put("Enum", name + " " +
      Util.getPilarClassName(Util.convertType(desc)) + " " + value)
}