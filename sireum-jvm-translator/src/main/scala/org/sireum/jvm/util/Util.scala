package org.sireum.jvm.util


import org.apache.commons.lang3.StringEscapeUtils
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

object Util {

  val opMap = Map(Opcodes.IADD -> "+", Opcodes.DADD -> "+", Opcodes.FADD -> "+", Opcodes.LADD -> "+",
    Opcodes.ISUB -> "-", Opcodes.FSUB -> "-", Opcodes.DSUB -> "-", Opcodes.LSUB -> "-",
    Opcodes.IMUL -> "*", Opcodes.LMUL -> "*", Opcodes.FMUL -> "*", Opcodes.DMUL -> "*",
    Opcodes.IDIV -> "/", Opcodes.LDIV -> "/", Opcodes.FDIV -> "/", Opcodes.DDIV -> "/",
    Opcodes.IREM -> "%", Opcodes.LREM -> "%", Opcodes.FREM -> "%", Opcodes.DREM -> "%",
    Opcodes.ISHL -> "^<", Opcodes.LSHL -> "^<",
    Opcodes.ISHR -> "^>>", Opcodes.LSHR -> "^>>", Opcodes.IUSHR -> "^>>>", Opcodes.LUSHR -> "^>>>",
    Opcodes.IAND -> "^&", Opcodes.LAND -> "^&",
    Opcodes.IOR -> "^|", Opcodes.LOR -> "^|",
    Opcodes.IXOR -> "^~", Opcodes.LXOR -> "^~",
    Opcodes.IFEQ -> "==", Opcodes.IFNE -> "!=", Opcodes.IFLE -> "<=", Opcodes.IFLT -> "<",
    Opcodes.IFGE -> ">=", Opcodes.IFGT -> ">", Opcodes.IF_ICMPEQ -> "==", Opcodes.IF_ICMPNE -> "!=",
    Opcodes.IF_ICMPGE -> ">=", Opcodes.IF_ICMPGT -> ">", Opcodes.IF_ICMPLE -> "<=", Opcodes.IF_ICMPLT -> "<",
    Opcodes.IF_ACMPEQ -> "==", Opcodes.IF_ACMPNE -> "!=", Opcodes.IFNULL -> "==", Opcodes.IFNONNULL -> "!=")

  val typeMap = Map(
    Opcodes.T_INT -> "int", Opcodes.T_BOOLEAN -> "boolean", Opcodes.T_BYTE -> "byte",
    Opcodes.T_CHAR -> "char", Opcodes.T_DOUBLE -> "double", Opcodes.T_FLOAT -> "float",
    Opcodes.T_LONG -> "long", Opcodes.T_SHORT -> "short")

  val insnTypeMap = Map(Opcodes.ICONST_0 -> "int", Opcodes.ICONST_1 -> "int", Opcodes.ICONST_2 -> "int",
    Opcodes.ICONST_3 -> "int", Opcodes.ICONST_4 -> "int", Opcodes.ICONST_5 -> "int",
    Opcodes.ICONST_M1 -> "int", Opcodes.LCONST_0 -> "long", Opcodes.LCONST_1 -> "long",
    Opcodes.FCONST_0 -> "float", Opcodes.FCONST_1 -> "float", Opcodes.FCONST_2 -> "float",
    Opcodes.DCONST_0 -> "double", Opcodes.DCONST_1 -> "double", Opcodes.ACONST_NULL -> "java.lang.Object",

    Opcodes.IALOAD -> "int", Opcodes.LALOAD -> "long", Opcodes.FALOAD -> "float",
    Opcodes.DALOAD -> "double", Opcodes.AALOAD -> "java.lang.Object", Opcodes.BALOAD -> "boolean",
    Opcodes.CALOAD -> "char", Opcodes.SALOAD -> "short", Opcodes.IASTORE -> "int",
    Opcodes.LASTORE -> "long", Opcodes.FASTORE -> " float", Opcodes.DASTORE -> "double",
    Opcodes.AASTORE -> "java.lang.Object", Opcodes.BASTORE -> "boolean", Opcodes.CASTORE -> "char",
    Opcodes.SASTORE -> "short",

    Opcodes.IADD -> "int", Opcodes.ISUB -> "int", Opcodes.IMUL -> "int",
    Opcodes.IDIV -> "int", Opcodes.IREM -> "int", Opcodes.ISHL -> "int", Opcodes.ISHR -> "int",
    Opcodes.IUSHR -> "int", Opcodes.IOR -> "int", Opcodes.IXOR -> "int", Opcodes.IAND -> "int",
    Opcodes.I2B -> "boolean", Opcodes.I2C -> "char", Opcodes.I2D -> "double", Opcodes.I2F -> "float",
    Opcodes.I2L -> "long", Opcodes.I2S -> "short", Opcodes.INEG -> "int",

    Opcodes.FADD -> "float", Opcodes.FSUB -> "float", Opcodes.FMUL -> "float",
    Opcodes.FDIV -> "float", Opcodes.FREM -> "float", Opcodes.F2D -> "double",
    Opcodes.F2I -> "int", Opcodes.F2L -> "long", Opcodes.FNEG -> "float",

    Opcodes.DADD -> "double", Opcodes.DSUB -> "double", Opcodes.DMUL -> "double",
    Opcodes.DDIV -> "double", Opcodes.DREM -> "double", Opcodes.D2F -> "float",
    Opcodes.D2I -> "int", Opcodes.D2L -> "long", Opcodes.DNEG -> "double",

    Opcodes.LADD -> "long", Opcodes.LSUB -> "long", Opcodes.LMUL -> "long",
    Opcodes.LDIV -> "long", Opcodes.LREM -> "long", Opcodes.L2D -> "double",
    Opcodes.L2F -> "float", Opcodes.L2I -> "integer", Opcodes.LNEG -> "long")

