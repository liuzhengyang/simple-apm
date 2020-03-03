package com.github.liuzhengyang.simpleapm.agent.command.decompiler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.slf4j.LoggerFactory;

import com.github.liuzhengyang.simpleapm.agent.util.ClassLoaderUtils;
import com.strobel.assembler.metadata.Buffer;
import com.strobel.assembler.metadata.ClasspathTypeLoader;
import com.strobel.assembler.metadata.ITypeLoader;

/**
 * @author liuzhengyang
 * Make something people want.
 * 2020/3/3
 */
public class ApmTypeLoader implements ITypeLoader {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ApmTypeLoader.class);

    private final static Logger LOG = Logger.getLogger(ClasspathTypeLoader.class.getSimpleName());


    public ApmTypeLoader() {
    }

    //
    // Temporarily removing this constructor to get a Java 9 compatibility fix out quickly.
    // Hopefully nobody is using it.  Will restore once ClasspathTypeLoader can be fleshed
    // out to support arbitrary paths.
    //
//    public ClasspathTypeLoader(final String classPath) {
//        throw new UnsupportedOperationException("Custom classpaths are temporarily unsupported.");
//    }

    @Override
    public boolean tryLoadType(final String internalName, final Buffer buffer) {
        List<ClassLoader> classLoaders = new ArrayList<>();
        classLoaders.add(ClassLoader.getSystemClassLoader());
        classLoaders.addAll(new HashSet<>(ClassLoaderUtils.getAllClassLoader().values()));
        for (ClassLoader classLoader : classLoaders) {
            if (doLoadType(classLoader, internalName, buffer)) {
                logger.info("Loaded {} by {}", internalName, classLoader);
                return true;
            }
        }
        return false;
    }

    public boolean doLoadType(ClassLoader classLoader, final String internalName, final Buffer buffer) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Attempting to load type: " + internalName + "...");
        }

        final String path = internalName.concat(".class");
        final URL resource = classLoader.getResource(path);

        if (resource == null) {
            return false;
        }

        try (final InputStream stream = classLoader.getResourceAsStream(path)) {
            final byte[] temp = new byte[4096];

            int bytesRead;

            while ((bytesRead = stream.read(temp, 0, temp.length)) > 0) {
//                buffer.ensureWriteableBytes(bytesRead);
                buffer.putByteArray(temp, 0, bytesRead);
            }

            buffer.flip();

            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Type loaded from " + resource + ".");
            }

            return true;
        }
        catch (final IOException ignored) {
            return false;
        }
    }
}
