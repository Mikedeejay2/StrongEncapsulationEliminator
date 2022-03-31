package com.mikedeejay2.jseetests.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ProfileMethodVisitor extends MethodVisitor {
    private final String _className;
    private final String _methodName;

    public ProfileMethodVisitor(MethodVisitor visitor, String className, String methodName) {
        super(Opcodes.ASM9, visitor);

        _className = className;
        _methodName = methodName;
        System.out.printf("Profiled %s in class %s.%n", methodName, className);
    }

    @Override
    public void visitCode() {
        this.visitLdcInsn(_className);
        this.visitLdcInsn(_methodName);
        this.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "com/mikedeejay2/jseetests/asm/TestProfile",
            "start",
            "(Ljava/lang/String;Ljava/lang/String;)V",
            false);
        super.visitCode();
    }

    @Override
    public void visitInsn(int opcode) {
        switch(opcode) {
            case Opcodes.ARETURN:
            case Opcodes.DRETURN:
            case Opcodes.FRETURN:
            case Opcodes.IRETURN:
            case Opcodes.LRETURN:
            case Opcodes.RETURN:
            case Opcodes.ATHROW:
                this.visitLdcInsn(_className);
                this.visitLdcInsn(_methodName);
                this.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    "com/mikedeejay2/jseetests/asm/TestProfile",
                    "end",
                    "(Ljava/lang/String;Ljava/lang/String;)V",
                    false
                    );
                break;
            default:
                break;
        }
        super.visitInsn(opcode);
    }
}
