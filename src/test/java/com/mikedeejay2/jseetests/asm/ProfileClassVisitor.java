package com.mikedeejay2.jseetests.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ProfileClassVisitor extends ClassVisitor {
    private final String className;

    public ProfileClassVisitor(ClassVisitor visitor, String theClass) {
        super(Opcodes.ASM4, visitor);
        this.className = theClass;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

        return new ProfileMethodVisitor(mv, className, name);
    }
}
