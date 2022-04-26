package com.mikedeejay2.jsee.asm.enhanced;

import com.mikedeejay2.jsee.asm.ASMUtil;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
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

    /**
     * Get a {@link MethodNode} from a {@link ClassNode} with a specified name and signature
     *
     * @param node      The <code>ClassNode</code> to obtain the <code>MethodNode</code> from
     * @param name      The name of the method to get
     * @param signature The signature of the method, only needed if there are multiple methods of the same name, can be
     *                  null
     * @return The located {@link MethodNode}, null if not found
     * @since 1.0.0
     */
    public MethodNode getMethodNode(ClassNode node, String name, String signature) {
        return ASMUtil.getMethodNode(node, name, signature);
    }

    /**
     * Get a {@link MethodNode} from a {@link ClassNode} with a specified name and signature
     *
     * @param node The <code>ClassNode</code> to obtain the <code>MethodNode</code> from
     * @param name The name of the method to get
     * @return The located {@link MethodNode}, null if not found
     * @since 1.0.0
     */
    public MethodNode getMethodNode(ClassNode node, String name) {
        return ASMUtil.getMethodNode(node, name);
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
