package com.mikedeejay2.jsee.asm;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.UUID;

/**
 * The default implementation of <code>agentmain()</code> for JSEE agents. This code can be copy-pasted easily to make a
 * custom <code>agentmain()</code> method if needed.
 *
 * @author Mikedeejay2
 * @since 1.0.0
 */
public final class JSEEAgent {
    /**
     * Private constructor. Throws <code>UnsupportedOperationException</code>
     * @since 1.0.0
     */
    private JSEEAgent() {
        throw new UnsupportedOperationException("JSEEAgent cannot be instantiated");
    }

    /**
     * The <code>agentmain</code> method that is called when the agent jar is loaded. The implementation of the method
     * first gets its associated {@link AgentInfo} from the {@link LateBindAttacher} class. Using the information in the
     * info, it adds all class transformers and then redefines all specified classes in the agent info.
     *
     * @param args The arguments passed to this agent, by default, it's the UUID String of the {@link AgentInfo} associated with
     *             this agent
     * @param instrumentation The {@link Instrumentation} to redefine classes
     * @since 1.0.0
     */
    public static void agentmain(String args, Instrumentation instrumentation) {
        // Obtain AgentInfo
        UUID uuid = UUID.fromString(args.split(" ")[0]);
        AgentInfo info = LateBindAttacher.INFO_MAP.remove(uuid);
        if(info == null) {
            System.err.println("AgentInfo not found for " + uuid);
            return;
        }

        // For all of the transformers in AgentInfo, add that to the instrumentation
        for(ClassFileTransformer transformer : info.getTransformers()) {
            instrumentation.addTransformer(transformer);
        }

        // Redefine all classes specified in AgentInfo
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
