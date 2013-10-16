package org.sireum.jvm.translator

import scala.Array.canBuildFrom
import scala.collection.JavaConversions.mapAsJavaMap
import scala.collection.immutable.Stack
import scala.collection.mutable

import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.sireum.jvm.models.Procedure
import org.sireum.jvm.models.Variable
import org.sireum.jvm.util.Util
import org.stringtemplate.v4.STGroupFile

class BytecodeMethodVisitor(api: Int, mv: MethodVisitor, procedure: Procedure) extends MethodVisitor(api, mv) {
  def this(proc: Procedure) = this(Opcodes.ASM4, null, proc)

  val stg = new STGroupFile(getClass.getResource("pilar.stg"), "UTF-8", '<', '>')
  val exceptionMap = mutable.Set[Label]()
  val stackMap = mutable.Map[Label, Stack[Variable]]()
  val labelMap = mutable.Map[Label, Int]()
  val a2z = {
    for (x <- ('A' to 'Z'); y <- ('a' to 'z')) yield (s"$x$y")
  }

  var varStack = Stack[Variable]()
  var localVariableCount: Int = -1
  var labelStr: String = "L00000"
  var currentLine: Int = 0
  var currentStack: Int = 0
  var maxStack: Int = -1

  def getTopAndUpdate() = {
    val result = varStack.top
    varStack = varStack.pop
    result
  }
  def popValue() = getTopAndUpdate.value
  def pushValue(va: Variable) { varStack = varStack.push(va) }
  def changeLastVar() {
    if (!varStack.isEmpty && !varStack.top.value.equals("jmp")) {
      val l = getTopAndUpdate
      addCodeLine("jmp := " + l.value)
      pushValue(Variable(l.typ, "jmp"))
    }
  }
  def getStackVar() = {
    val stackVar = s"s$currentStack"
    if (currentStack > maxStack) {
      maxStack = currentStack
      procedure.locals.add(stackVar)
    }
    currentStack += 1
    stackVar
  }

