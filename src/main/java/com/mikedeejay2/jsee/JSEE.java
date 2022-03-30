package com.mikedeejay2.jsee;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

public class JSEE {
    static {
//        MethodHandles.privateLookupIn()
    }

    private static boolean enabled;

    public static boolean setEnabled(boolean value) {
        if(value == enabled) return false;
        try {
            if(value) enable();
            else disable();
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            enabled = true;
        }
        return true;
    }

    private static void enable() throws ReflectiveOperationException {
    }

    private static void disable() throws ReflectiveOperationException {
    }
}
