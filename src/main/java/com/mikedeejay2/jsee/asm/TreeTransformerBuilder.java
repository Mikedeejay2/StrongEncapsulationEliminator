package com.mikedeejay2.jsee.asm;

import com.mikedeejay2.jsee.JSEE;
import com.mikedeejay2.jsee.asm.enhanced.JSEEClassNode;
import org.objectweb.asm.Type;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A helper class for creating a {@link ClassFileTransformer} with little to no boilerplate code. This class can then
 * be passed to {@link AgentInfo#AgentInfo(ClassFileTransformer)} or
 * {@link AgentInfo#addTransformers(ClassFileTransformer...)} to then be used in {@link JSEE#attachAgent(AgentInfo)} to
 * easily attach an ASM agent to the JVM.
 *
 * @author Mikedeejay2
 * @since 1.0.0
 */
public class TreeTransformerBuilder implements ClassFileTransformer {
    /**
     * The map of class names to <code>ClassExecutors</code>
     *
     * @since 1.0.0
     */
    protected final Map<String, ClassExecutor> classExecutors;

    /**
     * Construct a new {@link TreeTransformerBuilder}. See class Javadoc for usage.
     *
     * @since 1.0.0
     */
    public TreeTransformerBuilder() {
        this.classExecutors = new HashMap<>();
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        try {
            if(classExecutors.containsKey(className)) {
                JSEEClassNode node = new JSEEClassNode(classfileBuffer);
                ClassExecutor executor = classExecutors.get(className);
                Objects.requireNonNull(executor, "Found null ClassExecutor in TransformerBuilder");
                executor.execute(node);
                return node.toByteArray();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return classfileBuffer;
    }

    /**
     * Add a class executor to be executed upon a specified class name.
     *
     * @param className The name of the class that the executor will execute on
     * @param consumer  The consumer to be used for execution
     * @return The <code>TreeTransformerBuilder</code>
     * @since 1.0.0
     */
    public TreeTransformerBuilder addExecutor(String className, Consumer<JSEEClassNode> consumer) {
        Objects.requireNonNull(className, "Passed null Class name to TransformerBuilder");
        Objects.requireNonNull(consumer, "Passed null consumer to TransformerBuilder");
        classExecutors.put(className, new ClassExecutor(consumer));
        return this;
    }

    /**
     * Add a class executor to be executed upon a specified class name.
     *
     * @param clazz The class that the executor will execute on
     * @param consumer  The consumer to be used for execution
     * @return The <code>TreeTransformerBuilder</code>
     * @since 1.0.0
     */
    public TreeTransformerBuilder addExecutor(Class<?> clazz, Consumer<JSEEClassNode> consumer) {
        Objects.requireNonNull(clazz, "Passed null Class to TransformerBuilder");
        return this.addExecutor(Type.getInternalName(clazz), consumer);
    }

    /**
     * The internal class to hold data for execution upon classes for bytecode modification using the ASM tree API
     *
     * @author Mikedeejay2
     * @since 1.0.0
     */
    protected static class ClassExecutor {
        /**
         * Whether this executor has executed or not
         *
         * @since 1.0.0
         */
        protected boolean executed;

        /**
         * The <code>Consumer</code> used to execute the bytecode modification
         *
         * @since 1.0.0
         */
        private final Consumer<JSEEClassNode> consumer;

        /**
         * Construct a new <code>ClassExecutor</code>
         *
         * @param consumer The <code>Consumer</code> to use for execution
         * @since 1.0.0
         */
        public ClassExecutor(Consumer<JSEEClassNode> consumer) {
            this.executed = false;
            this.consumer = consumer;
        }

        /**
         * Execute upon a given {@link JSEEClassNode}
         *
         * @param node The node to execute upon
         * @since 1.0.0
         */
        public void execute(JSEEClassNode node) {
            if(executed) return;
            consumer.accept(node);
            executed = true;
        }
    }
}
