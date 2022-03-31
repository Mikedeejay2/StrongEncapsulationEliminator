package com.mikedeejay2.jsee.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public final class UnsafeGetter {
    private final static Unsafe unsafe;

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

    public static Unsafe getUnsafe() {
        return unsafe;
    }
}
