package com.mikedeejay2.jsee.asm;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.UUID;

public final class JSEEAgent {
    private JSEEAgent() {
        throw new UnsupportedOperationException("JSEEAgent cannot be instantiated");
    }

    public static void agentmain(String args, Instrumentation instrumentation) {
        UUID uuid = UUID.fromString(args.split(" ")[0]);
        AgentInfo info = LateBindAttacher.INFO_MAP.remove(uuid);
        if(info == null) {
            System.err.println("AgentInfo not found for " + uuid);
            return;
        }
        for(ClassFileTransformer transformer : info.getTransformers()) {
            instrumentation.addTransformer(transformer);
        }

        for(Class<?> toRedefine : info.getClassesToRedefine()) {
            try {
                instrumentation.redefineClasses(new ClassDefinition(toRedefine, ByteUtils.getBytesFromClass(toRedefine)));
            } catch(UnmodifiableClassException | ClassNotFoundException | VerifyError e) {
                System.err.printf("Failed redefine for class %s%n", toRedefine.getName());
                e.printStackTrace();
            }
        }
    }
}
