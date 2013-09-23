package org.sireum.jvm.samples;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;

public class Outerclass implements Opcodes {

    public static byte[] dump1() {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        FieldVisitor fv;
        MethodVisitor mv;

        cw.visit(V1_4, ACC_SUPER, "pkg/Outer$1", null, "pkg/Outer", null);

        cw.visitOuterClass("pkg/Outer", "m", "()V");
        cw.visitInnerClass("pkg/Outer$1", null, null, 0);

        fv = cw.visitField(ACC_FINAL + ACC_SYNTHETIC,
                "this$0",
                "Lpkg/Outer;",
                null,
                null);
        fv.visitEnd();

        mv = cw.visitMethod(0, "<init>", "(Lpkg/Outer;)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitFieldInsn(PUTFIELD, "pkg/Outer$1", "this$0", "Lpkg/Outer;");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "pkg/Outer", "<init>", "()V");
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        mv = cw.visitMethod(ACC_PUBLIC,
                "toString",
                "()Ljava/lang/String;",
                null,
                null);
        mv.visitCode();
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL,
                "java/lang/StringBuilder",
                "<init>",
                "()V");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, "pkg/Outer$1", "this$0", "Lpkg/Outer;");
        mv.visitMethodInsn(INVOKEVIRTUAL,
                "java/lang/StringBuilder",
                "append",
                "(Ljava/lang/Object;)Ljava/lang/StringBuilder;");
        mv.visitLdcInsn(" ");
        mv.visitMethodInsn(INVOKEVIRTUAL,
                "java/lang/StringBuilder",
                "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, "pkg/Outer$1", "this$0", "Lpkg/Outer;");
        mv.visitMethodInsn(INVOKESTATIC,
                "pkg/Outer",
                "access$000",
                "(Lpkg/Outer;)I");
        mv.visitMethodInsn(INVOKEVIRTUAL,
                "java/lang/StringBuilder",
                "append",
                "(I)Ljava/lang/StringBuilder;");
        mv.visitMethodInsn(INVOKEVIRTUAL,
                "java/lang/StringBuilder",
                "toString",
                "()Ljava/lang/String;");
        mv.visitInsn(ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        cw.visitEnd();

        return cw.toByteArray();
    }
    
    public static void main(String[] args) {
    	ClassReader cr = new ClassReader(dump1());
    	
    	TraceClassVisitor tcv = new TraceClassVisitor(new java.io.PrintWriter(System.out));
    	cr.accept(tcv, 0);
    	
    	
    }
    
}
