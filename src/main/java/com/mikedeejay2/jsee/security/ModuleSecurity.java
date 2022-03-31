package com.mikedeejay2.jsee.security;

import com.mikedeejay2.jsee.asm.ByteUtils;
import com.mikedeejay2.jsee.asm.LateBindAttacher;
import org.objectweb.asm.*;

import java.lang.instrument.*;
import java.security.ProtectionDomain;

public final class ModuleSecurity {
    private static boolean bypass = true;

    public static void attachManipulator() {
        LateBindAttacher.attach(
            ModuleAgent.class,
            ModuleAgent.class,
            LateBindAttacher.class,
            ModuleClassVisitor.class,
            ModuleMethodVisitor.class);
    }

    public boolean shouldBypass() {
        if(bypass) {
            return true;
        }
        return false;
    }

    private static class ModuleAgent implements ClassFileTransformer {
        private static Instrumentation instrumentation = null;
        private static ModuleAgent transformer;

        public static void agentmain(String s, Instrumentation i) {
            transformer = new ModuleAgent();
            instrumentation = i;
            instrumentation.addTransformer(transformer);

            try {
                instrumentation.redefineClasses(new ClassDefinition(Module.class, ByteUtils.getBytesFromClass(Module.class)));
            } catch(UnmodifiableClassException | ClassNotFoundException | VerifyError e) {
                System.err.println("Failed to redefine class!");
                e.printStackTrace();
            }
        }

        @Override
        public byte[] transform(
            ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classFileBuffer) {

            byte[] result = classFileBuffer;

            try {
                // Create class reader from buffer
                ClassReader reader = new ClassReader(classFileBuffer);
                // Make writer
                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                ClassVisitor visitor = new ModuleClassVisitor(writer, className);
                // Add the class adapter as a modifier
                reader.accept(visitor, 0); //true
                result = writer.toByteArray();
            } catch(Exception e) {
                e.printStackTrace();
            }
            return result;
        }


    }

    private static class ModuleClassVisitor extends ClassVisitor {
        private final String className;

        public ModuleClassVisitor(ClassVisitor visitor, String theClass) {
            super(Opcodes.ASM9, visitor);
            this.className = theClass;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

            return new ModuleMethodVisitor(mv, className, name);
        }
    }

    private static class ModuleMethodVisitor extends MethodVisitor {
        private final String _className;
        private final String _methodName;

        public ModuleMethodVisitor(MethodVisitor visitor, String className, String methodName) {
            super(Opcodes.ASM9, visitor);

            _className = className;
            _methodName = methodName;
        }

        @Override
        public void visitCode() {
            this.visitLdcInsn(_className);
            this.visitLdcInsn(_methodName);
            this.visitInsn(Opcodes.ICONST_1);
            this.visitInsn(Opcodes.IRETURN);
//            this.visitMethodInsn(
//                Opcodes.INVOKESTATIC,
//                "com/mikedeejay2/jsee/security/ModuleSecurity",
//                "start",
//                "(Ljava/lang/String;Ljava/lang/String;)V",
//                false);
            super.visitCode();
        }
    }
}
