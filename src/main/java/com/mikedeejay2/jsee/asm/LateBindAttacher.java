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

/**
 * A utility class for attaching a late binding agent to the current JVM.
 *
 * @see LateBindAttacher#attach(AgentInfo)
 * @author Mikedeejay2
 * @since 1.0.0
 */
public class LateBindAttacher {

    /**
     * A map of unique IDs to {@link AgentInfo}. Used so that newly created agents can locate other information needed
     * to execute successfully. The {@link LateBindAttacher#attach(AgentInfo)} method adds the <code>AgentInfo</code>
     * to this map, and the agent's transformer should remove that entry from the map after accessing.
     * @since 1.0.0
     */
    public static final Map<UUID, AgentInfo> INFO_MAP = new HashMap<>();

    /**
     * Private constructor. Throws <code>UnsupportedOperationException</code>
     * @since 1.0.0
     */
    private LateBindAttacher() {
        throw new UnsupportedOperationException("LateBindAttacher cannot be instantiated");
    }

    /**
     * Attach a new agent to this JVM. This method does the following
     * <ol>
     *     <li>
     *         Generate a jar file to be attached as an agent. This is a temporary file and is deleted when the VM
     *         closes.
     *     </li>
     *     <li>
     *         Force jdk.attach.allowAttachSelf to true if it is currently set to false. This can be set in the command
     *         line parameters to avoid this step, but in the case that it hasn't been set to true already, this
     *         function will run. This uses the {@link Unsafe} object to force the value to true.
     *     </li>
     *     <li>
     *         Attach the jar using {@link VirtualMachine}. If the <code>com.sun.tools.attach</code> package is missing
     *         in the JVM, this class will error. As of Java 9 and above, this package is included in the JRE but in
     *         Java 8 and below it was included in tools.jar which came with the JDK only. If Java 8 is a requirement,
     *         this package must be extracted from tools.jar and included in the final compiled jar file.
     *     </li>
     * </ol>
     *
     * @param info The {@link AgentInfo} used to create the agent. It includes all information required.
     * @since 1.0.0
     */
    public static void attach(AgentInfo info) {
        UUID uuid = UUID.randomUUID();
        try {
            final File jarFile = createTempFile();
            generateJar(info.getAgentMain(), info.getAgentClasses(), jarFile);
            setAllowAttachSelf(true);
            INFO_MAP.put(uuid, info);
            attachJar(info.getJVMPid(), jarFile, uuid + " " + info.getAdditionalArgs());
        } catch(AgentLoadException | IOException | AttachNotSupportedException | AgentInitializationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get this JVM's process ID from <code>RuntimeMXBean</code>
     *
     * @return The retrieved PID
     * @since 1.0.0
     */
    public static String getPidFromRuntimeBean() {
        String jvm = ManagementFactory.getRuntimeMXBean().getName();
        return jvm.substring(0, jvm.indexOf('@'));
    }

    /**
     * The base <code>Object</code> for the <code>ALLOW_ATTACH_SELF</code> field in the
     * <code>HotSpotVirtualMachine</code> class.
     * @since 1.0.0
     */
    private static final Object AAS_BASE;

    /**
     * The memory offset for the <code>ALLOW_ATTACH_SELF</code> field in the <code>HotSpotVirtualMachine</code> class.
     * @since 1.0.0
     */
    private static final long AAS_OFFSET;

    static {
        // Temporary storage values
        Object aasBase = null;
        long aasOffset = 0;
        try {
            Unsafe unsafe = JSEE.getUnsafe();
            // Get the class that stores whether allow attach self is enabled
            Class<?> hsvmClass = Class.forName("sun.tools.attach.HotSpotVirtualMachine");
            // Get the allow attach self field
            Field aasField = hsvmClass.getDeclaredField("ALLOW_ATTACH_SELF");
            // Obtain the base and offset of the field for use at a later date
            aasBase = unsafe.staticFieldBase(aasField);
            aasOffset = unsafe.staticFieldOffset(aasField);
        } catch(NoSuchFieldException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        AAS_BASE = aasBase;
        AAS_OFFSET = aasOffset;
    }

    /**
     * A replacement for the command line parameter <code>-Djdk.attach.allowAttachSelf=true</code>
     * <p>
     * Force jdk.attach.allowAttachSelf to true if it is currently set to false. This can be set in the command line
     * parameters to avoid this step, but in the case that it hasn't been set to true already, this function will run.
     * This uses the {@link Unsafe} object to force the value to true.
     *
     * @param value Whether to enable or disable <code>allowAttachSelf</code>
     * @since 1.0.0
     */
    public static void setAllowAttachSelf(boolean value) {
        String valueStr = String.valueOf(value);
        if(valueStr.equals(System.getProperty("jdk.attach.allowAttachSelf"))) return;
        JSEE.getUnsafe().putBoolean(AAS_BASE, AAS_OFFSET, value);
        System.setProperty("jdk.attach.allowAttachSelf", valueStr);
    }

    /**
     * Attach the jar using {@link VirtualMachine}. If the <code>com.sun.tools.attach</code> package is missing in the
     * JVM, this class will error. As of Java 9 and above, this package is included in the JRE but in Java 8 and below
     * it was included in tools.jar which came with the JDK only. If Java 8 is a requirement, this package must be
     * extracted from tools.jar and included in the final compiled jar file.
     *
     * @param JVMPid  The JVM's process ID
     * @param jarFile The jar file to attach
     * @param args    The arguments to be passed to <code>agentmain()</code>
     * @throws AttachNotSupportedException  If the {@code attachVirtualmachine} method of all installed providers throws
     *                                      {@code AttachNotSupportedException}, or there aren't any providers installed.
     * @throws IOException                  If an I/O error occurs
     * @throws AgentLoadException           If the agent does not exist, or cannot be started in the manner specified in
     *                                      the {@link java.lang.instrument} specification.
     * @throws AgentInitializationException If the {@code agentmain} throws an exception
     * @since 1.0.0
     */
    private static void attachJar(String JVMPid, File jarFile, String args)
        throws AttachNotSupportedException, IOException, AgentLoadException, AgentInitializationException {
        VirtualMachine vm = VirtualMachine.attach(JVMPid);
        vm.loadAgent(jarFile.getAbsolutePath(), args);
        vm.detach();
    }

    /**
     * Generate a jar file meant to act as an agent. This method generates a jar file with a custom manifest to act as
     * an agent and writes it to the disk.
     *
     * @param agentClass The <code>Class</code> where <code>agentmain()</code> is located
     * @param agentClasses The list of Classes that should be included in the jar file
     * @param jarFile The <code>File</code> of the jar file
     * @throws IOException if an I/O error has occurred with the <code>JarOutputStream</code>
     * @since 1.0.0
     */
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

    /**
     * Create a temporary file on the disk.
     *
     * @return The new temporary file
     * @throws IOException If a file could not be created
     * @since 1.0.0
     */
    private static File createTempFile() throws IOException {
        final File jarFile = File.createTempFile("agent", ".jar");
        jarFile.deleteOnExit();
        return jarFile;
    }
}
