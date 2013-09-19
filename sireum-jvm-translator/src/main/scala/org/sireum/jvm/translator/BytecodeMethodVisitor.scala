package org.sireum.jvm.translator

import scala.Array.canBuildFrom
import scala.annotation.elidable
import scala.collection.JavaConversions.mapAsJavaMap
import scala.collection.mutable
import scala.tools.asm.Label
import scala.tools.asm.MethodVisitor
import scala.tools.asm.Opcodes
import scala.tools.asm.Type

import org.sireum.jvm.models.Procedure
import org.sireum.jvm.models.Variable
import org.sireum.jvm.util.Util
import org.stringtemplate.v4.STGroupFile

class BytecodeMethodVisitor(api: Int, mv: MethodVisitor, procedure: Procedure) extends MethodVisitor(api, mv) {
  def this(proc: Procedure) = this(Opcodes.ASM4, null, proc)

  val labelMap = mutable.Map[Label, Int]()
  val varStack = mutable.Stack[Variable]()
  val a2z = ('a' to 'z').toList
  val stg = new STGroupFile("pilar.stg")

  var localVariableCount: Int = -1
  var labelStr: String = null
  var currentLine: Int = 0
  var currentLocal: Int = 0
  var currentStack: Int = 0
  var maxStack: Int = -1

  def popValue() = varStack.pop.value
  def getStackVar() = {
    val stackVar = s"s$currentStack"
    if (currentStack > maxStack) {
      maxStack = currentStack
      procedure.locals.add(stackVar)
    }
    currentStack += 1
    stackVar
  }

  def addCodeLine(code: String): Unit = addCodeLine(code, null, false)

  def addCodeLine(code: String, typ: String): Unit = addCodeLine(code, typ, false)

  def addCodeLine(code: String, nosemicolon: Boolean): Unit = addCodeLine(code, null, nosemicolon)

  def addCodeLine(code: String, typ: String, nosemi: Boolean) = {
    if (typ != null && !nosemi) {
      procedure.code.add(labelStr + a2z(currentLine) + ". " + code + " @type " + typ + ";")
    } else if (typ != null && nosemi) {
      procedure.code.add(labelStr + a2z(currentLine) + ". " + code + " @type " + typ)
    } else if (!nosemi) {
      procedure.code.add(labelStr + a2z(currentLine) + ". " + code + ";")
    } else if (nosemi) {
      procedure.code.add(labelStr + a2z(currentLine) + ". " + code)
    }
    currentLine += 1
  }

  def getLabelId(l: Label) = {
    val i = labelMap.getOrElseUpdate(l, labelMap.size)
    f"L$i%05d"
  }

  def getVarName(i: Int) = procedure.getVarName(i, labelStr)

  def getFrameLocalType(o: Object) = {
    if (o.isInstanceOf[String]) {
      Util.getTypeString(o.asInstanceOf[String])
    } else if (o.isInstanceOf[Label]) {
      ":" + getLabelId(o.asInstanceOf[Label])
    } else {
      Util.getPilarClassName(Util.frameLocalTypeMap.getOrElse(o.asInstanceOf[Int], "unk"))
    }
  }

  override def visitAnnotation(name: String, visible: Boolean) = {
    val bav = new BytecodeAnnotationVisitor(procedure)
    procedure.annotations.put(Util.getTypeString(name), "")
    bav
  }

  override def visitCode() = {
    //prntln(procedure.name + procedure.desc)
    procedure.locals.add("local dummy")
  }

  override def visitEnd() = {
    if (procedure.code.isEmpty()) {
      addCodeLine("return @void")
    }

    if (localVariableCount != 0) {
      procedure.parameters.size() until localVariableCount foreach (i => 
        (procedure.locals.add(getVarName(i))))
    }
    //if (!varStack.isEmpty) println(procedure.code.toArray().mkString("\n"))
    //assert(varStack.isEmpty, varStack)
  }

  override def visitFieldInsn(opcode: Int, owner: String, name: String, desc: String) = opcode match {
    case Opcodes.GETSTATIC => {
      val stackVar = getStackVar
      addCodeLine(stackVar + ":= " + Util.getPilarStaticField(owner + "." + name))
      varStack.push(Variable(Util.getTypeString(desc), stackVar))
    }
    case Opcodes.GETFIELD => {
      val stackVar = getStackVar
      addCodeLine(stackVar + ":= " + popValue + "." + Util.getPilarField(owner + "." + name))
      varStack.push(Variable(Util.getTypeString(desc), stackVar))
    }
    case Opcodes.PUTSTATIC => {
      addCodeLine(Util.getPilarStaticField(owner + "." + name) + ":= " + popValue)
    }
    case Opcodes.PUTFIELD => {
      val value = popValue
      addCodeLine(popValue + "." + Util.getPilarField(owner + "." + name) + ":= " + value)
    }
    case _ => { /*Oh God! how can this happen?*/ }
  }

