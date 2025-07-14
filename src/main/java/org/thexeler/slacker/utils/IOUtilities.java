package org.thexeler.slacker.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import org.apache.commons.io.output.CloseShieldOutputStream;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiPredicate;

public final class IOUtilities {
    private static final String TEMP_FILE_SUFFIX = ".neoforge-tmp";
    private static final OpenOption[] OPEN_OPTIONS = {
            StandardOpenOption.WRITE,
            StandardOpenOption.TRUNCATE_EXISTING
    };

    private IOUtilities() {
    }

    public static void cleanupTempFiles(Path targetPath, @Nullable String prefix) throws IOException {
        try (var filesToDelete = Files.find(targetPath, 1, createPredicate(prefix))) {
            for (var file : filesToDelete.toList()) {
                Files.deleteIfExists(file);
            }
        }
    }

    private static BiPredicate<Path, BasicFileAttributes> createPredicate(@Nullable String prefix) {
        return (file, attributes) -> {
            final var fileName = file.getFileName().toString();
            return fileName.endsWith(TEMP_FILE_SUFFIX) && (prefix == null || fileName.startsWith(prefix));
        };
    }

    public static void writeNbtCompressed(CompoundTag tag, Path path) throws IOException {
        atomicWrite(path, stream -> {
            try (var bufferedStream = new BufferedOutputStream(stream)) {
                NbtIo.writeCompressed(tag, bufferedStream);
            }
        });
    }

    public static void writeNbt(CompoundTag tag, Path path) throws IOException {
        atomicWrite(path, stream -> {
            try (var bufferedStream = new BufferedOutputStream(stream);
                 var dataStream = new DataOutputStream(bufferedStream)) {
                NbtIo.write(tag, dataStream);
            }
        });
    }

    public static void atomicWrite(Path targetPath, WriteCallback writeCallback) throws IOException {
        final var tempPath = Files.createTempFile(
                targetPath.getParent(),
                targetPath.getFileName().toString(),
                TEMP_FILE_SUFFIX);

        try {
            try (var channel = FileChannel.open(tempPath, OPEN_OPTIONS)) {
                var stream = CloseShieldOutputStream.wrap(Channels.newOutputStream(channel));
                writeCallback.write(stream);

                channel.force(true);
            }

            try {
                Files.move(tempPath, targetPath, StandardCopyOption.ATOMIC_MOVE);
            } catch (java.nio.file.AtomicMoveNotSupportedException e) {
                Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception first) {
            try {
                Files.deleteIfExists(tempPath);
            } catch (Exception second) {
                first.addSuppressed(second);
            }

            throw first;
        }
    }

    public interface WriteCallback {
        void write(OutputStream stream) throws IOException;
    }
}

