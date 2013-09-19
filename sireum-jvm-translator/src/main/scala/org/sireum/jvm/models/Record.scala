package org.sireum.jvm.models
import org.sireum.jvm.util.Util
import java.util.HashMap
import java.util.ArrayList

class Record(access: Int, name: String, supername: String, signature: String, interfaces: Array[String], val methodLocalMap: Map[String, LocalInfo]) extends BaseModel {
  val globals = new ArrayList[Field]()
  val members = new ArrayList[Field]()
  val procedures = new ArrayList[Procedure]()
  val innerClasses = new ArrayList[String]()
  val outerClasses = new ArrayList[String]()

  val getClassName = getRecordNameFromQName(name)
  val getPackageName = getPackageNameFromQName(name)
  val getName = Util.getPilarClassName(name)
  val getQName = name

  def getSupername() = {
    val supernames = Array(Util.getPilarClassName(supername))

    interfaces foreach (interface => supernames +: (Util.getPilarClassName(interface)))
    supernames
  }

  def getAnnotations() = {
    if (access != 0) annotations.put("AccessFlag", Util.getAccessFlag(access))
    annotations.put("type", Util.getRecordType(access))
    annotations
  }

  def getMembers = members
  def getGlobals = globals
  def getProcedures = procedures

  def addFields(field: Field) =
    if (field.isGlobal) globals.add(field)
    else members.add(field)
    
  def getRecordNameFromQName(qname: String) =
    if (qname.contains('/')) qname.drop(qname.lastIndexOf('/') + 1)
    else qname

  def getPackageNameFromQName(qname: String) = 
    if (qname.contains("/")) qname.take(qname.lastIndexOf('/'))
    else ""
}