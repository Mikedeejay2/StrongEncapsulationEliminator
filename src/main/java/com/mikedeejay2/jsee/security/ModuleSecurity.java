package com.mikedeejay2.jsee.security;

import com.mikedeejay2.jsee.asm.AgentInfo;
import com.mikedeejay2.jsee.asm.LateBindAttacher;
import com.mikedeejay2.jsee.asm.TreeTransformerBuilder;
import com.mikedeejay2.jsee.asm.enhanced.JSEEClassNode;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Allows for the toggling of Java 9's module system security measures. This allows for reflection upon internal Java
 * classes and other entries that are generally restricted.
 * <p>
 * <strong>This class only functions with Java 9 or higher! Java 8 does not have a reflection security system.</strong>
 *
 * @author Mikedeejay2
 * @since 1.0.0
 */
public final class ModuleSecurity {
    /**
     * Private constructor. Throws <code>UnsupportedOperationException</code>
     *
     * @since 1.0.0
     */
    private ModuleSecurity() {
        throw new UnsupportedOperationException("ModuleSecurity cannot be instantiated");
    }

    /**
     * <code>AtomicBoolean</code> which holds whether the {@link Module} class has been transformed to disable security
     *
     * @since 1.0.0
     */
    private static final AtomicBoolean transformed = new AtomicBoolean(false);

    /**
     * Toggle the security function of modules. Note that this method creates and attaches a new agent to transform
     * the JVM, so its execution time should be heavily considered.
     *
     * @since 1.0.0
     */
    public static void toggleSecurity() {
        checkModulesThrowElse();
        ClassFileTransformer transformer = new TreeTransformerBuilder()
            .addExecutor(Module.class, ModuleSecurity::transformModule);
        LateBindAttacher.attach(
            new AgentInfo(transformer)
                .addClassesToRedefine(Module.class));
    }

    private static void transformModule(JSEEClassNode classNode) {
        MethodNode methodNode = classNode.getMethodNode("implIsExportedOrOpen");
        InsnList instructions = methodNode.instructions;
        transformed.compareAndSet(false, true);
        InsnList list = new InsnList();
        list.add(new InsnNode(Opcodes.ICONST_1)); // push boolean true onto stack
        list.add(new InsnNode(Opcodes.IRETURN)); // push return int onto stack (return true boolean)
        instructions.insert(list); // insert list to start of stack
    }

    /**
     * {@link ClassFileTransformer} that transforms {@link Module} <code>implIsExportedOrOpen</code> method to either
     * always return true if the class has not yet been transformed or to transform the class back to the original bytes
     * if the class has been transformed.
     *
     * @author Mikedeejay2
     * @since 1.0.0
     */
    private static class ModuleTransformer implements ClassFileTransformer {
        /**
         * Whether this transformer has executed yet.
         */
        private boolean executed = false;

        @Override
        public byte[] transform(
            ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classFileBuffer) {
            if(!className.equals("java/lang/Module") || executed) return classFileBuffer;
            if(transformed.compareAndSet(true, false)) {
                return classFileBuffer; // return original bytes to remove added instructions
            }

            JSEEClassNode classNode = new JSEEClassNode(classFileBuffer);
            MethodNode methodNode = classNode.getMethodNode("implIsExportedOrOpen");
            InsnList instructions = methodNode.instructions;
            transformed.compareAndSet(false, true);
            InsnList list = new InsnList();
            list.add(new InsnNode(Opcodes.ICONST_1)); // push boolean true onto stack
            list.add(new InsnNode(Opcodes.IRETURN)); // push return int onto stack (return true boolean)
            instructions.insert(list); // insert list to start of stack
            executed = true;
            return classNode.toByteArray();
        }
    }

    /**
     * Get whether the {@link Module} class has been transformed to disable security or not.
     *
     * @return The transformed state, true if no module security, false if module security
     * @since 1.0.0
     */
    public static boolean isTransformed() {
        return transformed.get();
    }

    public static boolean hasModules() {
        try {
            Class<?> clazz = Class.forName("java.lang.Module");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    private static void checkModulesThrowElse() {
        if(!hasModules()) {
            throw new UnsupportedOperationException("Attempted to disable security of Java modules but JVM does not have modules.");
        }
    }
}
