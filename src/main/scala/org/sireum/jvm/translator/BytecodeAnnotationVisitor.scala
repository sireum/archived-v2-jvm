package org.sireum.jvm.translator

import scala.tools.asm.AnnotationVisitor
import scala.tools.asm.Opcodes
import org.sireum.jvm.models.BaseModel
import org.sireum.jvm.util.Util
import scala.tools.asm.Type

class BytecodeAnnotationVisitor(api:Int, av:AnnotationVisitor, baseModel: BaseModel) extends AnnotationVisitor(api, av) {
  def this(baseModel: BaseModel)  = this(Opcodes.ASM4, null, baseModel)

  override def visit(name:String, value:Object) =  {
    if(value.isInstanceOf[Type]) {
      baseModel.annotations.put(name, Util.convertType(value.asInstanceOf[Type].getDescriptor()))
    } else if (value.getClass().isArray) {
      
    } else {
      baseModel.annotations.put(name, value.toString)
    }
  }
  override def visitAnnotation(name: String, desc: String) = { 
    baseModel.annotations.put(name, Util.convertType(desc))
    this
  }
  override def visitArray(name: String) = ???
  override def visitEnd() = {}
  override def visitEnum(name:String, desc:String, value:String) = ???
}