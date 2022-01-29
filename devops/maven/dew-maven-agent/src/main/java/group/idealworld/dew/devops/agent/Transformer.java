/*
 * Copyright 2022. the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
