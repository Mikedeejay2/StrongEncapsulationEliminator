package com.mikedeejay2.jsee.asm;

import com.mikedeejay2.jsee.asm.enhanced.JSEEClassNode;
import org.objectweb.asm.Type;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class TreeTransformerBuilder implements ClassFileTransformer {
    protected final Map<String, ClassExecutor> classExecutors;

    public TreeTransformerBuilder() {
        this.classExecutors = new HashMap<>();
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
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

    public TreeTransformerBuilder addExecutor(String className, Consumer<JSEEClassNode> consumer) {
        Objects.requireNonNull(className, "Passed null Class name to TransformerBuilder");
        Objects.requireNonNull(consumer, "Passed null consumer to TransformerBuilder");
        classExecutors.put(className, new ClassExecutor(consumer));
        return this;
    }

    public TreeTransformerBuilder addExecutor(Class<?> clazz, Consumer<JSEEClassNode> consumer) {
        Objects.requireNonNull(clazz, "Passed null Class to TransformerBuilder");
        return this.addExecutor(Type.getInternalName(clazz), consumer);
    }

    protected static class ClassExecutor {
        protected boolean executed;
        private final Consumer<JSEEClassNode> consumer;

        public ClassExecutor(Consumer<JSEEClassNode> consumer) {
            this.executed = false;
            this.consumer = consumer;
        }

        public void execute(JSEEClassNode node) {
            if(executed) return;
            consumer.accept(node);
            executed = true;
        }
    }
}
