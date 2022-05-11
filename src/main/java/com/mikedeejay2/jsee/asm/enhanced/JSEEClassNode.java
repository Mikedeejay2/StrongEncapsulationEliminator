package com.mikedeejay2.jsee.asm.enhanced;

import com.mikedeejay2.jsee.asm.ASMUtil;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;

/**
 * A custom class node to add functionality and helper methods
 *
 * @author Mikedeejay2
 * @since 1.0.0
 */
public class JSEEClassNode extends ClassNode {

    /**
     * The writer operands of the internal writer
     * @since 1.0.0
     */
    protected int writerOps;

    /**
     * The reader operands of the internal reader
     * @since 1.0.0
     */
    protected int readerOps;

    /**
     * Construct a new <code>JSEEClassNode</code>
     *
     * @param api The API level of ASM
     * @param classFileBuffer The class bytes
     * @param writerOps The writer operands of the internal writer
     * @param readerOps The reader operands of the internal reader
     * @since 1.0.0
     */
    public JSEEClassNode(int api, byte[] classFileBuffer, int writerOps, int readerOps) {
        super(api);
        this.writerOps = writerOps;
        this.readerOps = readerOps;
        read(classFileBuffer);

    }

    /**
     * Construct a new <code>JSEEClassNode</code>
     *
     * @param api The API level of ASM
     * @param classFileBuffer The class bytes
     * @param writerOps The writer operands of the internal writer
     * @since 1.0.0
     */
    public JSEEClassNode(int api, byte[] classFileBuffer, int writerOps) {
        this(api, classFileBuffer, writerOps, 0);
    }

    /**
     * Construct a new <code>JSEEClassNode</code>
     *
     * @param api The API level of ASM
     * @param classFileBuffer The class bytes
     * @since 1.0.0
     */
    public JSEEClassNode(int api, byte[] classFileBuffer) {
        this(api, classFileBuffer, 0, 0);
    }

    /**
     * Construct a new <code>JSEEClassNode</code>
     *
     * @param classFileBuffer The class bytes
     * @param writerOps The writer operands of the internal writer
     * @param readerOps The reader operands of the internal reader
     * @since 1.0.0
     */
    public JSEEClassNode(byte[] classFileBuffer, int writerOps, int readerOps) {
        this(Opcodes.ASM9, classFileBuffer, writerOps, readerOps);
    }

    /**
     * Construct a new <code>JSEEClassNode</code>
     *
     * @param classFileBuffer The class bytes
     * @param writerOps The writer operands of the internal writer
     * @since 1.0.0
     */
    public JSEEClassNode(byte[] classFileBuffer, int writerOps) {
        this(Opcodes.ASM9, classFileBuffer, writerOps, 0);
    }

    /**
     * Construct a new <code>JSEEClassNode</code>
     *
     * @param classFileBuffer The class bytes
     * @since 1.0.0
     */
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

    /**
     * Get a method node contained within this class
     *
     * @param name The name of the method
     * @param signature The method's signature, only needed if multiple methods have the same name
     * @return The method node, null if not found
     * @since 1.0.0
     */
    public JSEEMethodNode getMethodNode(String name, String signature) {
        return (JSEEMethodNode) ASMUtil.getMethodNode(this, name, signature);
    }

    /**
     * Get a method node contained within this class
     *
     * @param name The name of the method
     * @return The method node, null if not found
     * @since 1.0.0
     */
    public JSEEMethodNode getMethodNode(String name) {
        return (JSEEMethodNode) ASMUtil.getMethodNode(this, name);
    }

    /**
     * Get a field node contained within this class
     *
     * @param name The name of the field
     * @param signature The field's signature, only needed to ensure whether data types are as expected
     * @return The method node, null if not found
     * @since 1.0.0
     */
    public JSEEFieldNode getFieldNode(String name, String signature) {
        return (JSEEFieldNode) ASMUtil.getFieldNode(this, name, signature);
    }

    /**
     * Get a field node contained within this class
     *
     * @param name The name of the field
     * @return The method node, null if not found
     * @since 1.0.0
     */
    public JSEEFieldNode getFieldNode(String name) {
        return (JSEEFieldNode) ASMUtil.getFieldNode(this, name);
    }

    /**
     * Read the input class bytes into this node
     *
     * @param classFileBuffer The class bytes to read
     * @since 1.0.0
     */
    protected void read(byte[] classFileBuffer) {
        ClassReader reader = new ClassReader(classFileBuffer);
        reader.accept(this, readerOps);
    }

    /**
     * Get the class bytes of this class node
     *
     * @return The generated class bytes
     * @since 1.0.0
     */
    public byte[] toByteArray() {
        ClassWriter writer = new ClassWriter(writerOps);
        this.accept(writer);
        return writer.toByteArray();
    }

    /**
     * Get the writer operands of the internal writer
     *
     * @return The writer operands
     * @since 1.0.0
     */
    public int getWriterOps() {
        return writerOps;
    }

    /**
     * Set the writer operands of the internal writer
     *
     * @param writerOps The new writer operands
     * @since 1.0.0
     */
    public void setWriterOps(int writerOps) {
        this.writerOps = writerOps;
    }

    /**
     * Get the reader operands of the internal reader
     *
     * @return The reader operands
     * @since 1.0.0
     */
    public int getReaderOps() {
        return readerOps;
    }

    /**
     * Set the reader operands of the internal reader
     *
     * @param readerOps The new reader operands
     * @since 1.0.0
     */
    public void setReaderOps(int readerOps) {
        this.readerOps = readerOps;
    }
}
