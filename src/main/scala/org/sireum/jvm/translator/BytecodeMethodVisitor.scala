package org.sireum.jvm.translator

import scala.tools.asm.MethodVisitor
import scala.tools.asm.Opcodes
import scala.tools.asm.Label
import scala.collection.mutable.Map
import scala.collection.mutable.Stack
import org.sireum.jvm.models.StackVar
import org.sireum.jvm.models.Procedure
import org.sireum.jvm.util.Util
import scala.tools.asm.Type
import org.sireum.jvm.models.StackVar
import org.stringtemplate.v4.STGroupFile

class BytecodeMethodVisitor(api:Int, mv:MethodVisitor, procedure: Procedure) extends MethodVisitor(api, mv) {
	def this(proc: Procedure) = this(Opcodes.ASM4, null, proc)
	
	val labelMap = Map[Label, Int]()
	val varStack = Stack[StackVar]()
	val a2z = ('a' to 'z').toList
	val stg = new STGroupFile("pilar.stg")
	
	var localVariableCount: Int = 0
	var labelStr: String = null
	var currentLine: Int = 0
    var currentLocal: Int = 0
	
    def addCodeLine(code: String) = {
	  procedure.code.add(labelStr + a2z(currentLine)+ ". " +code)
	  currentLine += 1
	}
	
	def popValue() = varStack.pop.value
	def popType() = varStack.pop.typ
	
	def getLabelId(l: Label) = {
	  val i = labelMap.getOrElseUpdate(l, labelMap.size)
	  f"L$i%05d"
	}
    
	override def visitAnnotation(name: String, visible:Boolean) = {
	  val bav = new BytecodeAnnotationVisitor(procedure)
	  bav
	}
	
	override def visitCode() = {
	  procedure.locals.add("local temp")
	  procedure.locals.add("ref")
	}
	
	override def visitEnd() = {
	  procedure.parameters.size() to localVariableCount foreach 
	  	(i => {procedure.locals.add(Util.getVarName(i))})
	  
	  if(procedure.code.isEmpty()) {
	    addCodeLine("return @void")
	  }
	  assert(varStack.isEmpty)
	}
	
	override def visitFieldInsn(opcode: Int, owner: String, name: String, desc: String) = opcode match {
	  case Opcodes.GETSTATIC => {
	    varStack.push(new StackVar(Util.convertType(desc), Util.getPilarName(owner+"."+name)))
	  } 
	  case Opcodes.GETFIELD => {
	    varStack.push(new StackVar(Util.convertType(desc), Util.getPilarName(owner+"."+name)))
	  }
	  case Opcodes.PUTSTATIC => {
	    addCodeLine(Util.getPilarName(owner+"."+name) + ":= " + popValue)
	  }
	  case Opcodes.PUTFIELD => {
	    val value = popValue
	    addCodeLine(varStack.pop.value + "." + Util.getPilarName(owner+"."+name) + ":= " + value)
	  }
	  case _ => {/*Oh God! how can this happen?*/}
	}
	
	override def visitFrame(typ: Int, nLocal: Int, local: Array[Object], nStack: Int, stack: Array[Object]) = {
	  
	}
	
	override def visitIincInsn(vr: Int, increment: Int) = {
	  val varName = Util.getVarName(vr)
	  addCodeLine(varName+":= "+varName+" + "+increment)
	}
	
