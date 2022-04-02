package com.mikedeejay2.jseetests;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TestMethods {
    public static void test() {
        System.out.println("Test");
    }

    public static boolean returnTrue() {
        boolean shouldBypass = false;
        try {
            Class<?> clazz = Class.forName("com.mikedeejay2.jsee.security.ModuleSecurity");
            Method method = clazz.getMethod("shouldBypassModuleSecurity");
            shouldBypass = (boolean) method.invoke(null);
        } catch(Exception e) {
            e.printStackTrace();
        }

        if(shouldBypass) {
            return true;
        }

        return false;
    }
}
