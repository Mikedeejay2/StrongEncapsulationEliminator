package com.mikedeejay2.jsee.security;

import com.mikedeejay2.jsee.asm.AgentInfo;
import com.mikedeejay2.jsee.asm.LateBindAttacher;
import org.objectweb.asm.*;

import java.lang.instrument.*;
import java.security.ProtectionDomain;

public final class ModuleSecurity {
    private static boolean transformed = false;

    public static void attachManipulator() {
        LateBindAttacher.attach(
            new AgentInfo()
                .addTransformers(new ModuleTransformer())
                .addClassesToRedefine(Module.class)
                .addAgentClasses(ModuleTransformer.class));
    }

    private static class ModuleTransformer implements ClassFileTransformer {
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
