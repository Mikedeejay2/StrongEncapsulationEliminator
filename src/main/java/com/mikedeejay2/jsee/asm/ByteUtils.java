package com.mikedeejay2.jsee.asm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class for retrieving the bytes of a <code>Class</code>
 *
 * @since 1.0.0
 */
public final class ByteUtils {
    /**
     * Private constructor. Throws <code>UnsupportedOperationException</code>
     * @since 1.0.0
     */
    private ByteUtils() {
        throw new UnsupportedOperationException("ByteUtils cannot be instantiated");
    }

    /**
     * Get bytes from an <code>InputStream</code>
     *
     * @param stream The <code>InputStream</code> to retrieve the bytes from
     * @return The bytes from the stream
     * @since 1.0.0
     */
    public static byte[] getBytesFromIS(InputStream stream) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            int nRead;
            byte[] data = new byte[16384];

            while((nRead = stream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();
        } catch(IOException e) {
            System.err.println("Failed to convert InputStream to byte[]");
            e.printStackTrace();
        }
        return buffer.toByteArray();
    }

    /**
     * Get bytes from a <code>Class</code>.
     * <p>
     * This method ensures that if the class's <code>ClassLoader</code> is null, retrieve the Bootstrap class loader
     *
     * @param clazz The class to retrieve the bytes from
     * @return The bytes from the Class, null if the class cannot be resolved
     * @since 1.0.0
     */
    public static byte[] getBytesFromClass(Class<?> clazz) {
        ClassLoader loader = clazz.getClassLoader();
        if(loader == null) loader = ClassLoader.getSystemClassLoader().getParent();
        InputStream input = loader.getResourceAsStream(clazz.getName().replace('.', '/') + ".class");
        if(input == null) return null;
        return getBytesFromIS(input);
    }
}
