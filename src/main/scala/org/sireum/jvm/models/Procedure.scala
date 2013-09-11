package org.sireum.jvm.models

import org.sireum.jvm.util.Util
import scala.tools.asm.Type
import java.util.HashMap
import java.util.ArrayList

class Procedure(access:Int, name:String, desc:String, signature: String, exceptions: Array[String], owner: Record) extends BaseModel{
    val parameters = new ArrayList[String]()
    val code = new ArrayList[String]()
    
	val getName = Util.getPilarName(owner.getClassName+"."+name)
	val getReturnType = Util.convertType(Type.getReturnType(desc))
	val getCode = code
	
	def getParameters() = {
      if (!Util.isStatic(access)) {
        parameters.add(owner.getName +" @type this")
      }
      for (argument <- Type.getArgumentTypes(desc)) {
        parameters.add(Util.convertType(argument))
      }
      parameters
	}
    
    def getAnnotations() = {
      annotations.put("owner", owner.getName)
      annotations.put("Access", Util.getAccessFlag(access, name.equals("<init>")))
      annotations.put("signature", Util.getFunctionSignature(owner.getClassName, name, desc))
      annotations
    }
    
    override def toString() = {
      getReturnType + getName
    }
}