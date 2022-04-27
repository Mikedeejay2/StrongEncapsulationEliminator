package com.mikedeejay2.jsee.asm.enhanced;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

public class JSEEMethodNode extends MethodNode {
    public JSEEMethodNode() {
        super();
    }

    public JSEEMethodNode(int api) {
        super(api);
    }

    public JSEEMethodNode(int access, String name, String descriptor, String signature, String[] exceptions) {
        super(access, name, descriptor, signature, exceptions);
    }

    public JSEEMethodNode(int api, int access, String name, String descriptor, String signature, String[] exceptions) {
        super(api, access, name, descriptor, signature, exceptions);
    }
}
