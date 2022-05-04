package com.mikedeejay2.jsee.asm.enhanced;

import com.mikedeejay2.jsee.asm.ASMUtil;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;

/**
 * @author Mikedeejay2
 * @since 1.0.0
 */
public class JSEEClassNode extends ClassNode {

    /**
     * The writer operands of the internal writer
     */
    protected int writerOps;

    /**
     * The reader operands of the internal reader
     */
    protected int readerOps;

    public JSEEClassNode(int api, byte[] classFileBuffer, int writerOps, int readerOps) {
        super(api);
        this.writerOps = writerOps;
        this.readerOps = readerOps;
        read(classFileBuffer);

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

    public JSEEMethodNode getMethodNode(String name, String signature) {
        return (JSEEMethodNode) ASMUtil.getMethodNode(this, name, signature);
    }

    public JSEEMethodNode getMethodNode(String name) {
        return (JSEEMethodNode) ASMUtil.getMethodNode(this, name);
    }

    public JSEEFieldNode getFieldNode(String name, String signature) {
        return (JSEEFieldNode) ASMUtil.getFieldNode(this, name, signature);
    }

    public JSEEFieldNode getFieldNode(String name) {
        return (JSEEFieldNode) ASMUtil.getFieldNode(this, name);
    }

    protected void read(byte[] classFileBuffer) {
        ClassReader reader = new ClassReader(classFileBuffer);
        reader.accept(this, readerOps);
    }

    public byte[] toByteArray() {
        ClassWriter writer = new ClassWriter(writerOps);
        this.accept(writer);
        return writer.toByteArray();
    }

    public int getWriterOps() {
        return writerOps;
    }

    public void setWriterOps(int writerOps) {
        this.writerOps = writerOps;
    }

    public int getReaderOps() {
        return readerOps;
    }

    public void setReaderOps(int readerOps) {
        this.readerOps = readerOps;
    }
}
