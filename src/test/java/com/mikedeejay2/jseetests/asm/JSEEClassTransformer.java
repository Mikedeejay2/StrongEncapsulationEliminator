package com.mikedeejay2.jseetests.asm;

import com.mikedeejay2.jsee.asm.ASMUtil;

import java.lang.instrument.*;
import java.security.ProtectionDomain;

public class JSEEClassTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(
        ClassLoader loader, String className, Class<?> classBeingRedefined,
        ProtectionDomain protectionDomain, byte[] classFileBuffer)
        throws IllegalClassFormatException {
        System.out.println(classBeingRedefined.getName());
        if(loader != ClassLoader.getSystemClassLoader()) {
            System.err.printf("%s is not using the system loader, and so cannot be loaded.%n", className);
            return classFileBuffer;
        }
        if(className.startsWith("com/mikedeejay2/jseetests/asm/LateBindAttacher")) {
            System.err.printf("%s is part of profiling classes.%n", className);
            return classFileBuffer;
        }

        return ASMUtil.operateVisitor(classFileBuffer, writer -> new ProfileClassVisitor(writer, className));
    }
}
