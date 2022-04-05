package com.mikedeejay2.jsee.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.util.function.Consumer;
import java.util.function.Function;

public class ASMUtil {
    public static byte[] operateNode(byte[] classFileBuffer, Consumer<ClassNode> consumer, int writerOps) {
        ClassReader reader = new ClassReader(classFileBuffer);
        ClassNode classNode = new ClassNode(Opcodes.ASM9);
        reader.accept(classNode, 0);

        consumer.accept(classNode);

        ClassWriter writer = new ClassWriter(writerOps);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    public static byte[] operateNode(byte[] classFileBuffer, Consumer<ClassNode> consumer) {
        return operateNode(classFileBuffer, consumer, ClassWriter.COMPUTE_MAXS);
    }

    public static byte[] operateVisitor(byte[] classFileBuffer, Function<ClassVisitor, ClassVisitor> consumer, int writerOps, int readerOps) {
        ClassReader reader = new ClassReader(classFileBuffer);
        ClassWriter writer = new ClassWriter(writerOps); // true
        ClassVisitor visitor = consumer.apply(writer);
        // Add the class adapter as a modifier
        reader.accept(visitor, readerOps); //true
        return writer.toByteArray();
    }

    public static byte[] operateVisitor(byte[] classFileBuffer, Function<ClassVisitor, ClassVisitor> consumer, int writerOps) {
        return operateVisitor(classFileBuffer, consumer, writerOps, 0);
    }

    public static byte[] operateVisitor(byte[] classFileBuffer, Function<ClassVisitor, ClassVisitor> consumer) {
        return operateVisitor(classFileBuffer, consumer, ClassWriter.COMPUTE_MAXS);
    }
}
