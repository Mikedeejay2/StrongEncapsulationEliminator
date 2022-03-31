package com.mikedeejay2.jseetests.asm;

import com.mikedeejay2.jsee.asm.LateBindAttacher;

public class ASMTest {
    public static void main(String[] args) {
        try {
            LateBindAttacher.attach(
                JSEEAgent.class,
                JSEEAgent.class,
                LateBindAttacher.class,
                TestProfile.class,
                ProfileClassVisitor.class,
                ProfileMethodVisitor.class);
        } catch(Exception e) {
            e.printStackTrace();
        }

        sayHello(5);
        sayWorld();
    }

    public static void sayHello(int s) {
        System.out.println("Hello");
    }

    public static void sayWorld() {
        System.out.println("World");
    }
}
