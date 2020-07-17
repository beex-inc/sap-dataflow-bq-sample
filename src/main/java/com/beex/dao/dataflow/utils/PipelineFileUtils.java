package com.beex.dao.dataflow.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.beam.sdk.io.FileSystems;
import org.apache.beam.sdk.io.fs.ResourceId;
import org.apache.beam.sdk.io.fs.ResolveOptions.StandardResolveOptions;

public class PipelineFileUtils {
    public static ResourceId getFileResourceId(String directory, String fileName) {
        ResourceId resourceID = FileSystems.matchNewResource(directory, true);
        return resourceID.getCurrentDirectory().resolve(fileName, StandardResolveOptions.RESOLVE_FILE);
    }

    public static void copyFile(ResourceId sourceFile, ResourceId destinationFile) throws IOException {

        try (WritableByteChannel writeChannel = FileSystems.create(destinationFile, "text/plain")) {
            try (ReadableByteChannel readChannel = FileSystems.open(sourceFile)) {

                final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
                while (readChannel.read(buffer) != -1) {
                    buffer.flip();
                    writeChannel.write(buffer);
                    buffer.compact();
                }
                buffer.flip();
                while (buffer.hasRemaining()) {
                    writeChannel.write(buffer);
                }
            }
        }
    }

    public static void setUpNativeLibrary(String sourcePath, String workerPath, String libFilename) throws Exception {

        Path path = Paths.get(workerPath);
        if (!path.toFile().exists()) {
            Files.createDirectories(path);
        }
        PipelineFileUtils.copyFile(PipelineFileUtils.getFileResourceId(sourcePath, libFilename),
                PipelineFileUtils.getFileResourceId(workerPath, libFilename));
    }

}