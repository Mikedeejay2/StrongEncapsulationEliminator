package com.mikedeejay2.jsee;

import java.lang.reflect.Field;

public class JSEETester {
    public static void main(String[] args) {
        JSEE.setEnabled(true);
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
