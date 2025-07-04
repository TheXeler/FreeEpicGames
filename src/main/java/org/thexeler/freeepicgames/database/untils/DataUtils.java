package org.thexeler.freeepicgames.database.untils;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.thexeler.freeepicgames.FreeEpicGames;
import org.thexeler.freeepicgames.FreeEpicGamesPaths;
import org.thexeler.freeepicgames.database.agent.GlobalRaidDataAgent;
import org.thexeler.freeepicgames.database.view.AbstractCacheView;
import org.thexeler.freeepicgames.database.view.AbstractView;
import org.thexeler.freeepicgames.database.view.RaidInstanceView;

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
    public static String getValue(JsonObject jsonObject, String key, String defaultValue) {
        if (jsonObject.get(key) == null) {
            jsonObject.addProperty(key, defaultValue);
        }
        return jsonObject.get(key).getAsString();
    }

    public static int getValue(JsonObject jsonObject, String key, int defaultValue) {
        if (jsonObject.get(key) == null) {
            jsonObject.addProperty(key, defaultValue);
        }
        return jsonObject.get(key).getAsInt();
    }

    public static float getValue(JsonObject jsonObject, String key, float defaultValue) {
        if (jsonObject.get(key) == null) {
            jsonObject.addProperty(key, defaultValue);
        }
        return jsonObject.get(key).getAsFloat();
    }

    public static double getValue(JsonObject jsonObject, String key, double defaultValue) {
        if (jsonObject.get(key) == null) {
            jsonObject.addProperty(key, defaultValue);
        }
        return jsonObject.get(key).getAsDouble();
    }

    public static boolean getValue(JsonObject jsonObject, String key, boolean defaultValue) {
        if (jsonObject.get(key) == null) {
            jsonObject.addProperty(key, defaultValue);
        }
        return jsonObject.get(key).getAsBoolean();
    }

    public static JsonObject getValue(JsonObject jsonObject, String key, JsonObject defaultValue) {
        if (jsonObject.get(key) == null) {
            jsonObject.add(key, defaultValue);
        }
        return jsonObject.get(key).getAsJsonObject();
    }

    public static JsonArray getValue(JsonObject jsonObject, String key, JsonArray defaultValue) {
        if (jsonObject.get(key) == null) {
            jsonObject.add(key, defaultValue);
        }
        return jsonObject.get(key).getAsJsonArray();
    }

    public static Map<String, JsonElement> getValue(JsonObject jsonObject, String key, Map<String, JsonElement> defaultValue) {
        if (jsonObject.get(key) == null) {
            jsonObject.add(key, new JsonObject());
            defaultValue.forEach((s, element) -> jsonObject.get(key).getAsJsonObject().add(s, element));
        }
        return jsonObject.get(key).getAsJsonObject().asMap();
    }

    public static List<JsonElement> getValue(JsonObject jsonObject, String key, List<JsonElement> defaultValue) {
        if (jsonObject.get(key) == null) {
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
        JsonObject json = new JsonObject();

        json.addProperty("id", itemStack.getItem().toString());
        json.addProperty("count", itemStack.getCount());
        json.addProperty("nbt", itemStack.serializeNBT().toString());

        return json;
    }

    public static JsonObject fromItemStack(Item item, int count, CompoundTag nbt) {
        JsonObject json = new JsonObject();

        json.addProperty("id", item.toString());
        json.addProperty("count", count);
        json.addProperty("nbt", nbt.toString());

        return json;
    }

    public static ItemStack toItemStack(JsonObject json) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(json.get("id").getAsString()));
        ItemStack itemStack = null;
        if (item != null) {
            itemStack = new ItemStack(item, json.get("count").getAsInt());

            try {
                itemStack.deserializeNBT(TagParser.parseTag(json.get("nbt").getAsString()));
            } catch (CommandSyntaxException ignored) {
                FreeEpicGames.LOGGER.warn("Failed to parse NBT \"{}\" for item {}", json.get("nbt").getAsString(), json.get("id").getAsString());
            }
        } else {
            FreeEpicGames.LOGGER.warn("Item not found: {}", json.get("id").getAsString());
        }

        return itemStack;
    }

    public static ItemStack toItemStack(Item item, int count, CompoundTag nbt) {
        ItemStack itemStack = new ItemStack(item, count);
        itemStack.deserializeNBT(nbt);
        return itemStack;
    }

    public static Map<String, JsonObject> getPackAllData(DataPacket type) {
        Path dir = DataPacket.getDirectory(type);
        Map<String, JsonObject> jsonMap = new HashMap<>();

        try {
            Files.walkFileTree(dir, new SimpleFileVisitor<>() {
                String prefix = "";

                @Override
                public @NotNull FileVisitResult preVisitDirectory(@NotNull Path dir, @NotNull BasicFileAttributes attrs) throws IOException {
                    if (Files.isHidden(dir) || Files.isSymbolicLink(dir)) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    prefix += dir.getFileName().toString() + '.';

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public @NotNull FileVisitResult visitFile(@NotNull Path file, @NotNull BasicFileAttributes attrs) {
                    if (file.endsWith(".json")) {
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile()))) {
                            JsonObject object = gson.fromJson(reader, JsonObject.class);

                            String filePath = file.getFileName().toString();
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
        for (RaidInstanceView view : GlobalRaidDataAgent.getInstance().getAllRaidInstance()) {
            if (view.isInside(new Vec3((chunkPosX * 16) - 8, 0, (chunkPosZ * 16) - 8))) {
                flag = false;
                break;
            }
        }
        return flag;
    }
}