  val varTypeMap = Map(Opcodes.ILOAD -> "int", Opcodes.LLOAD -> "long", Opcodes.FLOAD -> "float",
    Opcodes.DLOAD -> "double", Opcodes.ALOAD -> "java.lang.Object")

  val valueMap = Map(Opcodes.ICONST_0 -> "0", Opcodes.ICONST_1 -> "1", Opcodes.ICONST_2 -> "2",
    Opcodes.ICONST_3 -> "3", Opcodes.ICONST_4 -> "4", Opcodes.ICONST_5 -> "5",
    Opcodes.ICONST_M1 -> "-1", Opcodes.LCONST_0 -> "0", Opcodes.LCONST_1 -> "1",
    Opcodes.FCONST_0 -> "0.0", Opcodes.FCONST_1 -> "1.0", Opcodes.FCONST_2 -> "2.0",
    Opcodes.DCONST_0 -> "0.0", Opcodes.DCONST_1 -> "1.0", Opcodes.ACONST_NULL -> "null")

  val methodTypeMap = Map(Opcodes.INVOKEVIRTUAL -> "virtual", Opcodes.INVOKESTATIC -> "direct",
    Opcodes.INVOKESPECIAL -> "special", Opcodes.INVOKEINTERFACE -> "interface")

  val frameTypeMap = Map(Opcodes.F_APPEND -> "Append", Opcodes.F_CHOP -> "Chop", Opcodes.F_FULL -> "Full",
    Opcodes.F_NEW -> "New", Opcodes.F_SAME -> "Same", Opcodes.F_SAME1 -> "Same1")

  val frameLocalTypeMap = Map(Opcodes.INTEGER -> "int", Opcodes.FLOAT -> "float", Opcodes.LONG -> "long",
    Opcodes.DOUBLE -> "double", Opcodes.TOP -> "top", Opcodes.NULL -> "null", Opcodes.UNINITIALIZED_THIS -> "this")

  def getMethodType(opcode: Int) = methodTypeMap.getOrElse(opcode, "virtual")
  def getFrameType(opcode: Int) = frameTypeMap.getOrElse(opcode, "unk")
  def getOpcodeType(opcode: Int) = getPilarClassName(insnTypeMap.getOrElse(opcode, "unk"))
  def getOpcodeValue(opcode: Int) = valueMap.getOrElse(opcode, "0")
  def getOperator(opcode: Int) = opMap.getOrElse(opcode, "#")
  def getIntInsnType(operand: Int) = getPilarClassName(typeMap.getOrElse(operand, "(|int|)"))

  def getPilarName(name: String) = "[|" + name.replace("/", ".") + "|]"

  def getPilarClassName(name: String) = {
    if (name == null) "(|unk|)"
    else "(|" + name.replace("/", ".") + "|)"
  }

  def getPilarMethod(name: String) = "{|" + name.replace("/", ".") + "|}"

  def getPilarStaticField(name: String) = "+|" + name.replace("/", ".") + "|+"

  def getPilarField(name: String) = "<|" + name.replace("/", ".") + "|>"
  
  def getTextString(s: String) = "\"" + StringEscapeUtils.escapeJava(s) + "\""

  def getFunctionCall(className: String, functionName: String, desc: String, functionType: String, args: List[String]) = {
    val functionCall = new StringBuilder
    functionCall ++= getPilarMethod(getFunctionSignature(className.replace("/","."), functionName, desc))
    functionCall ++= "(" + args.mkString(",") + ")"
    functionCall ++= " @signature " + "\""+ getFunctionSignature(className.replace("/","."), functionName, desc) +"\""
    functionCall ++= " @classDescriptor " + getPilarName(className)
    functionCall ++= " @type " + functionType
    functionCall.toString
  }

