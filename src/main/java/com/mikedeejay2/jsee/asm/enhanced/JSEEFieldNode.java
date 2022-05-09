package com.mikedeejay2.jsee.asm.enhanced;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldNode;

/**
 * A custom field node to add functionality and helper methods
 *
 * @author Mikedeejay2
 * @since 1.0.0
 */
public class JSEEFieldNode extends FieldNode {
    /**
     * Construct a new <code>JSEEFieldNode</code>
     *
     * @param api The API level of ASM
     * @param access The access level of the field
     * @param name The name of the field
     * @param descriptor The descriptor of the field
     * @param signature The signature of the field
     * @param value The value of the field.
     */
    public JSEEFieldNode(int api, int access, String name, String descriptor, String signature, Object value) {
        super(api, access, name, descriptor, signature, value);
    }


    /**
     * Construct a new <code>JSEEFieldNode</code>
     *
     * @param access The access level of the field
     * @param name The name of the field
     * @param descriptor The descriptor of the field
     * @param signature The signature of the field
     * @param value The value of the field.
     */
    public JSEEFieldNode(int access, String name, String descriptor, String signature, Object value) {
        this(Opcodes.ASM9, access, name, descriptor, signature, value);
    }
}
