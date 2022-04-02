package com.mikedeejay2.jsee.asm;

import com.mikedeejay2.jsee.JSEE;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import sun.misc.Unsafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class LateBindAttacher {
    public static final Class<?> AGENT_MAIN_CLASS = LateBindAttacher.class;
    private static final Map<UUID, AgentInfo> infoMap = new HashMap<>();

    private LateBindAttacher() {
        throw new UnsupportedOperationException("LateBindAttacher cannot be instantiated");
    }

    public static void agentmain(String args, Instrumentation instrumentation) {
        UUID uuid = UUID.fromString(args);
        AgentInfo info = infoMap.get(uuid);
        if(info == null) {
            System.err.println("AgentInfo not found for " + uuid.toString());
            return;
        }
        for(ClassFileTransformer transformer : info.getTransformers()) {
            instrumentation.addTransformer(transformer);
        }

        for(Class<?> toRedefine : info.getClassesToRedefine()) {
            try {
                instrumentation.redefineClasses(new ClassDefinition(toRedefine, ByteUtils.getBytesFromClass(toRedefine)));
            } catch(UnmodifiableClassException | ClassNotFoundException | VerifyError e) {
                System.err.printf("Failed redefine for class %s%n", toRedefine.getName());
                e.printStackTrace();
            }
        }
    }



    public static void attach(AgentInfo info) {
        try {
            final File jarFile = createTempFile();
            generateJar(AGENT_MAIN_CLASS, info.getAgentClasses(), jarFile);
            setAllowAttachSelf(true);
            UUID uuid = UUID.randomUUID();
            infoMap.put(uuid, info);
            attachJar(info.getJVMPid(), jarFile, uuid);
        } catch(AgentLoadException | IOException | AttachNotSupportedException | AgentInitializationException e) {
            e.printStackTrace();
        }
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
            Unsafe unsafe = JSEE.getUnsafe();
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
        if(valueStr.equals(System.getProperty("jdk.attach.allowAttachSelf"))) return;
        JSEE.getUnsafe().putBoolean(AAS_BASE, AAS_OFFSET, value);
        System.setProperty("jdk.attach.allowAttachSelf", valueStr);
    }




    private static void attachJar(String JVMPid, File jarFile, UUID uuid)
        throws AttachNotSupportedException, IOException, AgentLoadException, AgentInitializationException {
        VirtualMachine vm = VirtualMachine.attach(JVMPid);
        vm.loadAgent(jarFile.getAbsolutePath(), uuid.toString());
        vm.detach();
    }

    private static void generateJar(Class<?> agentClass, List<Class<?>> agentClasses, File jarFile) throws IOException {
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