  def addCodeLine(code: String, typ: String = null, nosemicolon: Boolean = false) = {
    if (typ != null && !typ.isEmpty && !nosemicolon) {
      procedure.code.add(labelStr + a2z(currentLine) + ". " + code + " @Type " + typ + ";")
    } else if (typ != null && !typ.isEmpty && nosemicolon) {
      procedure.code.add(labelStr + a2z(currentLine) + ". " + code + " @Type " + typ)
    } else if (!nosemicolon) {
      procedure.code.add(labelStr + a2z(currentLine) + ". " + code + ";")
    } else if (nosemicolon) {
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
    val bav = new BytecodeAnnotationVisitor(name.substring(1), procedure)
    bav
  }

  override def visitCode() {
    //println(procedure.name)
    procedure.locals.add("local jmp")
  }

  override def visitEnd() = {
    if (procedure.code.isEmpty()) {
      addCodeLine("return @void")
    }

    if (localVariableCount != 0) {
      procedure.parameters.size() until localVariableCount foreach (i =>
        (procedure.locals.add(getVarName(i))))
    }
    //    if (!varStack.isEmpty) println(procedure.code.toArray().mkString("\n"))
    assert(varStack.isEmpty, varStack)

    varStack = null
    stackMap.clear
    exceptionMap.clear
    labelMap.clear
  }

  override def visitFieldInsn(opcode: Int, owner: String, name: String, desc: String) = opcode match {
    case Opcodes.GETSTATIC => {
      val stackVar = getStackVar
      addCodeLine(stackVar + " := " + Util.getPilarStaticField(owner + "." + name), Util.getTypeString(desc))
      pushValue(Variable(Util.getTypeString(desc), stackVar))
    }
    case Opcodes.GETFIELD => {
      val stackVar = getStackVar
      addCodeLine(stackVar + " := " + popValue + "." + Util.getPilarField(owner + "." + name), Util.getTypeString(desc))
      pushValue(Variable(Util.getTypeString(desc), stackVar))
    }
    case Opcodes.PUTSTATIC => {
      addCodeLine(Util.getPilarStaticField(owner + "." + name) + " := " + popValue, Util.getTypeString(desc))
    }
    case Opcodes.PUTFIELD => {
      val value = popValue
      addCodeLine(popValue + "." + Util.getPilarField(owner + "." + name) + " := " + value, Util.getTypeString(desc))
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

    typ match {
      case Opcodes.F_CHOP => varStack = Stack()
      case Opcodes.F_SAME => varStack = Stack()
      case Opcodes.F_SAME1 => { assert(varStack.size == 1, varStack) }
      case Opcodes.F_FULL => {
        //      if(varStack.size!=nStack) {
        //        println(nStack)
        //        println(varStack)
        //      } 
        assert(varStack.size == nStack)
      }
      case _ => {}
    }
    addCodeLine(stFrame.render, nosemicolon = true)
  }

  override def visitIincInsn(vr: Int, increment: Int) = {
    val varName = getVarName(vr)
    addCodeLine(varName + " := " + varName + " + " + increment, "(|int|)")
  }

  override def visitInsn(opcode: Int) = opcode match {
    case Opcodes.NOP => { /*Nope*/ }
    case Opcodes.ACONST_NULL |
      Opcodes.ICONST_M1 | Opcodes.ICONST_0 | Opcodes.ICONST_1 | Opcodes.ICONST_2 |
      Opcodes.ICONST_3 | Opcodes.ICONST_4 | Opcodes.ICONST_5 |
      Opcodes.LCONST_0 | Opcodes.LCONST_1 | Opcodes.FCONST_0 |
      Opcodes.FCONST_1 | Opcodes.FCONST_2 | Opcodes.DCONST_0 |
      Opcodes.DCONST_1 => {
      pushValue(Variable(Util.getOpcodeType(opcode), Util.getOpcodeValue(opcode)))
    }
    case Opcodes.IALOAD | Opcodes.LALOAD | Opcodes.FALOAD | Opcodes.DALOAD |
      Opcodes.AALOAD | Opcodes.BALOAD | Opcodes.CALOAD | Opcodes.SALOAD => {
      val index = popValue
      val arrayref = popValue
      val stackVar = getStackVar

      addCodeLine(stackVar + " := " + arrayref + "[" + index + "]")
      pushValue(Variable(Util.getOpcodeType(opcode), stackVar))
    }
    case Opcodes.IASTORE | Opcodes.LASTORE | Opcodes.FASTORE | Opcodes.DASTORE |
      Opcodes.AASTORE | Opcodes.BASTORE | Opcodes.CASTORE | Opcodes.SASTORE => {
      val value = popValue
      val index = popValue
      val arrayref = popValue

      addCodeLine(arrayref + "[" + index + "]" + " := " + value)
    }
    case Opcodes.POP => { popValue }
    case Opcodes.POP2 => { popValue; popValue }
    case Opcodes.DUP => { pushValue(varStack.top) }
    case Opcodes.DUP_X1 => {
      val value1 = getTopAndUpdate
      val value2 = getTopAndUpdate

      pushValue(value1)
      pushValue(value2)
      pushValue(value1)
    }
    case Opcodes.DUP_X2 => {
      val value1 = getTopAndUpdate
      val value2 = getTopAndUpdate
      val value3 = getTopAndUpdate

      pushValue(value1)
      pushValue(value3)
      pushValue(value2)
      pushValue(value1)
    }
    case Opcodes.DUP2 => {
      val value1 = getTopAndUpdate
      val value2 = getTopAndUpdate

      pushValue(value2)
      pushValue(value1)
      pushValue(value2)
      pushValue(value1)
    }
    case Opcodes.DUP2_X1 => {
      val value1 = getTopAndUpdate
      val value2 = getTopAndUpdate
      val value3 = getTopAndUpdate

      pushValue(value2)
      pushValue(value1)
      pushValue(value3)
      pushValue(value2)
      pushValue(value1)
    }
    case Opcodes.DUP2_X2 => {
      val value1 = getTopAndUpdate
      val value2 = getTopAndUpdate
      val value3 = getTopAndUpdate
      val value4 = getTopAndUpdate

      pushValue(value2)
      pushValue(value1)
      pushValue(value4)
      pushValue(value3)
      pushValue(value2)
      pushValue(value1)
    }
    case Opcodes.SWAP => {
      val value1 = getTopAndUpdate
      val value2 = getTopAndUpdate

      pushValue(value1)
      pushValue(value2)
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

      pushValue(Variable(Util.getOpcodeType(opcode), "(" + value2 + Util.getOperator(opcode) + value1 + ")"))
    }
    case Opcodes.INEG | Opcodes.LNEG | Opcodes.FNEG | Opcodes.DNEG => {
      pushValue(Variable(Util.getOpcodeType(opcode), "-" + popValue))
    }
    case Opcodes.I2L | Opcodes.I2F | Opcodes.I2D | Opcodes.L2I |
      Opcodes.L2F | Opcodes.L2D | Opcodes.F2I | Opcodes.F2L |
      Opcodes.F2D | Opcodes.D2I | Opcodes.D2L | Opcodes.D2F | Opcodes.I2B |
      Opcodes.I2C | Opcodes.I2S => {
      pushValue(Variable(Util.getOpcodeType(opcode), "(" + Util.getOpcodeType(opcode) + ")" + popValue))
    }
    case Opcodes.LCMP | Opcodes.FCMPL | Opcodes.FCMPG | Opcodes.DCMPL |
      Opcodes.DCMPG => {
      val stackVar = getStackVar
      val value1 = popValue
      val value2 = popValue

      opcode match {
        case Opcodes.LCMP => addCodeLine(stackVar + " := lcmp(" + value1 + "," + value2 + ")")
        case Opcodes.FCMPG => addCodeLine(stackVar + " := fcmpg(" + value1 + "," + value2 + ")")
        case Opcodes.FCMPL => addCodeLine(stackVar + " := fcmpl(" + value1 + "," + value2 + ")")
        case Opcodes.DCMPG => addCodeLine(stackVar + " := dcmpg(" + value1 + "," + value2 + ")")
        case Opcodes.DCMPL => addCodeLine(stackVar + " := dcmpl(" + value1 + "," + value2 + ")")
      }
      pushValue(new Variable("(|int|)", stackVar))
    }
    case Opcodes.IRETURN | Opcodes.LRETURN | Opcodes.FRETURN | Opcodes.DRETURN |
      Opcodes.ARETURN => {
      addCodeLine("return " + popValue)
    }
    case Opcodes.RETURN => {
      addCodeLine("return @void")
    }
    case Opcodes.ARRAYLENGTH => {
      pushValue(new Variable("(|int|)", popValue + ".length"))
    }
    case Opcodes.ATHROW => {
      addCodeLine("throw " + popValue)
      varStack = Stack()
    }
    case Opcodes.MONITORENTER => {
      addCodeLine("(@monitorenter " + popValue + ")", nosemicolon = true)
    }
    case Opcodes.MONITOREXIT => {
      addCodeLine("(@monitorexit " + popValue + ")", nosemicolon = true)
    }
    case _ => { /*Oh God! how can this happen?*/ }
  }

  override def visitIntInsn(opcode: Int, operand: Int) = opcode match {
    case Opcodes.BIPUSH | Opcodes.SIPUSH => {
      pushValue(new Variable("(|int|)", operand.toString))
    }
    case Opcodes.NEWARRAY => {
      val typ = Util.getIntInsnType(operand)
      val stackVar = getStackVar()

      addCodeLine(stackVar + " := new " + typ + "[" + popValue + "]")
      pushValue(Variable(Util.getIntInsnType(operand) + "",
        stackVar))
    }
    case _ => { /*Oh God! how can this happen?*/ }
  }

  override def visitJumpInsn(opcode: Int, label: Label) = opcode match {
    case Opcodes.IFEQ | Opcodes.IFNE | Opcodes.IFLT | Opcodes.IFGE |
      Opcodes.IFGT | Opcodes.IFLE => {
      val value1 = popValue

      //changeLastVar()
      addCodeLine("if " + value1 + " " + Util.getOperator(opcode) + " 0 then goto " + getLabelId(label) + "Aa")
      stackMap += (label -> varStack)
    }
    case Opcodes.IF_ICMPEQ | Opcodes.IF_ICMPNE | Opcodes.IF_ICMPLT | Opcodes.IF_ICMPGE |
      Opcodes.IF_ICMPGT | Opcodes.IF_ACMPEQ | Opcodes.IF_ACMPNE | Opcodes.IF_ICMPLE => {
      val value1 = popValue
      val value2 = popValue

      //changeLastVar()
      addCodeLine("if " + value2 + " " + Util.getOperator(opcode) + " " + value1 + " then goto " + getLabelId(label) + "Aa")
      stackMap += (label -> varStack)
    }
    case Opcodes.GOTO => {
      changeLastVar()
      addCodeLine("goto " + getLabelId(label) + "Aa")
      stackMap += (label -> varStack)
    }
    case Opcodes.JSR => { /* I don't like you */ }
    case Opcodes.IFNULL | Opcodes.IFNONNULL => {
      val value1 = popValue

      //changeLastVar()
      addCodeLine("if " + value1 + " " + Util.getOperator(opcode) + " null then goto " + getLabelId(label) + "Aa")
      stackMap += (label -> varStack)
    }
    case _ => { /*Oh God! how can this happen?*/ }
  }

  override def visitLabel(label: Label) = {
    if (stackMap.contains(label)) {
      changeLastVar()
      varStack = stackMap.getOrElse(label, Stack())
    }
    if (exceptionMap.contains(label)) {
      varStack = Stack()
      pushValue(Variable("(|java.lang.Object|)", "\"Exception\""))
    }
    labelStr = getLabelId(label)
    currentLine = 0
    if (varStack.isEmpty) currentStack = 0
  }

  override def visitLdcInsn(cst: Object) = {
    if (cst.isInstanceOf[String]) {
      pushValue(new Variable("string", Util.getTextString(cst.toString)))
    } else if (cst.isInstanceOf[Float]) {
      pushValue(new Variable("float", cst.toString))
    } else if (cst.isInstanceOf[Long]) {
      pushValue(new Variable("long", cst.toString + "L"))
    } else if (cst.isInstanceOf[Type]) {
      val t = cst.asInstanceOf[Type]
      pushValue(new Variable("", Util.getPilarStaticField(t.getClassName() + ".class")))
    } else {
      pushValue(new Variable("", Util.getTextString(cst.toString)))
    }
  }

  override def visitLocalVariable(name: String, desc: String, signature: String, start: Label, end: Label, index: Int) = {
    val stLocal = stg.getInstanceOf("localdef")
    stLocal.add("i", index)
    stLocal.add("id", name)
    stLocal.add("start", getLabelId(start) + "Aa")
    stLocal.add("end", getLabelId(end) + "Aa")
    stLocal.add("type", Util.getTypeString(desc))

    if (index >= procedure.parameters.size()) procedure.locals.add(Util.getTypeString(desc) + " [|" + name + "|]")

    localVariableCount = 0
  }

  override def visitLookupSwitchInsn(dflt: Label, keys: Array[Int], labels: Array[Label]) = {
    val stSwitch = stg.getInstanceOf("switchins")
    stSwitch.add("var", popValue)

    val blocks = mapAsJavaMap(keys zip (labels map (x => getLabelId(x) + "Aa")) toMap)
    stSwitch.add("blocks", blocks)
    stSwitch.add("dflt", getLabelId(dflt) + "Aa")

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

      0 to argLength foreach (_ => { args = popValue :: args })

      val stackVar = getStackVar
      addCodeLine("call " + stackVar + "  := " + Util.getFunctionCall(owner, name, desc, Util.getMethodType(opcode), args))
      if (!desc.endsWith("V")) {
        pushValue(Variable(Util.getTypeString(desc), stackVar))
      }
    }
    case Opcodes.INVOKEDYNAMIC => { /* TODO: what to with this */ }
    case _ => { /*Oh God! how can this happen?*/ }
  }

  override def visitMultiANewArrayInsn(desc: String, dims: Int) = {
    var dim = List[String]()
    0 until dims foreach (_ => dim = "[" + popValue + "]" :: dim)

    val stackVar = getStackVar
    addCodeLine(stackVar + " := new " + Util.getTypeString(desc) + dim.mkString(""))
    pushValue(new Variable("", stackVar))
  }

  //  override def visitParameterAnnotation(parameter: Int, desc: String, visible: Boolean) = {
  //    val bav = new BytecodeAnnotationVisitor(procedure)
  //    //procedure.paramAnnotationsMap += (parameter -> Util.getTypeString(desc))
  //    bav
  //  }

  override def visitTableSwitchInsn(min: Int, max: Int, dflt: Label, labels: Label*) = {
    val stSwitch = stg.getInstanceOf("switchins")
    stSwitch.add("var", popValue)
    stSwitch.add("dflt", getLabelId(dflt) + "Aa")

    val blocks = mapAsJavaMap(min to max zip (labels map (x => getLabelId(x) + "Aa")) toMap)
    stSwitch.add("blocks", blocks)
    addCodeLine(stSwitch.render)
  }

  override def visitTryCatchBlock(start: Label, end: Label, handler: Label, typ: String) = {
    exceptionMap += (handler)

    procedure.addCatch(Util.getPilarClassName(typ), getLabelId(start) + "Aa",
      getLabelId(end) + "Aa", getLabelId(handler) + "Aa")
  }

  override def visitTypeInsn(opcode: Int, desc: String) = opcode match {
    case Opcodes.NEW => {
      val stackVar = getStackVar
      addCodeLine(stackVar + " := new " + Util.getPilarClassName(desc))
      pushValue(new Variable(Util.getTypeString(desc), stackVar))
    }
    case Opcodes.ANEWARRAY => {
      val stackVar = getStackVar
      addCodeLine(stackVar + " := new " + Util.getPilarClassName(desc) + "[" + popValue + "]")
      pushValue(new Variable(Util.getTypeString(desc), stackVar))
    }
    case Opcodes.CHECKCAST => {
      pushValue(Variable(Util.getPilarClassName(desc), "(" + Util.getPilarClassName(desc) + ") " + popValue))
    }
    case Opcodes.INSTANCEOF => {
      val stackVar = getStackVar
      addCodeLine(stackVar + " := " + popValue + " <: " + Util.getPilarClassName(desc))
      pushValue(new Variable("boolean", stackVar))
    }
    case _ => { /*Oh God! how can this happen?*/ }
  }

  override def visitVarInsn(opcode: Int, vr: Int) = opcode match {
    case Opcodes.ILOAD | Opcodes.LLOAD | Opcodes.FLOAD | Opcodes.DLOAD |
      Opcodes.ALOAD => {
      pushValue(new Variable(Util.varTypeMap.getOrElse(opcode, "unk"),
        getVarName(vr)))
    }
    case Opcodes.ISTORE | Opcodes.LSTORE | Opcodes.FSTORE | Opcodes.DSTORE |
      Opcodes.ASTORE => {
      val arg1 = getTopAndUpdate
      addCodeLine(getVarName(vr) + " := " + arg1.value, arg1.typ)
    }
    case Opcodes.RET => { addCodeLine("return " + getVarName(vr)) }
    case _ => { /*Oh God! how can this happen?*/ }
  }

}