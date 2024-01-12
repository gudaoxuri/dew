package group.idealworld.dew.devops.agent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Set;

/**
 * The type Transformer.
 *
 * @author gudaoxuri
 */
public class Transformer implements ClassFileTransformer {

    private String version;
    private Set<String> classPath;

    /**
     * Instantiates a new Transformer.
     *
     * @param version   the version
     * @param classPath the class path
     */
    Transformer(String version, Set<String> classPath) {
        this.version = version;
        this.classPath = classPath;
    }

    /**
     * Get bytes from file byte [ ].
     *
     * @param inputStream the input stream
     * @return the byte [ ]
     */
    public static byte[] getBytesFromFile(InputStream inputStream) {
        final int bufLen = 4 * 0x400; // 4KB
        byte[] buf = new byte[bufLen];
        int readLen;

        try {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                while ((readLen = inputStream.read(buf, 0, bufLen)) != -1) {
                    outputStream.write(buf, 0, readLen);
                }
                return outputStream.toByteArray();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (!classPath.contains(className)) {
            return null;
        }
        String filePath = "/" + version + "/" + className + ".class";
        return getBytesFromFile(Transformer.class.getResourceAsStream(filePath));
    }

}
