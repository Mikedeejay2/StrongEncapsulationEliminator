package com.mikedeejay2.jsee.asm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class ByteUtils {
    private ByteUtils() {
        throw new UnsupportedOperationException("ByteUtils cannot be instantiated");
    }

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

    public static byte[] getBytesFromClass(Class<?> clazz) {
        return getBytesFromIS(clazz.getClassLoader().getResourceAsStream(clazz.getName().replace('.', '/') + ".class"));
    }
}
