package com.mikedeejay2.jseetests;

import com.mikedeejay2.jsee.security.ModuleSecurity;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;

public class JSEETester {
    public static void main(String[] args) {
        ModuleSecurity.toggleSecurity();
        System.out.println("Disabled module security, all reflection should be possible now");

        try {
            Class<?> testClass = Class.class;
            Field field = testClass.getDeclaredField("module");
            field.setAccessible(true); // Would usually throw InaccessibleObjectException
            Module module = (Module) field.get(testClass);
            System.out.println("Successfully got private field " + module.toString());
            field.set(testClass, module);
            System.out.println("Successfully set private field");
            boolean open = testClass.getModule().isOpen(JSEETester.class.getPackageName());
            System.out.println("Open? " + open);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Successfully enabled");
        }


        ModuleSecurity.toggleSecurity();
        System.out.println("Re-enabled module security, InaccessibleObjectException should now be thrown");

        try {
            Class<?> testClass = Class.class;
            Field field = testClass.getDeclaredField("module");
            boolean open = testClass.getModule().isOpen(JSEETester.class.getPackageName());
            System.out.println("Open? " + open);
            field.setAccessible(false);
            field.setAccessible(true); // Will throw InaccessibleObjectException
            System.out.println("Something failed");
        } catch(InaccessibleObjectException e) {
            System.out.println("Successfully disabled");
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
