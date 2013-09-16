package org.sireum.jvm.models

import org.sireum.jvm.util.Util
import scala.tools.asm.Type
import java.util.HashMap
import java.util.ArrayList
import java.util.HashSet
import scala.tools.asm.Label
import scala.collection.mutable

class Procedure(val access:Int,val name:String,val desc:String,val signature: String,val exceptions: Array[String],val owner: Record) extends BaseModel{
    val parameters = new ArrayList[String]()
    val code = new ArrayList[String]()
    val locals = new ArrayList[String]()
    val catchExceptions = new ArrayList[CatchException]()
    val paramAnnotationsMap = mutable.Map[Int, String]()
    val localVariableMap = if (owner.methodLocalMap == null) null 
    	else owner.methodLocalMap.getOrElse(name, null)
    
    // Getters for StringTemplate
	val getName = Util.getPilarMethod(owner.getClassName+"."+name)
	val getReturnType = Util.getTypeString(Type.getReturnType(desc).getDescriptor())
	val getCode = code
	val getLocals = locals
	
	val getParameters = {
      var i = 0
      if (!Util.isStatic(access)) {
        parameters.add(owner.getName +" "+getVarName(i))
        i+=1
      }
      
      for (argument <- Type.getArgumentTypes(desc)) {
        parameters.add(Util.getTypeString(argument.getDescriptor())+" "+ getVarName(i))
        i+=1
      }
      parameters
	}
    
    val getAnnotations = {
      annotations.put("Owner", owner.getName)
      annotations.put("Access", Util.getAccessFlag(access, name.equals("<init>")))
      annotations.put("Signature", Util.getFunctionSignature(owner.getClassName, name, desc))
      if (exceptions!=null) {
    	  annotations.put("Throws", (exceptions map (x=>{Util.getPilarName(x)})).mkString(","))
      }
      annotations
    }
    
    
    
    def getCatch() = catchExceptions
    def addCatch(typ: String, start: String, end: String, handler: String) = 
      catchExceptions.add(new CatchException(typ, start, end, handler))
    
    def getVarName(i: Int): String = getVarName(i, null)
    /* this is black magic, and needs to be turned into white */
    def getVarName(i: Int, l: String): String = {
      if (localVariableMap == null) {
        "[|v"+i+"|]"
      } else {
        val variables = localVariableMap.localVariable.getOrElse(i, null)
        if (variables == null) {
          "[|v"+i+"|]"
        } else if (variables.size == 1) {
          "[|"+variables(0).name+"|]"
        } else {
          val index = localVariableMap.labelLineMap.indexOf(l)
          val result = variables.find(x=> {
            val start = localVariableMap.labelLineMap.indexOf(x.start)
            val end = localVariableMap.labelLineMap.indexOf(x.end)
            //println(s"$x index: $index $l start: $start end: $end")
            index >= start-1 && index <=end
          })
          if (result.isDefined) "[|" + result.get.name + "|]"
          else "[|v"+i+"|]"
        }
      }
    }
      
    override def toString() = {
      getReturnType + getName
    }
    
    class CatchException(typ: String, start: String, end: String, handler: String)  {
      val getTyp = typ
      val getStart = start
      val getEnd = end
      val getHandler = handler
    }
}