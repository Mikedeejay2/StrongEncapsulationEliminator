package com.mikedeejay2.jseetests.asm;

import com.mikedeejay2.jsee.JSEE;
import com.mikedeejay2.jsee.asm.AgentInfo;
import org.junit.jupiter.api.Test;

public class TestASMAttach {
    @Test
    public void testASMAttach() {
        JSEE.attachAgent(
            new AgentInfo()
                .addTransformers(new JSEEClassTransformer())
                .addAgentClasses(
                    JSEEClassTransformer.class,
                    TestProfile.class,
                    ProfileClassVisitor.class,
                    ProfileMethodVisitor.class)
                .addClassesToRedefine(TestASMAttach.class));

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
