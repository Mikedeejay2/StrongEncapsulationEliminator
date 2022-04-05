package com.mikedeejay2.jsee.security;

import com.mikedeejay2.jsee.asm.ASMUtil;
import com.mikedeejay2.jsee.asm.AgentInfo;
import com.mikedeejay2.jsee.asm.LateBindAttacher;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public final class ModuleSecurity {
    private static boolean transformed = false;

    public static void toggleSecurity() {
        LateBindAttacher.attach(
            new AgentInfo(new ModuleTransformer())
                .addClassesToRedefine(Module.class));
    }

    private static class ModuleTransformer implements ClassFileTransformer {
        private boolean executed = false;

        @Override
        public byte[] transform(
            ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classFileBuffer) {
            if(!className.equals("java/lang/Module") || executed) return classFileBuffer;
            if(transformed) return classFileBuffer;

            return ASMUtil.operateNode(classFileBuffer, classNode -> {
                for(MethodNode methodNode : classNode.methods) {
                    if(!"implIsExportedOrOpen".equals(methodNode.name)) continue;
                    InsnList instructions = methodNode.instructions;
                    transformed = true;
                    InsnList list = new InsnList();
                    list.add(new InsnNode(Opcodes.ICONST_1)); // push boolean true onto stack
                    list.add(new InsnNode(Opcodes.IRETURN)); // push return int onto stack (return true boolean)
                    instructions.insert(list); // insert list to start of stack
                }
                executed = true;
            });
        }
    }

    public static boolean isTransformed() {
        return transformed;
    }
}