	override def visitInsn(opcode: Int) = opcode match {
	  case Opcodes.NOP => { /*Nope*/ }
	  case Opcodes.ACONST_NULL => {varStack.push(new StackVar("object", "null"))}
	  case Opcodes.ICONST_M1 | Opcodes.ICONST_0 | Opcodes.ICONST_1 | Opcodes.ICONST_2 | 
	  	Opcodes.ICONST_3 | Opcodes.ICONST_4 | Opcodes.ICONST_5 | 
	  	Opcodes.LCONST_0 | Opcodes.LCONST_1 | Opcodes.FCONST_0 | 
	  	Opcodes.FCONST_1 | Opcodes.FCONST_2 | Opcodes.DCONST_0 | 
	  	Opcodes.DCONST_1 => {
	  	  varStack.push(new StackVar(Util.getOpcodeType(opcode), Util.getOpcodeValue(opcode)))
	  }
	  case Opcodes.IALOAD | Opcodes.LALOAD | Opcodes.FALOAD | Opcodes.DALOAD |
	    Opcodes.AALOAD | Opcodes.BALOAD | Opcodes.CALOAD | Opcodes.SALOAD => {
	      val index = popValue
	      val arrayref = popValue
	      varStack.push(new StackVar(Util.getOpcodeType(opcode), arrayref +"["+index+"]"))
	    }
	  case Opcodes.IASTORE | Opcodes.LASTORE | Opcodes.FASTORE | Opcodes.DASTORE |
	  	Opcodes.AASTORE | Opcodes.BASTORE | Opcodes.CASTORE | Opcodes.SASTORE => {
	  	  val value = popValue
	  	  val index = popValue
	  	  val arrayref = popValue
	  	  addCodeLine(arrayref + "["+index+"]"+" := "+value)
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
	      varStack.push(new StackVar(Util.getOpcodeType(opcode), value2 + Util.getOperator(opcode)+ value1))
	    } 
	  case  Opcodes.INEG | Opcodes.LNEG | Opcodes.FNEG | Opcodes.DNEG => {
	    varStack.push(new StackVar(Util.getOpcodeType(opcode), "-"+popValue))
	  }
	  case Opcodes.I2L => {}
	  case Opcodes.I2F => {}
	  case Opcodes.I2D => {}
	  case Opcodes.L2I => {}
	  case Opcodes.L2F => {}
	  case Opcodes.L2D => {}
	  case Opcodes.F2I => {}
	  case Opcodes.F2L => {}
	  case Opcodes.F2D => {}
	  case Opcodes.D2I => {} 
	  case Opcodes.D2L => {} 
	  case Opcodes.D2F => {}
	  case Opcodes.I2B => {}
	  case Opcodes.I2C => {}
	  case Opcodes.I2S => {}
	  case Opcodes.LCMP => {}
	  case Opcodes.FCMPL => {}
	  case Opcodes.FCMPG => {}
	  case Opcodes.DCMPL => {}
	  case Opcodes.DCMPG => {}
	  case Opcodes.IRETURN | Opcodes.LRETURN | Opcodes.FRETURN | Opcodes.DRETURN | Opcodes.ARETURN => {
	    addCodeLine("return "+popValue)
	  }
	  case Opcodes.RETURN => {
	    addCodeLine("return @void")
	  }
	  case Opcodes.ARRAYLENGTH => {}
	  case Opcodes.ATHROW => {}
	  case Opcodes.MONITORENTER => {
	    addCodeLine("@(monitorenter " + varStack.pop.value + ")")
	  }
	  case Opcodes.MONITOREXIT => {
	    addCodeLine("@(monitorexit " + varStack.pop.value + ")")
	  }
	  case _ => {/*Oh God! how can this happen?*/}
	}
	
	override def visitIntInsn(opcode: Int, operand: Int) = opcode match {
	  case Opcodes.BIPUSH | Opcodes.SIPUSH => {
	    varStack.push(new StackVar("int", operand.toString))
	  }
	  case Opcodes.NEWARRAY => {
	    val typ = Util.getPilarName(Util.typeMap.getOrElse(operand, "int"))
	    addCodeLine("ref:= new "+typ+"["+varStack.pop.value+"]")
	    varStack.push(new StackVar(Util.typeMap.getOrElse(operand, "int")+"[]", 
	        "ref"))
	  }
	  case _ => {/*Oh God! how can this happen?*/}
	}
	
	override def visitJumpInsn(opcode: Int, label: Label) = opcode match {
	  case Opcodes.IFEQ | Opcodes.IFNE | Opcodes.IFLT | Opcodes.IFGE | 
	  	Opcodes.IFGT | Opcodes.IFLE => {
	  	  val value1 = popValue
	  	  addCodeLine("if "+value1+" "+Util.getOperator(opcode)+" 0 then goto "+getLabelId(label))
	  	}
	  case Opcodes.IF_ICMPEQ | Opcodes.IF_ICMPNE | Opcodes.IF_ICMPLT | Opcodes.IF_ICMPGE |
	  	Opcodes.IF_ICMPGT | Opcodes.IF_ACMPEQ | Opcodes.IF_ACMPNE | Opcodes.GOTO => {
	  	  val value1 = popValue
	  	  val value2 = popValue
	  	  addCodeLine("if "+value2+" "+Util.getOperator(opcode)+" "+value1+" then goto "+getLabelId(label))
	  	}
	  case Opcodes.JSR => {/* I don't like you */}
	  case Opcodes.IFNULL | Opcodes.IFNONNULL => {
	    val value1 = popValue
	    addCodeLine("if "+value1+" "+Util.getOperator(opcode)+" null then goto "+getLabelId(label))
	  }
	  case _ => {/*Oh God! how can this happen?*/}
	}
	
