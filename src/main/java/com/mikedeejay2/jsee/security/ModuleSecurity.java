package com.mikedeejay2.jsee.security;

import com.mikedeejay2.jsee.asm.ASMUtil;
import com.mikedeejay2.jsee.asm.AgentInfo;
import com.mikedeejay2.jsee.asm.LateBindAttacher;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Allows for the toggling of Java 9's module system security measures. This allows for reflection upon internal Java
 * classes and other entries that are generally restricted.
 * <p>
 * <o>Th</o>
 *
 * @since 1.0.0
 * @author Mikedeejay2
 */
public final class ModuleSecurity {
    /**
     * Private constructor. Throws <code>UnsupportedOperationException</code>
     */
    private ModuleSecurity() {
        throw new UnsupportedOperationException("ModuleSecurity cannot be instantiated");
    }

    /**
     * <code>AtomicBoolean</code> which holds whether the {@link Module} class has been transformed to disable security
     */
    private static final AtomicBoolean transformed = new AtomicBoolean(false);

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
            if(transformed.compareAndSet(true, false)) {
                return classFileBuffer; // return original bytes to remove added instructions
            }

            return ASMUtil.operateNode(classFileBuffer, classNode -> {
                MethodNode methodNode = ASMUtil.getMethodNode(classNode, "implIsExportedOrOpen");
                InsnList instructions = methodNode.instructions;
                transformed.compareAndSet(false, true);
                InsnList list = new InsnList();
                list.add(new InsnNode(Opcodes.ICONST_1)); // push boolean true onto stack
                list.add(new InsnNode(Opcodes.IRETURN)); // push return int onto stack (return true boolean)
                instructions.insert(list); // insert list to start of stack
                executed = true;
            });
        }
    }

    public static boolean isTransformed() {
        return transformed.get();
    }
}
