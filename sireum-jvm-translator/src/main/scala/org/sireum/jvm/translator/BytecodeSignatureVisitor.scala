package org.sireum.jvm.translator

import scala.tools.asm.signature.SignatureVisitor
import org.stringtemplate.v4.ST
import scala.tools.asm.Opcodes
import org.stringtemplate.v4.STGroupFile
import org.sireum.jvm.util.Util
import scala.collection.mutable

class BytecodeSignatureVisitor(api: Int, val st: ST) extends SignatureVisitor(api) {
  val stg = new STGroupFile("pilar.stg")
  var currentSt : ST = null
  val stClassTypes = mutable.Stack[ST]()
  def this(st: ST) = this(Opcodes.ASM4, st)
  
  override def visitArrayType() = {
    val stArray = stg.getInstanceOf("arraytypesigdef")
    currentSt.add("element", stArray)
    currentSt = stArray
    this
  }
  override def visitBaseType(desc: Char) = {
    val stBase = stg.getInstanceOf("basetypesigdef")
    stBase.add("desc", Util.getPilarClassName(Util.convertType(desc.toString)))
    currentSt.add("element", stBase)
  }
  override def visitClassBound() = {
    currentSt = stg.getInstanceOf("classboundsigdef")
    st.add("classbound", currentSt)
    this
  }
  override def visitClassType(name: String) = {
    val stClassType = stg.getInstanceOf("classtypesigdef")
    stClassType.add("name", Util.getPilarClassName(name))
    if (stClassTypes.size == 0) {
      currentSt.add("element", stClassType)
    } else {
      stClassTypes.top.add("typearg", stClassType)
    }
    stClassTypes.push(stClassType)
  }
  override def visitExceptionType() = {
    currentSt = stg.getInstanceOf("exceptiontypesigdef")
    st.add("exceptionType", currentSt)
    this
  }
  override def visitFormalTypeParameter(name: String) = {
    val stTypeParam = stg.getInstanceOf("typeparamsigdef")
    stTypeParam.add("name", name)
    st.add("typeparam", stTypeParam)
  }
  override def visitInnerClassType(name: String) = {
    val stInnerClassType = stg.getInstanceOf("innerclasstypesigdef")
    stInnerClassType.add("name", name)
    stClassTypes.top.add("typeparam", stInnerClassType)    
  }
  override def visitInterface() = {
    currentSt = stg.getInstanceOf("interfacesigdef")
    st.add("interface", currentSt)
    this
  }
  override def visitInterfaceBound() = {
    currentSt = stg.getInstanceOf("interfaceboundsigdef")
    st.add("interfacebound", currentSt)
    this
  }
  override def visitParameterType() = {
    currentSt = stg.getInstanceOf("paramtypesigdef")
    st.add("paramtype", currentSt)
    this
  }
  override def visitReturnType() = {
    currentSt = stg.getInstanceOf("returntypesigdef")
    st.add("returntype", currentSt)
    this
  }
  override def visitSuperclass() = {
    currentSt = stg.getInstanceOf("superclasssigdef")
    st.add("superclass", currentSt)
    this
  }
  override def visitTypeArgument() = {
    val stType = stClassTypes.top
    stType.add("typeard", "@unboundedTypeArg")
  }
  override def visitTypeArgument(wildcard: Char) = {
    val stType = stClassTypes.top
    currentSt = stg.getInstanceOf("typeargsigdef")
    wildcard match {
      case SignatureVisitor.EXTENDS => currentSt.add("name", "Extends")
      case SignatureVisitor.INSTANCEOF => currentSt.add("name", "InstanceOf")
      case SignatureVisitor.SUPER => currentSt.add("name", "Super")
      case _ => {/* How can this happen */}
    }
    st.add("typearg", currentSt)
    this
  }
  override def visitTypeVariable(name: String) = {
    val stTypeVar = stg.getInstanceOf("typevarsigdef")
    stTypeVar.add("name", name)
    currentSt.add("element", stTypeVar)
  }
}