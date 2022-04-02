package com.mikedeejay2.jsee.security;

import com.mikedeejay2.jsee.asm.AgentInfo;
import com.mikedeejay2.jsee.asm.ByteUtils;
import com.mikedeejay2.jsee.asm.LateBindAttacher;
import org.objectweb.asm.*;

import java.lang.instrument.*;
import java.security.ProtectionDomain;

public final class ModuleSecurity {
    private static boolean transformed = false;

    public static void attachManipulator() {
        LateBindAttacher.attach(
            new AgentInfo()
                .addTransformers(new ModuleAgent())
                .addClassesToRedefine(Module.class)
                .addAgentClasses(ModuleAgent.class));
    }

    private static class ModuleAgent implements ClassFileTransformer {

        public static void agentmain(String args, Instrumentation instrumentation) {
            instrumentation.addTransformer(new ModuleAgent());

            Class<?> toRedefine = Module.class;
            try {
                instrumentation.redefineClasses(new ClassDefinition(toRedefine, ByteUtils.getBytesFromClass(toRedefine)));
            } catch(UnmodifiableClassException | ClassNotFoundException | VerifyError e) {
                System.err.printf("Failed redefine for class %s%n", toRedefine.getName());
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
                ClassVisitor visitor = writer;
                visitor = new OpenClassVisitor(visitor, className);

                // Add the class adapter as a modifier
                reader.accept(visitor, 0);
                result = writer.toByteArray();
            } catch(Exception e) {
                e.printStackTrace();
            }
            return result;
        }


    }

    private static class OpenClassVisitor extends ClassVisitor {
        private final String clazz;

        public OpenClassVisitor(ClassVisitor visitor, String theClass) {
            super(Opcodes.ASM9, visitor);
            this.clazz = theClass;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if(!name.equals("implIsExportedOrOpen")) {
                return super.visitMethod(access, name, desc, signature, exceptions);
            }

            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

            return new MethodVisitor(Opcodes.ASM9, mv) {
                @Override
                public void visitCode() {
                    this.visitInsn(Opcodes.ICONST_1);
                    this.visitInsn(Opcodes.IRETURN);
                    super.visitCode();
                    ModuleSecurity.transformed = true;
                }
            };
        }
    }
}
