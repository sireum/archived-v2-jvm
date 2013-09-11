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

class BytecodeMethodVisitor(api:Int, mv:MethodVisitor, procedure: Procedure) extends MethodVisitor(api, mv) {
	def this(proc: Procedure) = this(Opcodes.ASM4, null, proc)
	
	val labelMap = Map[Label, Int]()
	val varStack = Stack[StackVar]()
	
	
	override def visitAnnotation(name: String, visible:Boolean) = {
	  val bav = new BytecodeAnnotationVisitor(procedure)
	  bav
	}
	
	override def visitCode() = {
	  procedure.code.add("local temp;")
	}
	
	override def visitEnd() = {
	  
	}
	
	override def visitFieldInsn(opcode: Int, owner: String, name: String, desc: String) = opcode match {
	  case Opcodes.GETSTATIC => {
	    varStack.push(new StackVar(Util.convertType(Type.getType(desc)), Util.getPilarName(owner+"."+name)))
	  } 
	  case Opcodes.GETFIELD => {
	    varStack.push(new StackVar(Util.convertType(Type.getType(desc)), Util.getPilarName(owner+"."+name)))
	  }
	  case Opcodes.PUTSTATIC => {}
	  case Opcodes.PUTFIELD => {}
	  case _ => {}
	}
	
	override def visitFrame(typ: Int, nLocal: Int, local: Array[Object], nStack: Int, stack: Array[Object]) = {
	  
	}
	
	override def visitIincInsn(vr: Int, increment: Int) = {
	  
	}
	
	override def visitInsn(opcode: Int) = opcode match {
	  case Opcodes.NOP => {}
	  case Opcodes.ACONST_NULL => {}
	  case Opcodes.ICONST_M1 => {}
	  case Opcodes.ICONST_0 => {}
	  case Opcodes.ICONST_1 => {}
	  case Opcodes.ICONST_2 => {}
	  case Opcodes.ICONST_3 => {}
	  case Opcodes.ICONST_4 => {}
	  case Opcodes.ICONST_5 => {}
	  case Opcodes.LCONST_0 => {}
	  case Opcodes.LCONST_1 => {}
	  case Opcodes.FCONST_0 => {}
	  case Opcodes.FCONST_1 => {}
	  case Opcodes.FCONST_2 => {}
	  case Opcodes.DCONST_0 => {}
	  case Opcodes.DCONST_1 => {}
	  case Opcodes.IALOAD => {}
	  case Opcodes.LALOAD => {}
	  case Opcodes.FALOAD => {}
	  case Opcodes.DALOAD => {}
	  case Opcodes.AALOAD => {}
	  case Opcodes.BALOAD => {}
	  case Opcodes.CALOAD => {}
	  case Opcodes.SALOAD => {}
	  case Opcodes.IASTORE => {}
	  case Opcodes.LASTORE => {}
	  case Opcodes.FASTORE => {}
	  case Opcodes.DASTORE => {}
	  case Opcodes.AASTORE => {}
	  case Opcodes.BASTORE => {}
	  case Opcodes.CASTORE => {}
	  case Opcodes.SASTORE => {}
	  case Opcodes.POP => {}
	  case Opcodes.POP2 => {}
	  case Opcodes.DUP => {}
	  case Opcodes.DUP_X1 => {}
	  case Opcodes.DUP_X2 => {}
	  case Opcodes.DUP2 => {}
	  case Opcodes.DUP2_X1 => {}
	  case Opcodes.DUP2_X2 => {}
	  case Opcodes.SWAP => {}
	  case Opcodes.IADD => {}
	  case Opcodes.LADD => {}
	  case Opcodes.FADD => {}
	  case Opcodes.DADD => {}
	  case Opcodes.ISUB => {}
	  case Opcodes.LSUB => {}
	  case Opcodes.FSUB => {}
	  case Opcodes.DSUB => {} 
	  case Opcodes.IMUL => {}
	  case Opcodes.LMUL => {}
	  case Opcodes.FMUL => {}
	  case Opcodes.DMUL => {}
	  case Opcodes.IDIV => {}
	  case Opcodes.LDIV => {}
	  case Opcodes.FDIV => {}
	  case Opcodes.DDIV => {}
	  case Opcodes.IREM => {} 
	  case Opcodes.LREM => {}
	  case Opcodes.FREM => {}
	  case Opcodes.DREM => {}
	  case Opcodes.INEG => {}
	  case Opcodes.LNEG => {}
	  case Opcodes.FNEG => {}
	  case Opcodes.DNEG => {}
	  case Opcodes.ISHL => {}
	  case Opcodes.LSHL => {}
	  case Opcodes.ISHR => {}
	  case Opcodes.LSHR => {}
	  case Opcodes.IUSHR => {}
	  case Opcodes.LUSHR => {}
	  case Opcodes.IAND => {}
	  case Opcodes.LAND => {}
	  case Opcodes.IOR => {}
	  case Opcodes.LOR => {}
	  case Opcodes.IXOR => {}
	  case Opcodes.LXOR => {}
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
	  case Opcodes.IRETURN => {}
	  case Opcodes.LRETURN => {}
	  case Opcodes.FRETURN => {}
	  case Opcodes.DRETURN => {}
	  case Opcodes.ARETURN => {}
	  case Opcodes.RETURN => {}
	  case Opcodes.ARRAYLENGTH => {}
	  case Opcodes.ATHROW => {}
	  case Opcodes.MONITORENTER => {}
	  case Opcodes.MONITOREXIT => {}
	  case _ => {}
	}
	
