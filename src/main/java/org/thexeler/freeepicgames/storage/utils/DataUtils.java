package org.thexeler.freeepicgames.storage.utils;

import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.thexeler.freeepicgames.FreeEpicGames;
import org.thexeler.freeepicgames.FreeEpicGamesPaths;
import org.thexeler.freeepicgames.storage.agent.RaidDataAgent;
import org.thexeler.freeepicgames.storage.view.AbstractCacheView;
import org.thexeler.freeepicgames.storage.view.AbstractView;
import org.thexeler.freeepicgames.storage.view.RaidInstanceView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataUtils {

    public static <T> Tag encodeForTag(Codec<T> codec, T value) {
        return codec.encodeStart(NbtOps.INSTANCE, value).getOrThrow();
    }

    public static <T> JsonElement encodeForJson(Codec<T> codec, T value) {
        return codec.encodeStart(JsonOps.INSTANCE, value).getOrThrow();
    }

    public static <T> T decode(Codec<T> codec, Tag tag) {
        return codec.parse(NbtOps.INSTANCE, tag).getOrThrow();
    }

    public static <T> T decode(Codec<T> codec, JsonElement tag) {
        return codec.parse(JsonOps.INSTANCE, tag).getOrThrow();
    }

    public static String getValue(JsonObject jsonObject, String key, @NotNull String defaultValue) {
        if (jsonObject.get(key) == null || jsonObject.get(key).isJsonNull()) {
            jsonObject.addProperty(key, defaultValue);
        }
        return jsonObject.get(key).getAsString();
    }

    public static int getValue(JsonObject jsonObject, String key, int defaultValue) {
        if (jsonObject.get(key) == null || jsonObject.get(key).isJsonNull()) {
            jsonObject.addProperty(key, defaultValue);
        }
        return jsonObject.get(key).getAsInt();
    }

    public static float getValue(JsonObject jsonObject, String key , float defaultValue) {
        if (jsonObject.get(key) == null || jsonObject.get(key).isJsonNull()) {
            jsonObject.addProperty(key, defaultValue);
        }
        return jsonObject.get(key).getAsFloat();
    }

    public static double getValue(JsonObject jsonObject, String key, double defaultValue) {
        if (jsonObject.get(key) == null || jsonObject.get(key).isJsonNull()) {
            jsonObject.addProperty(key, defaultValue);
        }
        return jsonObject.get(key).getAsDouble();
    }

    public static boolean getValue(JsonObject jsonObject, String key, boolean defaultValue) {
        if (jsonObject.get(key) == null || jsonObject.get(key).isJsonNull()) {
            jsonObject.addProperty(key, defaultValue);
        }
        return jsonObject.get(key).getAsBoolean();
    }

    public static JsonObject getValue(JsonObject jsonObject, String key, @NotNull JsonObject defaultValue) {
        if (jsonObject.get(key) == null || jsonObject.get(key).isJsonNull()) {
            jsonObject.add(key, defaultValue);
        }
        return jsonObject.get(key).getAsJsonObject();
    }

    public static JsonArray getValue(JsonObject jsonObject, String key, @NotNull JsonArray defaultValue) {
        if (jsonObject.get(key) == null || jsonObject.get(key).isJsonNull()) {
            jsonObject.add(key, defaultValue);
        }
        return jsonObject.get(key).getAsJsonArray();
    }

    public static Map<String, JsonElement> getValue(JsonObject jsonObject, String key, @NotNull Map<String, JsonElement> defaultValue) {
        if (jsonObject.get(key) == null || jsonObject.get(key).isJsonNull()) {
            jsonObject.add(key, new JsonObject());
            defaultValue.forEach((s, element) -> jsonObject.get(key).getAsJsonObject().add(s, element));
        }
        return jsonObject.get(key).getAsJsonObject().asMap();
    }

    public static List<JsonElement> getValue(JsonObject jsonObject, String key, @NotNull List<JsonElement> defaultValue) {
        if (jsonObject.get(key) == null || jsonObject.get(key).isJsonNull()) {
            jsonObject.add(key, new JsonArray());
            defaultValue.forEach(element -> jsonObject.get(key).getAsJsonArray().add(element));
        }
        return jsonObject.get(key).getAsJsonArray().asList();
    }

    public static void computeViewMap(Map<String, ? extends AbstractView> views, JsonObject json) {
        clearDeprecatedKey(json, views);

        views.forEach((key, view) -> json.add(key, view.toJson()));
    }

    public static void computeCacheViewMap(Map<String, ? extends AbstractCacheView> views, JsonObject json) {
        clearDeprecatedKey(json, views);

        views.forEach((key, view) -> json.add(key, view.toCacheJson()));
    }

    public static void clearDeprecatedKey(JsonObject json, Map<String, ?> values) {
        json.asMap().forEach((key, value) -> {
            if (!values.containsKey(key)) {
                json.remove(key);
            }
        });
    }


    public static JsonArray fromContainer(Container container) {
        JsonArray array = new JsonArray();
        for (int i = 0; i < container.getContainerSize(); i++) {
            array.add(fromItemStack(container.getItem(i)));
        }
        return array;
    }

    public static Container toContainer(JsonArray json, Container container) {
        for (int i = 0; i < json.size(); i++) {
            ItemStack itemStack = toItemStack(json.get(i).getAsJsonObject());
            if (!itemStack.isEmpty()) {
                container.setItem(i, itemStack);
            }
        }
        return container;
    }

    public static Container toContainer(JsonArray json) {
        Container container = new SimpleContainer(27);
        return toContainer(json, container);
    }

    public static JsonObject fromItemStack(ItemStack itemStack) {
        return fromItemStack(itemStack.getItem(), itemStack.getCount(), itemStack.getComponentsPatch());
    }

    public static JsonObject fromItemStack(Item item, int count, DataComponentPatch patch) {
        JsonObject json = new JsonObject();

        json.addProperty("id", item.toString());
        json.addProperty("count", count);
        json.add("nbt", DataUtils.encodeForJson(DataComponentPatch.CODEC, patch));

        return json;
    }

    public static ItemStack toItemStack(JsonObject json) {
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(json.get("id").getAsString()));
        ItemStack itemStack = null;
        if (item instanceof AirItem) {
            FreeEpicGames.LOGGER.warn("Item not found: {}", json.get("id").getAsString());
        } else {
            itemStack = new ItemStack(item, json.get("count").getAsInt());
            itemStack.applyComponents(DataUtils.decode(DataComponentPatch.CODEC, json.get("nbt")));
        }

        return itemStack;
    }

    public static ItemStack toItemStack(Item item, int count, DataComponentPatch patch) {
        ItemStack itemStack = new ItemStack(item, count);
        itemStack.applyComponents(patch);
        return itemStack;
    }

    public static Map<String, JsonObject> getPackAllData(DataPacket type) {
        Path dir = DataPacket.getDirectory(type);
        Map<String, JsonObject> jsonMap = new HashMap<>();

        try {
            Files.walkFileTree(dir, new SimpleFileVisitor<>() {
                String prefix = null;

                @Override
                public @NotNull FileVisitResult preVisitDirectory(@NotNull Path dir, @NotNull BasicFileAttributes attrs) throws IOException {
                    if (Files.isHidden(dir) || Files.isSymbolicLink(dir)) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    if (prefix != null) {
                        prefix += dir.getFileName().toString() + '.';
                    } else {
                        prefix = "";
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public @NotNull FileVisitResult visitFile(@NotNull Path file, @NotNull BasicFileAttributes attrs) {
                    if (file.toString().endsWith(".json")) {
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile()))) {
                            JsonObject object = gson.fromJson(reader, JsonObject.class);

                            String filePath = file.getFileName().toString();
                            if (object == null) {
                                object = new JsonObject();
                            }
                            jsonMap.put(prefix + filePath.substring(0, filePath.lastIndexOf('.')), object);
                        } catch (IOException e) {
                            FreeEpicGames.LOGGER.error(e.getMessage());
                        }
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public @NotNull FileVisitResult postVisitDirectory(@NotNull Path dir, @Nullable IOException exc) {
                    prefix = prefix.substring(0, prefix.lastIndexOf('.', prefix.length() - 2) + 1);

                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            FreeEpicGames.LOGGER.error(e.getMessage());
        }

        return jsonMap;
    }

    public static void savePacketAllData(DataPacket type, Map<String, JsonObject> jsonMap) {
        Path dir = DataPacket.getDirectory(type);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();


        jsonMap.forEach((key, jsonObject) -> {
            try {
                String[] parts = key.split("\\.");
                Path filePath = dir;
                for (String part : parts) {
                    filePath = filePath.resolve(part);
                }
                filePath = FreeEpicGamesPaths.makeSureExist(filePath.getParent().resolve(filePath.getFileName() + ".json"));

                try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                    writer.write(gson.toJson(jsonObject));
                }
            } catch (IOException e) {
                FreeEpicGames.LOGGER.error(e.getMessage());
            }
        });

    }

    public static boolean isChunkEmpty(int chunkPosX, int chunkPosZ) {
        boolean flag = true;
        for (RaidInstanceView view : RaidDataAgent.getInstance().getAllRaidInstance()) {
            if (view.isInside(new Vec3((chunkPosX * 16) - 8, 0, (chunkPosZ * 16) - 8))) {
                flag = false;
                break;
            }
        }
        return flag;
    }
}