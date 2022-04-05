package com.mikedeejay2.jsee;

import com.mikedeejay2.jsee.asm.AgentInfo;
import com.mikedeejay2.jsee.asm.LateBindAttacher;
import com.mikedeejay2.jsee.security.ModuleSecurity;
import com.mikedeejay2.jsee.unsafe.UnsafeGetter;
import sun.misc.Unsafe;

public final class JSEE {
    public static Unsafe getUnsafe() {
        return UnsafeGetter.getUnsafe();
    }

    public static void attachAgent(AgentInfo info) {
        LateBindAttacher.attach(info);
    }

    public static void toggleModuleSecurity() {
        ModuleSecurity.toggleSecurity();
    }
}
