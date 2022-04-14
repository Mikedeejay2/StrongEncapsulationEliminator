package com.mikedeejay2.jsee.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Allows easy access to the {@link Unsafe} object. This class uses reflection to obtain the <code>Unsafe</code> and
 * store it for use. Reflection is only used once on class initialization.
 *
 * @author Mikedeejay2
 * @since 1.0.0
 */
public final class UnsafeGetter {
    /**
     * Private constructor. Throws <code>UnsupportedOperationException</code>
     *
     * @since 1.0.0
     */
    private UnsafeGetter() {
        throw new UnsupportedOperationException("UnsafeGetter cannot be instantiated");
    }

    /**
     * The {@link Unsafe} object. Retrieved at class initialization.
     *
     * @since 1.0.0
     */
    private final static Unsafe unsafe;

    // Static block obtains Unsafe
    static {
        Unsafe temp = null;
        try {
            Class<?> unsafeClass = Unsafe.class;
            Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            temp = (Unsafe) unsafeField.get(null);
        } catch(NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        unsafe = temp;
    }

    /**
     * Get the {@link Unsafe} object
     *
     * @return The <code>Unsafe</code>
     * @since 1.0.0
     */
    public static Unsafe getUnsafe() {
        return unsafe;
    }
}
