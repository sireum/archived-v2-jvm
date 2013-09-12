package org.sireum.jvm.models

import org.sireum.jvm.util.Util
import scala.tools.asm.Type
import java.util.HashMap
import java.util.ArrayList

class Procedure(val access:Int,val name:String,val desc:String,val signature: String,val exceptions: Array[String],val owner: Record) extends BaseModel{
    val parameters = new ArrayList[String]()
    val code = new ArrayList[String]()
    val locals = new ArrayList[String]()
    
	val getName = Util.getPilarName(owner.getClassName+"."+name)
	val getReturnType = Util.convertType(Type.getReturnType(desc).getDescriptor())
	val getCode = code
	val getLocals = locals
	
	val getParameters = {
      var i = 0
      if (!Util.isStatic(access)) {
        parameters.add(owner.getName +" "+Util.getVarName(i)+" @type this")
        i+=1
      }
      for (argument <- Type.getArgumentTypes(desc)) {
        parameters.add(Util.convertType(argument.getDescriptor())+" "+Util.getVarName(i))
        i+=1
      }
      parameters
	}
    
    val getAnnotations = {
      annotations.put("owner", owner.getName)
      annotations.put("Access", Util.getAccessFlag(access, name.equals("<init>")))
      annotations.put("signature", Util.getFunctionSignature(owner.getClassName, name, desc))
      annotations
    }
    
    override def toString() = {
      getReturnType + getName
    }
}