  override def visitFrame(typ: Int, nLocal: Int, local: Array[Object], nStack: Int, stack: Array[Object]) = {
    val stFrame = stg.getInstanceOf("framedef")
    stFrame.add("type", Util.getFrameType(typ))
    stFrame.add("nLocal", nLocal)
    stFrame.add("local", local map (x => getFrameLocalType(x)))
    stFrame.add("nStack", nStack)
    stFrame.add("stack", stack map (x => getFrameLocalType(x)))

//    typ match {
//      case Opcodes.F_CHOP => varStack.clear
//      case Opcodes.F_SAME => varStack.clear
//      case Opcodes.F_SAME1 => assert (varStack.size == 1, varStack)
//      case Opcodes.F_FULL => { varStack.clear
//      0 until nStack foreach (_=>varStack.push(Variable("java.lang.Object", "Exception"))) }
//      case _ => {}
//    }
    addCodeLine(stFrame.render, nosemicolon = true)
  }

  override def visitIincInsn(vr: Int, increment: Int) = {
    val varName = getVarName(vr)
    addCodeLine(varName + ":= " + varName + " + " + increment, "(|int|)")
  }

  override def visitInsn(opcode: Int) = opcode match {
    case Opcodes.NOP => { /*Nope*/ }
    case Opcodes.ACONST_NULL => { varStack.push(Variable("java.lang.Object", "null")) }
    case Opcodes.ICONST_M1 | Opcodes.ICONST_0 | Opcodes.ICONST_1 | Opcodes.ICONST_2 |
      Opcodes.ICONST_3 | Opcodes.ICONST_4 | Opcodes.ICONST_5 |
      Opcodes.LCONST_0 | Opcodes.LCONST_1 | Opcodes.FCONST_0 |
      Opcodes.FCONST_1 | Opcodes.FCONST_2 | Opcodes.DCONST_0 |
      Opcodes.DCONST_1 => {
      varStack.push(Variable(Util.getOpcodeType(opcode), Util.getOpcodeValue(opcode)))
    }
    case Opcodes.IALOAD | Opcodes.LALOAD | Opcodes.FALOAD | Opcodes.DALOAD |
      Opcodes.AALOAD | Opcodes.BALOAD | Opcodes.CALOAD | Opcodes.SALOAD => {
      val index = popValue
      val arrayref = popValue
      val stackVar = getStackVar

      addCodeLine(stackVar + ":= " + arrayref + "[" + index + "]")
      varStack.push(Variable(Util.getOpcodeType(opcode), stackVar))
    }
    case Opcodes.IASTORE | Opcodes.LASTORE | Opcodes.FASTORE | Opcodes.DASTORE |
      Opcodes.AASTORE | Opcodes.BASTORE | Opcodes.CASTORE | Opcodes.SASTORE => {
      val value = popValue
      val index = popValue
      val arrayref = popValue

      addCodeLine(arrayref + "[" + index + "]" + ":= " + value)
    }
    case Opcodes.POP => { varStack.pop }
    case Opcodes.POP2 => { varStack.pop; varStack.pop }
    case Opcodes.DUP => { varStack.push(varStack.top) }
    case Opcodes.DUP_X1 => {
      val value1 = varStack.pop
      val value2 = varStack.pop

      varStack.push(value1)
      varStack.push(value2)
      varStack.push(value1)
    }
    case Opcodes.DUP_X2 => {
      val value1 = varStack.pop
      val value2 = varStack.pop
      val value3 = varStack.pop

      varStack.push(value1)
      varStack.push(value3)
      varStack.push(value2)
      varStack.push(value1)
    }
    case Opcodes.DUP2 => {
      val value1 = varStack.pop
      val value2 = varStack.pop

      varStack.push(value2)
      varStack.push(value1)
      varStack.push(value2)
      varStack.push(value1)
    }
    case Opcodes.DUP2_X1 => {
      val value1 = varStack.pop
      val value2 = varStack.pop
      val value3 = varStack.pop

      varStack.push(value2)
      varStack.push(value1)
      varStack.push(value3)
      varStack.push(value2)
      varStack.push(value1)
    }
    case Opcodes.DUP2_X2 => {
      val value1 = varStack.pop
      val value2 = varStack.pop
      val value3 = varStack.pop
      val value4 = varStack.pop

      varStack.push(value2)
      varStack.push(value1)
      varStack.push(value4)
      varStack.push(value3)
      varStack.push(value2)
      varStack.push(value1)
    }
    case Opcodes.SWAP => {
      val value1 = varStack.pop
      val value2 = varStack.pop

      varStack.push(value1)
      varStack.push(value2)
    }
    case Opcodes.IADD | Opcodes.FADD | Opcodes.DADD | Opcodes.LADD |
      Opcodes.ISUB | Opcodes.LSUB | Opcodes.FSUB | Opcodes.DSUB |
      Opcodes.IMUL | Opcodes.LMUL | Opcodes.FMUL | Opcodes.DMUL |
      Opcodes.IDIV | Opcodes.LDIV | Opcodes.FDIV | Opcodes.DDIV |
      Opcodes.IREM | Opcodes.LREM | Opcodes.FREM | Opcodes.DREM |
      Opcodes.ISHL | Opcodes.LSHL |
      Opcodes.ISHR | Opcodes.LSHR |
      Opcodes.IUSHR | Opcodes.LUSHR |
      Opcodes.IAND | Opcodes.LAND |
      Opcodes.IOR | Opcodes.LOR |
      Opcodes.IXOR | Opcodes.LXOR => {
      val value1 = popValue
      val value2 = popValue

      varStack.push(new Variable(Util.getOpcodeType(opcode), value2 + Util.getOperator(opcode) + value1))
    }
    case Opcodes.INEG | Opcodes.LNEG | Opcodes.FNEG | Opcodes.DNEG => {
      varStack.push(new Variable(Util.getOpcodeType(opcode), "-" + popValue))
    }
    case Opcodes.I2L | Opcodes.I2F | Opcodes.I2D | Opcodes.L2I |
      Opcodes.L2F | Opcodes.L2D | Opcodes.F2I | Opcodes.F2L |
      Opcodes.F2D | Opcodes.D2I | Opcodes.D2L | Opcodes.D2F | Opcodes.I2B |
      Opcodes.I2C | Opcodes.I2S => {
      varStack.push(new Variable(Util.getOpcodeType(opcode), popValue))
    }
    case Opcodes.LCMP | Opcodes.FCMPL | Opcodes.FCMPG | Opcodes.DCMPL |
      Opcodes.DCMPG => {
      val stackVar = getStackVar
      val value1 = popValue
      val value2 = popValue

      opcode match {
        case Opcodes.LCMP => addCodeLine(stackVar + ":= lcmp(" + value1 + "," + value2 + ")")
        case Opcodes.FCMPG => addCodeLine(stackVar + ":= fcmpg(" + value1 + "," + value2 + ")")
        case Opcodes.FCMPL => addCodeLine(stackVar + ":= fcmpl(" + value1 + "," + value2 + ")")
        case Opcodes.DCMPG => addCodeLine(stackVar + ":= dcmpg(" + value1 + "," + value2 + ")")
        case Opcodes.DCMPL => addCodeLine(stackVar + ":= dcmpl(" + value1 + "," + value2 + ")")
      }
      varStack.push(new Variable("(|int|)", stackVar))
    }
    case Opcodes.IRETURN | Opcodes.LRETURN | Opcodes.FRETURN | Opcodes.DRETURN |
      Opcodes.ARETURN => {
      addCodeLine("return " + popValue)
    }
    case Opcodes.RETURN => {
      addCodeLine("return @void")
    }
    case Opcodes.ARRAYLENGTH => {
      varStack.push(new Variable("(|int|)", popValue + ".length"))
    }
    case Opcodes.ATHROW => {
      addCodeLine("throw " + popValue)
    }
    case Opcodes.MONITORENTER => {
      addCodeLine("(@monitorenter " + varStack.pop.value + ")", nosemicolon = true)
    }
    case Opcodes.MONITOREXIT => {
      addCodeLine("(@monitorexit " + varStack.pop.value + ")", nosemicolon = true)
    }
    case _ => { /*Oh God! how can this happen?*/ }
  }

