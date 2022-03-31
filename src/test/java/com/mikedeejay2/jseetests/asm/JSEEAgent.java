package com.mikedeejay2.jseetests.asm;

import com.mikedeejay2.jsee.asm.ByteUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.*;
import java.security.ProtectionDomain;

public class JSEEAgent implements ClassFileTransformer {
    private static Instrumentation instrumentation = null;
    private static JSEEAgent transformer;

    public static void agentmain(String s, Instrumentation i) {
        System.out.println("Agent loaded!");

        // Initialization code
        transformer = new JSEEAgent();
        instrumentation = i;
        instrumentation.addTransformer(transformer);

        // To instrument, first revert all added bytecode
        // Call retransformClasses() on all modifiable methods
        try {
            instrumentation.redefineClasses(new ClassDefinition(ASMTest.class, ByteUtils.getBytesFromClass(ASMTest.class)));
        } catch(UnmodifiableClassException | ClassNotFoundException | VerifyError e) {
            System.err.println("Failed to redefine class!");
            e.printStackTrace();
        }
    }

    @Override
    public byte[] transform(
        ClassLoader loader, String className, Class<?> classBeingRedefined,
        ProtectionDomain protectionDomain, byte[] classFileBuffer)
        throws IllegalClassFormatException {
        System.out.println(classBeingRedefined.getName());
        if(loader != ClassLoader.getSystemClassLoader()) {
            System.err.printf("%s is not using the system loader, and so cannot be loaded!%n", className);
            return classFileBuffer;
        }
        if(className.startsWith("com/mikedeejay2/jseetests/asm/LateBindAttacher")) {
            System.err.printf("%s is part of profiling classes.%n", className);
            return classFileBuffer;
        }

        byte[] result = classFileBuffer;

        try {
            // Create class reader from buffer
            ClassReader reader = new ClassReader(classFileBuffer);
            // Make writer
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS); // true
            ClassVisitor profiler = new ProfileClassVisitor(writer, className);
            // Add the class adapter as a modifier
            reader.accept(profiler, 0); //true
            result = writer.toByteArray();
            System.out.println("Returning reinstrumented class " + className);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
