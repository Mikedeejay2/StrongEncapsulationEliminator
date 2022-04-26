package com.mikedeejay2.jsee.asm.enhanced;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

public class JSEEMethodNode extends MethodNode {
    public JSEEMethodNode(int api, int access, String name, String descriptor, String signature, String[] exceptions) {
        super(api, access, name, descriptor, signature, exceptions);
    }

    public JSEEMethodNode(int api, ClassNode classNode, MethodNode methodNode) {
        this(api, methodNode.access, methodNode.name, methodNode.desc, methodNode.signature, methodNode.exceptions.toArray(new String[0]));
        List<MethodNode> methods = classNode.methods;
        if(!methods.contains(methodNode)) {
            throw new RuntimeException(String.format(
                "MethodNode %s%s could not be found in ClassNode %s",
                methodNode.name, methodNode.signature, classNode.name));
        }
        methods.set(methods.indexOf(methodNode), this);
    }
}
