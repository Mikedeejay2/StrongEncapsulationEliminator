package com.mikedeejay2.jseetests.asm;

import com.mikedeejay2.jsee.JSEE;
import com.mikedeejay2.jsee.asm.AgentInfo;

public class ASMTest {
    public static void main(String[] args) {
        JSEE.attachASM(
            new AgentInfo()
                .addTransformers(new JSEEClassTransformer())
                .addAgentClasses(
                    JSEEClassTransformer.class,
                    TestProfile.class,
                    ProfileClassVisitor.class,
                    ProfileMethodVisitor.class)
                .addClassesToRedefine(ASMTest.class));

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