  override def visitIntInsn(opcode: Int, operand: Int) = opcode match {
    case Opcodes.BIPUSH | Opcodes.SIPUSH => {
      varStack.push(new Variable("(|int|)", operand.toString))
    }
    case Opcodes.NEWARRAY => {
      val typ = Util.getIntInsnType(operand)
      val stackVar = getStackVar()

      addCodeLine(stackVar + ":= new " + typ + "[" + popValue + "]")
      varStack.push(Variable(Util.getIntInsnType(operand) + "",
        stackVar))
    }
    case _ => { /*Oh God! how can this happen?*/ }
  }

  override def visitJumpInsn(opcode: Int, label: Label) = opcode match {
    case Opcodes.IFEQ | Opcodes.IFNE | Opcodes.IFLT | Opcodes.IFGE |
      Opcodes.IFGT | Opcodes.IFLE => {
      val value1 = popValue

      addCodeLine("if " + value1 + " " + Util.getOperator(opcode) + " 0 then goto " + getLabelId(label) + "a")
    }
    case Opcodes.IF_ICMPEQ | Opcodes.IF_ICMPNE | Opcodes.IF_ICMPLT | Opcodes.IF_ICMPGE |
      Opcodes.IF_ICMPGT | Opcodes.IF_ACMPEQ | Opcodes.IF_ACMPNE | Opcodes.IF_ICMPLE => {
      val value1 = popValue
      val value2 = popValue

      addCodeLine("if " + value2 + " " + Util.getOperator(opcode) + " " + value1 + " then goto " + getLabelId(label) + "a")
    }
    case Opcodes.GOTO => {
      addCodeLine("goto " + getLabelId(label) + "a")
    }
    case Opcodes.JSR => { /* I don't like you */ }
    case Opcodes.IFNULL | Opcodes.IFNONNULL => {
      val value1 = popValue

      addCodeLine("if " + value1 + " " + Util.getOperator(opcode) + " null then goto " + getLabelId(label) + "a")
    }
    case _ => { /*Oh God! how can this happen?*/ }
  }

