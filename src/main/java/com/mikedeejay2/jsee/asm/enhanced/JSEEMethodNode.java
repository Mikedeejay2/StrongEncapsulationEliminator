package com.mikedeejay2.jsee.asm.enhanced;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

/**
 * @author Mikedeejay2
 * @since 1.0.0
 */
public class JSEEMethodNode extends MethodNode {
    public JSEEMethodNode() {
        this(Opcodes.ASM9);
    }

    public JSEEMethodNode(int api) {
        super(api);
    }

    public JSEEMethodNode(int api, int access, String name, String descriptor, String signature, String[] exceptions) {
        super(api, access, name, descriptor, signature, exceptions);
    }

    public JSEEMethodNode(int access, String name, String descriptor, String signature, String[] exceptions) {
        this(Opcodes.ASM9, access, name, descriptor, signature, exceptions);
    }
}
