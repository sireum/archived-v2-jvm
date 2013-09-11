package org.sireum.jvm.util

import scala.tools.asm.Opcodes
import scala.tools.asm.Type
import com.sun.org.apache.xpath.internal.compiler.FunctionTable

object Util {
  
  def getPilarName(name:String)  = {
    "[|"+name.replace("/",":")+"|]"
  }
  
  def getRecordNameFromQName(qname:String) = 
	if(qname.contains('/')) qname.drop(qname.lastIndexOf('/')+1)
    else qname
    
  def getPackageNameFromQName(qname:String) = {
    if(qname.contains("/")) qname.take(qname.lastIndexOf('/'))
    else ""
  }
  
  def getLabelId(i : Int) = "L" + i
  def getVarName(i: Int) = "v" + i
  
  def getFunctionCall(className:String, functionName: String, desc: String, functionType: String, args: List[String]) = {
    val functionCall = new StringBuilder
    functionCall ++= getPilarName(className+"."+functionName)
    functionCall ++= "("+args.mkString(",") + ")"
    functionCall ++= " @signature "+getFunctionSignature(className, functionName, desc)
    functionCall ++= " @classDescriptor "+getPilarName(className)
    functionCall ++= " @type "+ functionType
    functionCall.toString
  }
  
  def getFunctionSignature(className: String, functionName: String, desc: String) = 
    Util.getPilarName("L"+className +";."+functionName+":"+desc)
  
  def convertType(t:Type):String = t.getSort() match {
    case Type.ARRAY => {
      val temp = new StringBuilder
      temp ++= (Util.convertType(t.getElementType()))
      temp ++= "[]" * t.getDimensions()
      temp ++= " @type (object)"
      temp.toString()
    }
    case Type.BOOLEAN => "[|boolean|]"
    case Type.BYTE => "[byte|]"
    case Type.CHAR => "[char|]"
    case Type.DOUBLE => "[|double|]"
    case Type.FLOAT => "[|float|]"
    case Type.INT => "[|int|]"
    case Type.LONG => "[|long|]"
    case Type.OBJECT => Util.getPilarName(t.getInternalName())
    case Type.SHORT => "[|short|]"
    case Type.VOID => "[|void|]"
    case _ => "[|java.lang.Object|]"
  }
  
  def getAccessFlag(access:Int):String = getAccessFlag(access, false)
  
  def getAccessFlag(access:Int, isConstructor: Boolean) = {
    val accessType= new StringBuilder()
    if(isPublic(access))  {
      accessType ++= "PUBLIC_"
    } else if(isPrivate(access)) {
      accessType ++= "PRIVATE_"
    } else if(isProtected(access)) {
      accessType ++= "PROTECTED_"
    }
    
    if(isInterface(access)) {
      accessType ++= "INTERFACE_"
    }
    if(isStatic(access)) {
      accessType ++= "STATIC_"
    }
    if(isFinal(access)) {
      accessType ++= "FINAL_"
    }
    if(isEnum(access)) {
      accessType ++= "ENUM_"
    }
    if(isNative(access)) {
      accessType ++= "NATIVE_"
    }
    if(isStrict(access)) {
      accessType ++= "STRICT_"
    }
    if(isSynthetic(access)) {
      accessType ++= "SYNTHETIC_"
    }
    if(isTransient(access)) {
      accessType ++= "TRANSIENT_"
    }
    if(isVolatile(access)) {
      accessType ++= "VOLATILE_"
    }
    if(isVarArgs(access)) {
      accessType ++= "VARARGS_"
    }
    if(isConstructor) {
      accessType ++= "CONSTRUCTOR_"
    }
    
    accessType.toString.dropRight(1)
  }
  
  def getRecordType(access:Int) = 
    if(isInterface(access)) 
      "interface"
    else
      "class"
  
  def isAbstract(access:Int) =
    (access & Opcodes.ACC_ABSTRACT)!= 0
  
  def isAnnotation(access:Int) = 
    (access & Opcodes.ACC_ANNOTATION)!= 0
    
  def isBridge(access:Int) = 
    (access & Opcodes.ACC_BRIDGE)!= 0
    
  def isDeprecated(access:Int) = 
    (access & Opcodes.ACC_DEPRECATED)!= 0
    
  def isEnum(access:Int) =
    (access & Opcodes.ACC_ENUM)!= 0
  
  def isFinal(access:Int) = 
    (access & Opcodes.ACC_FINAL)!= 0
    
  def isInterface(access:Int) = 
    (access & Opcodes.ACC_INTERFACE)!= 0
    
  def isNative(access:Int) = 
    (access & Opcodes.ACC_NATIVE)!= 0
    
  def isPrivate(access:Int) =
    (access & Opcodes.ACC_PRIVATE)!= 0
  
  def isPublic(access:Int) = 
    (access & Opcodes.ACC_PUBLIC)!= 0
    
  def isProtected(access:Int) = 
    (access & Opcodes.ACC_PROTECTED)!= 0
    
  def isStatic(access:Int) = 
    (access & Opcodes.ACC_STATIC)!= 0

  def isStrict(access:Int) =
    (access & Opcodes.ACC_STRICT)!= 0
  
  def isSuper(access:Int) = 
    (access & Opcodes.ACC_SUPER)!= 0
    
  def isSynchronized(access:Int) = 
    (access & Opcodes.ACC_SYNCHRONIZED)!= 0
    
  def isSynthetic(access:Int) = 
    (access & Opcodes.ACC_SYNTHETIC)!= 0
    
  def isTransient(access:Int) =
    (access & Opcodes.ACC_TRANSIENT)!= 0
  
  def isVarArgs(access:Int) = 
    (access & Opcodes.ACC_VARARGS)!= 0
    
  def isVolatile(access:Int) = 
    (access & Opcodes.ACC_VOLATILE)!= 0
}