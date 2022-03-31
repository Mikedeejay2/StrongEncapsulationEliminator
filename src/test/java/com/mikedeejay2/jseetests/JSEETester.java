package com.mikedeejay2.jseetests;

import com.mikedeejay2.jsee.JSEE;
import com.mikedeejay2.jsee.security.ModuleSecurity;

import java.lang.reflect.Field;

public class JSEETester {
    public static void main(String[] args) {
        ModuleSecurity.attachManipulator();
        System.out.println("Enabled JSEE");

        try {
            Class<?> testClass = Class.class;
            Field field = testClass.getDeclaredField("module");
            field.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Success");
        }
    }
}