  override def visitLabel(label: Label) = {
    labelStr = getLabelId(label)
    currentLine = 0
    currentStack = 0
//     if(!varStack.isEmpty) println("WHAT??" + labelStr)
//    if(!varStack.isEmpty) println(procedure.code.toArray.mkString("\n"))
  }

  override def visitLdcInsn(cst: Object) = {
    if (cst.isInstanceOf[String]) {
      varStack.push(new Variable("string", Util.getTextString(cst.toString)))
    } else if (cst.isInstanceOf[Float]) {
      varStack.push(new Variable("float", cst.toString))
    } else if (cst.isInstanceOf[Long]) {
      varStack.push(new Variable("long", cst.toString + "L"))
    } else {
      varStack.push(new Variable("", Util.getTextString(cst.toString)))
    }
  }

  override def visitLineNumber(line: Int, start: Label) = {
    //println(getLabelId(start) + "->" + line)
  }

  override def visitLocalVariable(name: String, desc: String, signature: String, start: Label, end: Label, index: Int) = {
    currentLocal += 1

    val stLocal = stg.getInstanceOf("localdef")
    stLocal.add("i", index)
    stLocal.add("id", name)
    stLocal.add("start", getLabelId(start) + "a")
    stLocal.add("end", getLabelId(end) + "a")
    stLocal.add("type", Util.getTypeString(desc))

    if (index >= procedure.parameters.size()) procedure.locals.add(Util.getTypeString(desc) + " [|" + name + "|]")

    localVariableCount = 0
    //procedure.annotations.put("Local"+currentLocal, stLocal.render())
  }

  override def visitLookupSwitchInsn(dflt: Label, keys: Array[Int], labels: Array[Label]) = {
    val stSwitch = stg.getInstanceOf("switchins")
    stSwitch.add("var", popValue)

    val blocks = mapAsJavaMap(keys zip (labels map (x => getLabelId(x) + "a")) toMap)
    stSwitch.add("blocks", blocks)
    stSwitch.add("dflt", getLabelId(dflt) + "a")

    addCodeLine(stSwitch.render())
  }

  override def visitMaxs(maxStack: Int, maxLocals: Int) = {
    if (localVariableCount != 0) localVariableCount = maxLocals
    procedure.annotations.put("MaxStack", maxStack.toString)
    procedure.annotations.put("MaxLocals", maxLocals.toString)
  }

