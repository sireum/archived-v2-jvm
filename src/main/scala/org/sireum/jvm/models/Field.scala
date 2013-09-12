package org.sireum.jvm.models

import org.sireum.jvm.util.Util
import java.util.HashMap
import scala.tools.asm.Type

class Field(access: Int, name: String, desc: String, signature: String, value: Object) extends BaseModel {
    
	def isGlobal = Util.isPublic(access) && Util.isStatic(access) && Util.isFinal(access)
	def getName() = Util.getPilarName(name)
	def getType() = Util.convertType(desc)
	def getAnnotations() = {
	  annotations.put("AccessFlag", Util.getAccessFlag(access))
	  annotations
	}
}