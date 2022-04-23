package com.mikedeejay2.jsee.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A utility class for reducing the boilerplate of ASM code.
 *
 * @author Mikedeejay2
 * @since 1.0.0
 */
public final class ASMUtil {
    /**
     * Private constructor. Throws <code>UnsupportedOperationException</code>
     * @since 1.0.0
     */
    private ASMUtil() {
        throw new UnsupportedOperationException("ASMUtil cannot be instantiated");
    }

    /**
     * Operate on a {@link ClassNode}, return modified bytes
     *
     * @param classFileBuffer The class bytes
     * @param consumer        The <code>Consumer</code> that takes <code>ClassNode</code>
     * @param writerOps       The writer flags from {@link ClassWriter} if needed, else 0
     * @param readerOps       The reader flags from {@link ClassReader} if needed, else 0
     * @return The modified class bytes
     * @since 1.0.0
     */
    public static byte[] operateNode(byte[] classFileBuffer, Consumer<ClassNode> consumer, int writerOps, int readerOps) {
        ClassReader reader = new ClassReader(classFileBuffer);
        ClassNode classNode = new ClassNode(Opcodes.ASM9);
        reader.accept(classNode, readerOps);

        consumer.accept(classNode);

        ClassWriter writer = new ClassWriter(writerOps);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    /**
     * Operate on a {@link ClassNode}, return modified bytes
     *
     * @param classFileBuffer The class bytes
     * @param consumer        The <code>Consumer</code> that takes <code>ClassNode</code>
     * @param writerOps       The writer flags from {@link ClassWriter} if needed, else 0
     * @return The modified class bytes
     * @since 1.0.0
     */
    public static byte[] operateNode(byte[] classFileBuffer, Consumer<ClassNode> consumer, int writerOps) {
        return operateNode(classFileBuffer, consumer, writerOps, 0);
    }

    /**
     * Operate on a {@link ClassNode}, return modified bytes
     *
     * @param classFileBuffer The class bytes
     * @param consumer The <code>Consumer</code> that takes <code>ClassNode</code>
     * @return The modified class bytes
     * @since 1.0.0
     */
    public static byte[] operateNode(byte[] classFileBuffer, Consumer<ClassNode> consumer) {
        return operateNode(classFileBuffer, consumer, ClassWriter.COMPUTE_MAXS);
    }

    /**
     * Operate a {@link ClassVisitor}, return modified bytes
     *
     * @param classFileBuffer The class bytes
     * @param consumer        A <code>Function</code> that receives the <code>ClassVisitor</code> and returns a custom visitor
     * @param writerOps       The writer flags from {@link ClassWriter} if needed, else 0
     * @param readerOps       The reader flags from {@link ClassReader} if needed, else 0
     * @return The modified class bytes
     * @since 1.0.0
     */
    public static byte[] operateVisitor(byte[] classFileBuffer, Function<ClassVisitor, ClassVisitor> consumer, int writerOps, int readerOps) {
        ClassReader reader = new ClassReader(classFileBuffer);
        ClassWriter writer = new ClassWriter(writerOps);
        ClassVisitor visitor = consumer.apply(writer);
        // Add the class adapter as a modifier
        reader.accept(visitor, readerOps);
        return writer.toByteArray();
    }

    /**
     * Operate a {@link ClassVisitor}, return modified bytes
     *
     * @param classFileBuffer The class bytes
     * @param consumer        A <code>Function</code> that receives the <code>ClassVisitor</code> and returns a custom visitor
     * @param writerOps       The writer flags from {@link ClassWriter} if needed, else 0
     * @return The modified class bytes
     * @since 1.0.0
     */
    public static byte[] operateVisitor(byte[] classFileBuffer, Function<ClassVisitor, ClassVisitor> consumer, int writerOps) {
        return operateVisitor(classFileBuffer, consumer, writerOps, 0);
    }

    /**
     * Operate a {@link ClassVisitor}, return modified bytes
     *
     * @param classFileBuffer The class bytes
     * @param consumer        A <code>Function</code> that receives the <code>ClassVisitor</code> and returns a custom visitor
     * @return The modified class bytes
     * @since 1.0.0
     */
    public static byte[] operateVisitor(byte[] classFileBuffer, Function<ClassVisitor, ClassVisitor> consumer) {
        return operateVisitor(classFileBuffer, consumer, ClassWriter.COMPUTE_MAXS);
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
    public static MethodNode getMethodNode(ClassNode node, String name, String signature) {
        for(MethodNode mNode : node.methods) {
            if(!name.equals(mNode.name)) continue;
            if(signature != null && !mNode.signature.equals(signature)) continue;
            return mNode;
        }
        return null;
    }

    /**
     * Get a {@link MethodNode} from a {@link ClassNode} with a specified name and signature
     *
     * @param node The <code>ClassNode</code> to obtain the <code>MethodNode</code> from
     * @param name The name of the method to get
     * @return The located {@link MethodNode}, null if not found
     * @since 1.0.0
     */
    public static MethodNode getMethodNode(ClassNode node, String name) {
        return getMethodNode(node, name, null);
    }

    /**
     * Get a {@link FieldNode} from a {@link ClassNode} with a specified name and signature
     *
     * @param node      The <code>ClassNode</code> to obtain the <code>FieldNode</code> from
     * @param name      The name of the field to get
     * @param signature The signature of the field, only required if you want to be absolutely sure of a data type
     * @return The located {@link FieldNode}, null if not found
     * @since 1.0.0
     */
    public static FieldNode getFieldNode(ClassNode node, String name, String signature) {
        for(FieldNode fNode : node.fields) {
            if(!name.equals(fNode.name)) continue;
            if(signature != null && !fNode.signature.equals(signature)) continue;
            return fNode;
        }
        return null;
    }

    /**
     * Get a {@link FieldNode} from a {@link ClassNode} with a specified name and signature
     *
     * @param node The <code>ClassNode</code> to obtain the <code>FieldNode</code> from
     * @param name The name of the field to get
     * @return The located {@link FieldNode}, null if not found
     * @since 1.0.0
     */
    public static FieldNode getFieldNode(ClassNode node, String name) {
        return getFieldNode(node, name, null);
    }
}
