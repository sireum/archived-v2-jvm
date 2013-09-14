package org.sireum.jvm.models

import org.sireum.jvm.util.Util
import java.util.HashMap
import scala.tools.asm.Type

class Field(access: Int, name: String, desc: String, signature: String, value: Object) extends BaseModel {
    
	def isGlobal = Util.isPublic(access) && Util.isStatic(access) && Util.isFinal(access)
	def getName() = if(isGlobal) Util.getPilarStaticField(name) else Util.getPilarField(name)
	def getType() = Util.getTypeString(desc)
	def getAnnotations() = {
	  annotations.put("AccessFlag", Util.getAccessFlag(access))
	  annotations
	}
}