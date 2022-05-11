package com.mikedeejay2.jsee.asm;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
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
        // Find the loading ClassLoader, given that JSEE was loaded with a custom classloader
        ClassLoader classLoader = null;
        final String jseeClass = "com.mikedeejay2.jsee.JSEE";
        for(Class<?> clazz : instrumentation.getAllLoadedClasses()) {
            if(clazz.getName().equals(jseeClass)) {
                classLoader = clazz.getClassLoader();
                break;
            }
        }
        if(classLoader == null) {
            System.err.println("Unable to locate ClassLoader associated with JSEE.");
            return;
        }

        // Invoke the agentmain_ method in the original ClassLoader
        try {
            Class<?> clazz = Class.forName(JSEEAgent.class.getName(), true, classLoader);
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodType methodType = MethodType.methodType(void.class, String.class, Instrumentation.class);
            MethodHandle handle = lookup.findStatic(clazz, "agentmain_", methodType);
            handle.invoke(args, instrumentation);
        } catch(Throwable e) {
            System.err.println("Unable to invoke JSEE agentmain internal method");
        }
    }

    public static void agentmain_(String args, Instrumentation instrumentation) {
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
