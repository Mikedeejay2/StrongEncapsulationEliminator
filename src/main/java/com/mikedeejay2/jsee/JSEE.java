package com.mikedeejay2.jsee;

import com.mikedeejay2.jsee.asm.AgentInfo;
import com.mikedeejay2.jsee.asm.LateBindAttacher;
import com.mikedeejay2.jsee.unsafe.UnsafeGetter;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import sun.misc.Unsafe;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

public final class JSEE {
    public static Unsafe getUnsafe() {
        return UnsafeGetter.getUnsafe();
    }

    public static void attachASM(AgentInfo info) {
        LateBindAttacher.attach(info);
    }
}