  override def visitMethodInsn(opcode: Int, owner: String, name: String, desc: String) = opcode match {
    case Opcodes.INVOKEVIRTUAL | Opcodes.INVOKESPECIAL | Opcodes.INVOKESTATIC | Opcodes.INVOKEINTERFACE => {
      var args = List[String]()
      var argLength = 0
      opcode match {
        case Opcodes.INVOKEVIRTUAL | Opcodes.INVOKESPECIAL | Opcodes.INVOKEINTERFACE => argLength = Type.getArgumentTypes(desc).length
        case Opcodes.INVOKESTATIC => argLength = (Type.getArgumentTypes(desc).length - 1)
      }

      0 to argLength foreach (_ => { args = varStack.pop.value :: args })

      val stackVar = getStackVar
      addCodeLine("call " + stackVar + ":= " + Util.getFunctionCall(owner, name, desc, Util.getMethodType(opcode), args))
      if (!desc.endsWith("V")) {
        varStack.push(new Variable(Util.getTypeString(desc), stackVar))
      }
    }
    case Opcodes.INVOKEDYNAMIC => { /* TODO: what to with this */ }
    case _ => { /*Oh God! how can this happen?*/ }
  }

  override def visitMultiANewArrayInsn(desc: String, dims: Int) = {
    var dim = List[String]()
    0 until dims foreach (_ => dim = "[" + popValue + "]" :: dim)

    val stackVar = getStackVar
    addCodeLine(stackVar + ":= new " + Util.getTypeString(desc) + dim.mkString(""))
    varStack.push(new Variable("", stackVar))
  }

  override def visitParameterAnnotation(parameter: Int, desc: String, visible: Boolean) = {
    val bav = new BytecodeAnnotationVisitor(procedure)
    procedure.paramAnnotationsMap += (parameter -> Util.getTypeString(desc))
    bav
  }

  override def visitTableSwitchInsn(min: Int, max: Int, dflt: Label, labels: Label*) = {
    val stSwitch = stg.getInstanceOf("switchins")
    stSwitch.add("var", popValue)
    stSwitch.add("dflt", getLabelId(dflt) + "a")

    val blocks = mapAsJavaMap(min to max zip (labels map (x => getLabelId(x) + "a")) toMap)
    stSwitch.add("blocks", blocks)
    addCodeLine(stSwitch.render)
  }

  override def visitTryCatchBlock(start: Label, end: Label, handler: Label, typ: String) = {
    varStack.push(Variable("java.lang.Object", Util.getTextString(typ)))

    procedure.addCatch(Util.getPilarClassName(typ), getLabelId(start) + "a",
      getLabelId(end) + "a", getLabelId(handler) + "a")
  }

  override def visitTypeInsn(opcode: Int, desc: String) = opcode match {
    case Opcodes.NEW => {
      val stackVar = getStackVar
      addCodeLine(stackVar + ":= new " + Util.getPilarClassName(desc))
      varStack.push(new Variable(Util.getTypeString(desc), stackVar))
    }
    case Opcodes.ANEWARRAY => {
      val stackVar = getStackVar
      addCodeLine(stackVar + ":= new " + Util.getPilarClassName(desc) + "[" + popValue + "]")
      varStack.push(new Variable(Util.getTypeString(desc), stackVar))
    }
    case Opcodes.CHECKCAST => {
      val stackVar = varStack.pop
      varStack.push(Variable(Util.getPilarClassName(desc), "("+Util.getPilarClassName(desc)+") "+stackVar.value))
    }
    case Opcodes.INSTANCEOF => {
      val stackVar = getStackVar
      addCodeLine(stackVar + ":= instanceof @varname " + popValue + " @type \"" +
        Util.getPilarClassName(desc) + "\"")
      varStack.push(new Variable("boolean", stackVar))
    }
    case _ => { /*Oh God! how can this happen?*/ }
  }

  override def visitVarInsn(opcode: Int, vr: Int) = opcode match {
    case Opcodes.ILOAD | Opcodes.LLOAD | Opcodes.FLOAD | Opcodes.DLOAD |
      Opcodes.ALOAD => {
      varStack.push(new Variable(Util.varTypeMap.getOrElse(opcode, "unk"),
        getVarName(vr)))
    }
    case Opcodes.ISTORE | Opcodes.LSTORE | Opcodes.FSTORE | Opcodes.DSTORE |
      Opcodes.ASTORE => {
      val arg1 = varStack.pop
      addCodeLine(getVarName(vr) + ":= " + arg1.value, arg1.typ)
    }
    case Opcodes.RET => { addCodeLine("return " + getVarName(vr)) }
    case _ => { /*Oh God! how can this happen?*/ }
  }

}