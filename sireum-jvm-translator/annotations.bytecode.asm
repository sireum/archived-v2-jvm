package asm.org.sireum.jvm.samples;
import java.util.*;
import org.objectweb.asm.*;
import org.objectweb.asm.attrs.*;
public class AnnotationsDump implements Opcodes {

public static byte[] dump () throws Exception {

ClassWriter cw = new ClassWriter(0);
FieldVisitor fv;
MethodVisitor mv;
AnnotationVisitor av0;

cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER, "org/sireum/jvm/samples/Annotations", null, "java/lang/Object", null);

cw.visitSource("Annotations.java", null);

{
av0 = cw.visitAnnotation("Lcom/google/common/annotations/Beta;", false);
av0.visitEnd();
}
{
av0 = cw.visitAnnotation("Lorg/sireum/jvm/samples/WithValue;", false);
av0.visit("bytes", "akljfla");
av0.visit("arr", new int[] {1,2});
{
AnnotationVisitor av1 = av0.visitArray("nested");
{
AnnotationVisitor av2 = av1.visitAnnotation(null, "Lorg/sireum/jvm/samples/Nested;");
av2.visit("name", "abc");
av2.visitEnd();
}
av1.visitEnd();
}
av0.visitEnd();
}
{
fv = cw.visitField(0, "x", "I", null, null);
fv.visitEnd();
}
{
mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
mv.visitCode();
Label l0 = new Label();
mv.visitLabel(l0);
mv.visitLineNumber(15, l0);
mv.visitVarInsn(ALOAD, 0);
mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
mv.visitInsn(RETURN);
Label l1 = new Label();
mv.visitLabel(l1);
mv.visitLocalVariable("this", "Lorg/sireum/jvm/samples/Annotations;", null, l0, l1, 0);
mv.visitMaxs(1, 1);
mv.visitEnd();
}
cw.visitEnd();

return cw.toByteArray();
}
}