  def getFunctionSignature(className: String, functionName: String, desc: String) =
    className + "." + functionName + desc

  def getTypeString(desc: String) = getPilarClassName(convertType(desc))

  def convertType(desc: String): String = convertType(Type.getType(desc))

  def convertType(t: Type): String = t getSort match {
    case Type.ARRAY => {
      val temp = new StringBuilder
      temp ++= (Util.convertType(t.getElementType().getDescriptor()))
      temp ++= "[]" * t.getDimensions()
      temp.toString()
    }
    case Type.BOOLEAN => "boolean"
    case Type.BYTE => "byte"
    case Type.CHAR => "char"
    case Type.DOUBLE => "double"
    case Type.FLOAT => "float"
    case Type.INT => "int"
    case Type.LONG => "long"
    case Type.OBJECT => t.getInternalName
    case Type.SHORT => "short"
    case Type.VOID => "void"
    case _ => "java.lang.Object"
  }

  def getAccessFlag(access: Int): String = getAccessFlag(access, false)

  def getAccessFlag(access: Int, isConstructor: Boolean) = {
    var accessType = List[String]()
    if (isPublic(access)) {
      accessType = accessType :+ "PUBLIC"
    } else if (isPrivate(access)) {
      accessType = accessType :+ "PRIVATE"
    } else if (isProtected(access)) {
      accessType = accessType :+ "PROTECTED"
    }

    if (isInterface(access)) {
      accessType = accessType :+ "INTERFACE"
    }
    if (isStatic(access)) {
      accessType = accessType :+ "STATIC"
    }
    if (isFinal(access)) {
      accessType = accessType :+ "FINAL"
    }
    if (isEnum(access)) {
      accessType = accessType :+ "ENUM"
    }
    if (isNative(access)) {
      accessType = accessType :+ "NATIVE"
    }
    if (isStrict(access)) {
      accessType = accessType :+ "STRICT"
    }
    if (isSynthetic(access)) {
      accessType = accessType :+ "SYNTHETIC"
    }
    if (isTransient(access)) {
      accessType = accessType :+ "TRANSIENT"
    }
    if (isVolatile(access)) {
      accessType = accessType :+ "VOLATILE"
    }
    if (isVarArgs(access)) {
      accessType = accessType :+ "VARARGS"
    }
    if (isConstructor) {
      accessType = accessType :+ "CONSTRUCTOR"
    }

    "(" + accessType.mkString(",") + ")"
  }

  def getRecordType(access: Int) =
    if (isInterface(access))
      "interface"
    else
      "class"

  def isAbstract(access: Int) =
    (access & Opcodes.ACC_ABSTRACT) != 0

  def isAnnotation(access: Int) =
    (access & Opcodes.ACC_ANNOTATION) != 0

  def isBridge(access: Int) =
    (access & Opcodes.ACC_BRIDGE) != 0

  def isDeprecated(access: Int) =
    (access & Opcodes.ACC_DEPRECATED) != 0

  def isEnum(access: Int) =
    (access & Opcodes.ACC_ENUM) != 0

  def isFinal(access: Int) =
    (access & Opcodes.ACC_FINAL) != 0

  def isInterface(access: Int) =
    (access & Opcodes.ACC_INTERFACE) != 0

  def isNative(access: Int) =
    (access & Opcodes.ACC_NATIVE) != 0

  def isPrivate(access: Int) =
    (access & Opcodes.ACC_PRIVATE) != 0

  def isPublic(access: Int) =
    (access & Opcodes.ACC_PUBLIC) != 0

  def isProtected(access: Int) =
    (access & Opcodes.ACC_PROTECTED) != 0

  def isStatic(access: Int) =
    (access & Opcodes.ACC_STATIC) != 0

  def isStrict(access: Int) =
    (access & Opcodes.ACC_STRICT) != 0

  def isSuper(access: Int) =
    (access & Opcodes.ACC_SUPER) != 0

  def isSynchronized(access: Int) =
    (access & Opcodes.ACC_SYNCHRONIZED) != 0

  def isSynthetic(access: Int) =
    (access & Opcodes.ACC_SYNTHETIC) != 0

  def isTransient(access: Int) =
    (access & Opcodes.ACC_TRANSIENT) != 0

  def isVarArgs(access: Int) =
    (access & Opcodes.ACC_VARARGS) != 0

  def isVolatile(access: Int) =
    (access & Opcodes.ACC_VOLATILE) != 0
    
  
  def time[A](f: => A) = {
    val s = System.nanoTime
    val ret = f
    println("time: " + (System.nanoTime - s) / 1e6 + "ms")
    ret
  }
}