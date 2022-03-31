package com.mikedeejay2.jseetests.asm;

public class TestProfile {
    public static void start(String className, String methodName) {
        System.out.printf("%s\t%s\tstart\t%d%n", className, methodName, System.currentTimeMillis());
    }

    public static void end(String className, String methodName) {
        System.out.printf("%s\t%s\tend\t%d%n", className, methodName, System.currentTimeMillis());
    }
}