	override def visitIntInsn(opcode: Int, operand: Int) = opcode match {
	  case Opcodes.BIPUSH => {}
	  case Opcodes.SIPUSH => {}
	  case Opcodes.NEWARRAY => {}
	  case _ => {}
	}
	
	override def visitJumpInsn(opcode: Int, label: Label) = opcode match {
	  case Opcodes.IFEQ => {}
	  case Opcodes.IFNE => {}
	  case Opcodes.IFLT => {}
	  case Opcodes.IFGE => {}
	  case Opcodes.IFGT => {}
	  case Opcodes.IFLE => {}
	  case Opcodes.IF_ICMPEQ => {}
	  case Opcodes.IF_ICMPNE => {}
	  case Opcodes.IF_ICMPLT => {}
	  case Opcodes.IF_ICMPGE => {}
	  case Opcodes.IF_ICMPGT => {}
	  case Opcodes.IF_ACMPEQ => {}
	  case Opcodes.IF_ACMPNE => {}
	  case Opcodes.GOTO => {}
	  case Opcodes.JSR => {}
	  case Opcodes.IFNULL => {}
	  case Opcodes.IFNONNULL => {}
	  case _ => {}
	}
	
	override def visitLabel(label: Label) = {}
	
	
	override def visitLdcInsn(cst: Object) = {
	  if (cst.isInstanceOf[String]) {
	    varStack.push(new StackVar("", "\""+cst.toString()+"\""))
	  } else {
	    varStack.push(new StackVar("", cst.toString()))
	  }
	}
	
	override def visitLineNumber(line:Int, start:Label) = {
	  
	}
	
	override def visitLocalVariable(name: String, desc: String, signature: String, start: Label, end: Label, index: Int) = {
	  
	}
	
	override def visitLookupSwitchInsn(dflt: Label, keys: Array[Int], labels: Array[Label]) = {
	  
	}
	
	override def visitMaxs(maxStack: Int, maxLocals: Int) = {
	  
	}
	
	override def visitMethodInsn(opcode: Int, owner: String, name: String, desc: String) = opcode match {
	  case Opcodes.INVOKEVIRTUAL => {
	    var args = List[String]()
	    0 to Type.getArgumentTypes(desc).length foreach (_ => {args = args :+ varStack.pop.value})
	    
	    procedure.code.add("call temp:= "+Util.getFunctionCall(owner, name, desc, "virtual", args))
	    if(!desc.endsWith("V")) {
	      varStack.push(new StackVar(Util.convertType(Type.getReturnType(desc)), "temp"))
	    }
	  }
	  case Opcodes.INVOKESPECIAL => {
	    var args = List[String]()
	    0 to Type.getArgumentTypes(desc).length foreach (_ => {args = args :+ varStack.pop.value})
	    
	    procedure.code.add("call temp:= "+Util.getFunctionCall(owner, name, desc, "direct", args))
	    if(!desc.endsWith("V")) {
	      varStack.push(new StackVar(Util.convertType(Type.getReturnType(desc)), "temp"))
	    }
	  }
	  case Opcodes.INVOKESTATIC => {}
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
	
	override def visitVarInsn(opcode: Int, vr: Int) = opcode match {
	  case Opcodes.ILOAD | Opcodes.LLOAD | Opcodes.FLOAD | Opcodes.DLOAD | 
	  	Opcodes.ALOAD => {varStack.push(new StackVar("", Util.getVarName(vr)))}
	  case Opcodes.ISTORE | Opcodes.LSTORE | Opcodes.FSTORE | Opcodes.DSTORE |
	  	Opcodes.ASTORE => {procedure.code.add(Util.getVarName(vr)+":="+varStack.pop.value)}
	  case Opcodes.RET => {}
	  case _ => {}
	}
	
}