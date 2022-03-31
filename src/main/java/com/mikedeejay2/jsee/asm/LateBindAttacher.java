package com.mikedeejay2.jsee.asm;

import com.mikedeejay2.jsee.unsafe.UnsafeGetter;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import sun.misc.Unsafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class LateBindAttacher {
    private LateBindAttacher() {
        throw new UnsupportedOperationException("LateBindAttacher cannot be instantiated");
    }

    public static void attach(Class<? extends ClassFileTransformer> agentClass, String JVMPid, Class<?>... agentClasses)
        throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        final File jarFile = createTempFile();
        generateJar(agentClass, agentClasses, jarFile);
        setAllowAttachSelf(true);
        attachJar(JVMPid, jarFile);
    }

    public static void attach(Class<? extends ClassFileTransformer> agentClass, Class<?>... agentClasses)
        throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        attach(agentClass, getPidFromRuntimeBean(), agentClasses);
    }

    public static String getPidFromRuntimeBean() {
        String jvm = ManagementFactory.getRuntimeMXBean().getName();
        return jvm.substring(0, jvm.indexOf('@'));
    }

    private static final Object AAS_BASE;
    private static final long AAS_OFFSET;

    static {
        Object aasBase = null;
        long aasOffset = 0;
        try {
            Unsafe unsafe = UnsafeGetter.getUnsafe();
            Class<?> hsvmClass = Class.forName("sun.tools.attach.HotSpotVirtualMachine");
            Field aasField = hsvmClass.getDeclaredField("ALLOW_ATTACH_SELF");
            aasBase = unsafe.staticFieldBase(aasField);
            aasOffset = unsafe.staticFieldOffset(aasField);
        } catch(NoSuchFieldException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        AAS_BASE = aasBase;
        AAS_OFFSET = aasOffset;
    }

    // -Djdk.attach.allowAttachSelf=true
    public static void setAllowAttachSelf(boolean value) {
        String valueStr = String.valueOf(value);
        if(System.getProperty("jdk.attach.allowAttachSelf").equals(valueStr)) return;
        UnsafeGetter.getUnsafe().putBoolean(AAS_BASE, AAS_OFFSET, value);
        System.setProperty("jdk.attach.allowAttachSelf", valueStr);
    }




    private static void attachJar(String JVMPid, File jarFile)
        throws AttachNotSupportedException, IOException, AgentLoadException, AgentInitializationException {
        VirtualMachine vm = VirtualMachine.attach(JVMPid);
        vm.loadAgent(jarFile.getAbsolutePath());
        vm.detach();
    }

    private static void generateJar(Class<? extends ClassFileTransformer> agentClass, Class<?>[] agentClasses, File jarFile) throws IOException {
        final Manifest manifest = new Manifest();
        final Attributes mainAttributes = manifest.getMainAttributes();
        mainAttributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        mainAttributes.put(new Attributes.Name("Agent-Class"), agentClass.getName());
        mainAttributes.put(new Attributes.Name("Can-Retransform-Classes"), "true");
        mainAttributes.put(new Attributes.Name("Can-Redefine-Classes"), "true");
        final JarOutputStream jos = new JarOutputStream(new FileOutputStream(jarFile), manifest);

        for(Class<?> clazz : agentClasses) {
            final JarEntry agent = new JarEntry(clazz.getName().replace('.', '/') + ".class");
            jos.putNextEntry(agent);

            jos.write(ByteUtils.getBytesFromClass(clazz));
            jos.closeEntry();
        }

        jos.close();
    }

    private static File createTempFile() throws IOException {
        final File jarFile = File.createTempFile("agent", ".jar");
        jarFile.deleteOnExit();
        return jarFile;
    }
}
