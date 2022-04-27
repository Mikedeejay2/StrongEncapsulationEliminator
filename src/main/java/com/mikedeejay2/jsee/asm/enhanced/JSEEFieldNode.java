package com.mikedeejay2.jsee.asm.enhanced;

import org.objectweb.asm.tree.FieldNode;

public class JSEEFieldNode extends FieldNode {
    public JSEEFieldNode(int access, String name, String descriptor, String signature, Object value) {
        super(access, name, descriptor, signature, value);
    }

    public JSEEFieldNode(int api, int access, String name, String descriptor, String signature, Object value) {
        super(api, access, name, descriptor, signature, value);
    }
}