	override def visitLabel(label: Label) = {
	  labelStr = getLabelId(label)
	  currentLine = 0
	}
	
	
	override def visitLdcInsn(cst: Object) = {
	  if (cst.isInstanceOf[String]) {
	    varStack.push(new StackVar("", "\""+cst.toString()+"\""))
	  } else {
	    varStack.push(new StackVar("", cst.toString()))
	  }
	}
	
	override def visitLineNumber(line:Int, start:Label) = {
	  //lineNumber = line.toString
	  //currentLine = 0
	}
	
	override def visitLocalVariable(name: String, desc: String, signature: String, start: Label, end: Label, index: Int) = {
	  currentLocal += 1
	  
	  val stLocal = stg.getInstanceOf("localdef")
	  stLocal.add("i", index)
	  stLocal.add("id", name)
	  stLocal.add("start", Util.getLabelId(labelMap.getOrElse(start, 0)))
	  stLocal.add("end", Util.getLabelId(labelMap.getOrElse(end, 0)))
	  stLocal.add("type", Util.convertType(desc))
	  
	  //procedure.annotations.put("Local"+currentLocal, stLocal.render())
	}
	
	override def visitLookupSwitchInsn(dflt: Label, keys: Array[Int], labels: Array[Label]) = {
	  
	}
	
	override def visitMaxs(maxStack: Int, maxLocals: Int) = {
	  localVariableCount = maxLocals
	  procedure.annotations.put("MaxStack", maxStack.toString)
	  procedure.annotations.put("MaxLocals", maxLocals.toString)
	}
	
	override def visitMethodInsn(opcode: Int, owner: String, name: String, desc: String) = opcode match {
	  case Opcodes.INVOKEVIRTUAL | Opcodes.INVOKESPECIAL | Opcodes.INVOKESTATIC | Opcodes.INVOKEINTERFACE => {
	    var args = List[String]()
	    0 to Type.getArgumentTypes(desc).length foreach (_ => {args = varStack.pop.value :: args})
	    
	    addCodeLine("call temp:= "+Util.getFunctionCall(owner, name, desc, Util.getMethodType(opcode), args))
	    if(!desc.endsWith("V")) {
	      varStack.push(new StackVar(Util.convertType(desc), "temp"))
	    }
	  }
	  case Opcodes.INVOKEDYNAMIC => {}
	  case _ => {}
	}
	
	override def visitMultiANewArrayInsn(desc: String, dims: Int) = {
	  
	}
	
	override def visitParameterAnnotation(parameter: Int, desc: String, visible: Boolean) = {
	  val bav = new BytecodeAnnotationVisitor(procedure)
	  bav
	}
	
	override def visitTableSwitchInsn(min: Int, max: Int, dflt: Label, labels: Label*) = {
	  
	}
	
	override def visitTryCatchBlock(start: Label, end: Label, handler: Label, typ:String) = {
	  
	}
	
	override def visitTypeInsn(opcode: Int, desc: String) = opcode match {
	  case Opcodes.NEW => {
	    addCodeLine("ref:= new "+Util.getPilarName(desc))
	    varStack.push(new StackVar(Util.convertType(desc), "ref"))
	  }
	  case _ => {/*Oh God! how can this happen?*/}
	}
	
	override def visitVarInsn(opcode: Int, vr: Int) = opcode match {
	  case Opcodes.ILOAD | Opcodes.LLOAD | Opcodes.FLOAD | Opcodes.DLOAD | 
	  	Opcodes.ALOAD => {varStack.push(new StackVar("", Util.getVarName(vr)))}
	  case Opcodes.ISTORE | Opcodes.LSTORE | Opcodes.FSTORE | Opcodes.DSTORE |
	  	Opcodes.ASTORE => {addCodeLine(Util.getVarName(vr)+":= "+varStack.pop.value)}
	  case Opcodes.RET => {addCodeLine("return "+Util.getVarName(vr))}
	  case _ => {/*Oh God! how can this happen?*/}
	}
	
}