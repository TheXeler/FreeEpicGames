package org.thexeler.freeepicgames.database.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.thexeler.freeepicgames.FreeEpicGames;
import org.thexeler.freeepicgames.database.untils.DataPacket;
import org.thexeler.freeepicgames.database.untils.DataUtils;

import java.util.*;

public class ChestMenuType {
    private final static Map<String, ChestMenuType> types = Collections.synchronizedMap(new HashMap<>());

    @Getter
    private final String name;
    @Getter
    private final int size;
    private final Map<ItemStack, String> items;
    @Getter
    private final Container container;

    public ChestMenuType(String name, int size, Map<ItemStack, String> items) {
        this.name = name;
        this.size = size;
        this.items = items;

        container = new SimpleContainer(size);
    }

    public JsonObject toJson() {
        JsonObject jobData = new JsonObject();

        jobData.add("items", new JsonArray());
        items.forEach((item, itemCmd) -> {
            JsonObject specialObject = DataUtils.fromItemStack(item);
            specialObject.addProperty("command", itemCmd);
            jobData.getAsJsonArray("items").add(specialObject);
        });

        return jobData;
    }

    public static boolean register(String name, JsonObject object) {
        if (!types.containsKey(name)) {
            Map<Item, Map<CompoundTag, Integer>> items = Collections.synchronizedMap(new HashMap<>());
            JsonArray itemsJson = DataUtils.getValue(object, "items", new JsonArray());
            itemsJson.asList().forEach(element -> {
                ItemStack stack = DataUtils.toItemStack(element.getAsJsonObject());
                items.computeIfAbsent(stack.getItem(),
                                i -> Collections.synchronizedMap(new HashMap<>())).
                        put(stack.serializeNBT(), stack.getCount());
            });
            types.put(name, new ChestMenuType(name, items));
            return true;
        } else {
            FreeEpicGames.LOGGER.error("Repeated registration key : " + name);
        }
        return false;
    }

    public static boolean unregister(String name) {
        if (types.containsKey(name)) {
            types.remove(name);
            return true;
        }
        return false;
    }

    public static List<ChestMenuType> getAllType() {
        return new ArrayList<>(types.values());
    }

    public static List<String> getAllTypeName() {
        return new ArrayList<>(types.keySet());
    }

    @Nullable
    public static ChestMenuType getType(String name) {
        return types.get(name);
    }

    public static void init() {
        FreeEpicGames.LOGGER.info("Registering JobTypes...");
        DataUtils.getPackAllData(DataPacket.JOB_TYPE).forEach(ChestMenuType::register);
        FreeEpicGames.LOGGER.info("Registered {} JobTypes.", types.size());
    }

    public static void expire() {
        FreeEpicGames.LOGGER.info("Saving JobTypes...");
        Map<String, JsonObject> jsonMap = new HashMap<>();
        types.forEach((name, type) -> jsonMap.put(name, type.toJson()));
        DataUtils.savePacketAllData(DataPacket.JOB_TYPE, jsonMap);
        FreeEpicGames.LOGGER.info("Expiring JobTypes...");
        types.clear();
        FreeEpicGames.LOGGER.info("Expired JobTypes.");
    }
}
