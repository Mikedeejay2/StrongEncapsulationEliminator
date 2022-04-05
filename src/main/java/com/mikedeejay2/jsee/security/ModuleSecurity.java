package com.mikedeejay2.jsee.security;

import com.mikedeejay2.jsee.asm.AgentInfo;
import com.mikedeejay2.jsee.asm.LateBindAttacher;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.lang.instrument.*;
import java.security.ProtectionDomain;

public final class ModuleSecurity {
    private static boolean transformed = false;

    public static void toggleSecurity() {
        LateBindAttacher.attach(
            new AgentInfo()
                .addTransformers(new ModuleTransformer())
                .addClassesToRedefine(Module.class)
                .addAgentClasses(ModuleTransformer.class));
    }

    private static class ModuleTransformer implements ClassFileTransformer {
        private boolean executed = false;

        @Override
        public byte[] transform(
            ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classFileBuffer) {
            if(!className.equals("java/lang/Module") || executed) return classFileBuffer;

            // Create class reader from buffer
            ClassReader reader = new ClassReader(classFileBuffer);
            // Make writer
            ClassNode classNode = new ClassNode(Opcodes.ASM9);
            reader.accept(classNode, 0);

            for(MethodNode methodNode : classNode.methods) {
                if(!"implIsExportedOrOpen".equals(methodNode.name)) continue;
                InsnList instructions = methodNode.instructions;
                if(transformed) {
                    transformed = false;
                    for(AbstractInsnNode node : instructions) {
                        System.out.print(node.getOpcode() + ", ");
                        AbstractInsnNode next = node.getNext();
//                        if(node.getOpcode() == Opcodes.ICONST_1 &&
//                            next != null && next.getOpcode() == Opcodes.IRETURN) {
//                            instructions.remove(node);
//                            instructions.remove(next);
//                            break;
//                        }
                    }
                } else {
                    transformed = true;

                    for(AbstractInsnNode node : instructions) {
                        System.out.print(node.getOpcode() + ", ");
                        AbstractInsnNode next = node.getNext();
//                        if(node.getOpcode() == Opcodes.ICONST_1 &&
//                            next != null && next.getOpcode() == Opcodes.IRETURN) {
//                            instructions.remove(node);
//                            instructions.remove(next);
//                            break;
//                        }
                    }
                    InsnList list = new InsnList();
                    list.add(new InsnNode(Opcodes.ICONST_1)); // push boolean true onto stack
                    list.add(new InsnNode(Opcodes.IRETURN)); // push return int onto stack (return true boolean)
                    instructions.insert(list); // insert list to start of stack
                }
            }

            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            classNode.accept(writer);
            executed = true;

            return writer.toByteArray();
        }
    }

    public static boolean isTransformed() {
        return transformed;
    }
}
