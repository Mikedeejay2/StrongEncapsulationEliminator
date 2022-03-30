package com.mikedeejay2.jsee;

public class JSEE {
    public static boolean setEnabled(boolean value) {
        return value ? enable() : disable();
    }

    private static boolean enable() {
        return true;
    }

    private static boolean disable() {
        return true;
    }
}
