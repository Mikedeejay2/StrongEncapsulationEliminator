package com.mikedeejay2.jsee.asm.enhanced;

import com.mikedeejay2.jsee.asm.ASMUtil;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class JSEEClassNode extends ClassNode {
    protected ClassReader reader;
    protected ClassWriter writer;

    public JSEEClassNode(int api, byte[] classFileBuffer, int writerOps, int readerOps) {
        super(api);
        this.reader = new ClassReader(classFileBuffer);
        reader.accept(this, readerOps);

        this.writer = new ClassWriter(writerOps);
        this.accept(writer);
    }

    public JSEEClassNode(int api, byte[] classFileBuffer, int writerOps) {
        this(api, classFileBuffer, writerOps, 0);
    }

    public JSEEClassNode(int api, byte[] classFileBuffer) {
        this(api, classFileBuffer, 0, 0);
    }

    public JSEEClassNode(byte[] classFileBuffer, int writerOps, int readerOps) {
        this(Opcodes.ASM9, classFileBuffer, writerOps, readerOps);
    }

    public JSEEClassNode(byte[] classFileBuffer, int writerOps) {
        this(Opcodes.ASM9, classFileBuffer, writerOps, 0);
    }

    public JSEEClassNode(byte[] classFileBuffer) {
        this(Opcodes.ASM9, classFileBuffer, 0, 0);
    }

    @Override
    public FieldVisitor visitField(
        final int access,
        final String name,
        final String descriptor,
        final String signature,
        final Object value) {
        JSEEFieldNode field = new JSEEFieldNode(access, name, descriptor, signature, value);
        fields.add(field);
        return field;
    }

    @Override
    public MethodVisitor visitMethod(
        final int access,
        final String name,
        final String descriptor,
        final String signature,
        final String[] exceptions) {
        JSEEMethodNode method = new JSEEMethodNode(access, name, descriptor, signature, exceptions);
        methods.add(method);
        return method;
    }

    public JSEEMethodNode getMethodNode(ClassNode node, String name, String signature) {
        return (JSEEMethodNode) ASMUtil.getMethodNode(node, name, signature);
    }

    public JSEEMethodNode getMethodNode(ClassNode node, String name) {
        return (JSEEMethodNode) ASMUtil.getMethodNode(node, name);
    }

    public JSEEFieldNode getFieldNode(ClassNode node, String name, String signature) {
        return (JSEEFieldNode) ASMUtil.getFieldNode(node, name, signature);
    }

    public JSEEFieldNode getFieldNode(ClassNode node, String name) {
        return (JSEEFieldNode) ASMUtil.getFieldNode(node, name);
    }

    public ClassReader getReader() {
        return reader;
    }

    public ClassWriter getWriter() {
        return writer;
    }

    public byte[] toByteArray() {
        return writer.toByteArray();
    }
}
