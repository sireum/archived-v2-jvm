package org.sireum.jvm.models
import org.sireum.jvm.util.Util
import java.util.HashMap
import java.util.ArrayList

class Record(access: Int, name: String, supername: String, signature: String, interfaces: Array[String]) extends BaseModel {
    val globals = new ArrayList[Field]()
    val members = new ArrayList[Field]()
    val procedures = new ArrayList[Procedure]()
    val innerClasses = new ArrayList[String]()
    val outerClasses = new ArrayList[String]()
    
    val getClassName = Util.getRecordNameFromQName(name)
	val getName = Util.getPilarName(getClassName)
	val getPackageName = Util.getPackageNameFromQName(name)
	val getQName = name
	
	def getSupername() = {
      val supernames = Array(Util.getPilarName(supername))
	  
	  if(!interfaces.isEmpty) {
	    for(interface <- interfaces) {
	      supernames +: (Util.getPilarName(interface))
	    }
	  }
	  supernames
	}
	
	def getAnnotations() = {
	  if(access!=0) annotations.put("AccessFlag", Util.getAccessFlag(access))
	  annotations.put("type", Util.getRecordType(access))
	  annotations
	}
	
	def getMembers() = members
		
	def getGlobals = globals
	
	def getProcedures = procedures
	
	def getSignature() = {
	  ???
	}
		
	def addFields(field: Field) = 
	  if(field.isGlobal) globals.add(field)
	  else members.add(field)
}