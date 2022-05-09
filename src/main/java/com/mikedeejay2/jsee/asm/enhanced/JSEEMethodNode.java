package com.mikedeejay2.jsee.asm.enhanced;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

/**
 * A custom method node to add functionality and helper methods
 *
 * @author Mikedeejay2
 * @since 1.0.0
 */
public class JSEEMethodNode extends MethodNode {
    /**
     * Construct a new <code>JSEEMethodNode</code>
     */
    public JSEEMethodNode() {
        this(Opcodes.ASM9);
    }

    /**
     * Construct a new <code>JSEEMethodNode</code>
     *
     * @param api The API level of ASM
     */
    public JSEEMethodNode(int api) {
        super(api);
    }

    /**
     * Construct a new <code>JSEEMethodNode</code>
     *
     * @param api The API level of ASM
     * @param access The access level of the method
     * @param name The name of the method
     * @param descriptor The descriptor of the method
     * @param signature The signature of the method
     * @param exceptions The exceptions that the method throws
     */
    public JSEEMethodNode(int api, int access, String name, String descriptor, String signature, String[] exceptions) {
        super(api, access, name, descriptor, signature, exceptions);
    }

    /**
     * Construct a new <code>JSEEMethodNode</code>
     *
     * @param access The access level of the method
     * @param name The name of the method
     * @param descriptor The descriptor of the method
     * @param signature The signature of the method
     * @param exceptions The exceptions that the method throws
     */
    public JSEEMethodNode(int access, String name, String descriptor, String signature, String[] exceptions) {
        this(Opcodes.ASM9, access, name, descriptor, signature, exceptions);
    }
}
