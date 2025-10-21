package org.thexeler.tomls;

import net.neoforged.fml.loading.FMLPaths;
import org.thexeler.freeepicgames.FreeEpicGames;
import org.thexeler.tomls.exception.MultipleRegistrationException;
import org.thexeler.tomls.exception.TomlException;
import org.thexeler.tomls.exception.UnparseableException;
import org.thexeler.tomls.value.BooleanValue;
import org.thexeler.tomls.value.ConfigValue;
import org.thexeler.tomls.value.IntegerValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class TomlConfigSpec {
    private File configFile;
    private FileOutputStream configFileOutputStream;
    private final Map<String, ConfigValue<?>> rootValues;
    private final Map<String, Map<String, ConfigValue<?>>> zoneValues;

    private String commentCache;

    private TomlConfigSpec() {
        this.rootValues = new HashMap<>();
        this.zoneValues = new HashMap<>();
    }

    public void load() throws TomlException, IOException {
        if (Files.exists(this.configFile.toPath())) {
            try (Stream<String> stream = Files.lines(configFile.toPath())) {
                AtomicReference<Map<String, ConfigValue<?>>> currentZone = new AtomicReference<>(rootValues);

                stream.forEach(line -> {
                    line = line.trim();

                    if (!(line.isEmpty() || (line.indexOf(0) == '#'))) {
                        if (line.startsWith("[") && line.endsWith("]")) {
                            currentZone.set(zoneValues.computeIfAbsent(line.substring(1, line.length() - 1), k -> new HashMap<>()));
                        } else if (line.contains("=")) {
                            String[] parts = line.split("=", 2);
                            if (currentZone.get().containsKey(parts[0])) {
                                currentZone.get().get(parts[0]).parse(line);
                            } else {
                                throw new UnparseableException("Error config data line by : " + line);
                            }
                        } else {
                            throw new UnparseableException("Error config data line by : " + line);
                        }
                    }
                });
            }
        } else {
            Files.createFile(this.configFile.toPath());
        }
    }

    public void save() throws TomlException, IOException {
        rootValues.forEach((key, value) -> {
            if (value.getComment() != null && !value.getComment().isEmpty()) {
                writeToFile(value.getComment());
            }
            writeToFile(key + " = " + value.compile());
        });

        zoneValues.forEach((zone, values) -> {
            writeToFile("[" + zone + "]");
            values.forEach((key, value) -> {
                if (value.getComment() != null && !value.getComment().isEmpty()) {
                    writeToFile(value.getComment());
                }
                writeToFile(key + " = " + value.compile());
            });

        });
    }

    private void writeToFile(String string) {
        try {
            Files.writeString(configFile.toPath(), string + "\n", StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Builder {
        private final TomlConfigSpec spec;

        public Builder(String modId) {
            spec = new TomlConfigSpec();

            spec.configFile = FMLPaths.GAMEDIR.get().resolve("config").resolve(modId + ".toml").toFile();
            try {
                spec.configFileOutputStream = new FileOutputStream(spec.configFile);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        public Builder(File configFile) {
            spec = new TomlConfigSpec();

            spec.configFile = configFile;
        }

        private void registerValue(String key, ConfigValue<?> value) {
            if (!spec.rootValues.containsKey(key)) {
                spec.rootValues.put(key, value);
            } else {
                throw new MultipleRegistrationException(key);
            }
        }

        private void registerValue(String zone, String key, ConfigValue<?> value) {
            if (zone == null || zone.isEmpty()) {
                registerValue(key, value);
            } else {
                Map<String, ConfigValue<?>> valueZone = spec.zoneValues.computeIfAbsent(zone, k -> new HashMap<>());

                if (valueZone.containsKey(key)) {
                    valueZone.put(key, value);
                } else {
                    throw new MultipleRegistrationException(key);
                }
            }
        }

        public TomlConfigSpec build() {
            try {
                if (!Files.exists(spec.configFile.toPath())) {
                    Files.createFile(spec.configFile.toPath());
                }
            } catch (IOException e) {
                FreeEpicGames.LOGGER.error(e.getMessage());
            }

            return spec;
        }

        public Builder comment(String comment) {
            spec.commentCache = comment;

            return this;
        }

        public BooleanValue define(String key, boolean defaultValue) {
            return define("", key, defaultValue);
        }

        public BooleanValue define(String zone, String key, boolean defaultValue) {
            BooleanValue value = new BooleanValue(spec.commentCache, defaultValue);

            if (zone.isEmpty()) {
                spec.rootValues.put(key, value);
            } else {
                spec.zoneValues.computeIfAbsent(zone, k -> new HashMap<>()).put(key, value);
            }

            return value;
        }

        public IntegerValue defineInRange(String key, int defaultValue, int min, int max) {
            return defineInRange("", key, defaultValue, min, max);
        }

        public IntegerValue defineInRange(String zone, String key, int defaultValue, int min, int max) {
            IntegerValue value = new IntegerValue(spec.commentCache, defaultValue, min, max);
            registerValue(zone, key, value);
            return value;
        }

    }
}
