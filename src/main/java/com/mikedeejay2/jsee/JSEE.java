package com.mikedeejay2.jsee;

import com.mikedeejay2.jsee.asm.AgentInfo;
import com.mikedeejay2.jsee.asm.LateBindAttacher;
import com.mikedeejay2.jsee.security.ModuleSecurity;
import com.mikedeejay2.jsee.unsafe.UnsafeGetter;
import sun.misc.Unsafe;

/**
 * Java Strong Encapsulation Eliminator (JSEE)
 * <p>
 * JSEE is a set of tools used for manipulating the JVM. JSEE allows you to "see" and modify the entire Java Virtual
 * Machine at runtime. This library is meant to act as a Java power user's sandbox without restrictions. This library
 * can be dangerous, but it can also be extremely useful. Use it at your own risk.
 * <p>
 * GitHub repo: <a href="https://github.com/Mikedeejay2/StrongEncapsulationEliminator">https://github.com/Mikedeejay2/StrongEncapsulationEliminator</a>
 *
 * @since 1.0.0
 * @author Mikedeejay2
 */
public final class JSEE {
    /**
     * Private constructor. Throws <code>UnsupportedOperationException</code>
     *
     * @since 1.0.0
     */
    private JSEE() {
        throw new UnsupportedOperationException("JSEE cannot be instantiated");
    }

    /**
     * Get the {@link Unsafe} object
     *
     * @return The <code>Unsafe</code>
     * @since 1.0.0
     */
    public static Unsafe getUnsafe() {
        return UnsafeGetter.getUnsafe();
    }

    public static void attachAgent(AgentInfo info) {
        LateBindAttacher.attach(info);
    }

    public static void toggleModuleSecurity() {
        ModuleSecurity.toggleSecurity();
    }
}
