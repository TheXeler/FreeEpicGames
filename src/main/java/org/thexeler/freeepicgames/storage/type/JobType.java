package org.thexeler.freeepicgames.storage.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.thexeler.freeepicgames.FreeEpicGames;
import org.thexeler.freeepicgames.storage.utils.DataPacket;
import org.thexeler.freeepicgames.storage.utils.DataUtils;

import java.util.*;

public class JobType {
    private final static Map<String, JobType> types = Collections.synchronizedMap(new HashMap<>());

    @Getter
    private final String name;
    private final Map<Item, Map<Tag, Integer>> jobItems;

    public JobType(String name, Map<Item, Map<Tag, Integer>> jobItems) {
        this.name = name;
        this.jobItems = jobItems;
    }

    public void setItem(ItemStack stack) {
        setItem(stack, stack.getCount());
    }

    public void setItem(ItemStack stack, int num) {
        Item item = stack.getItem();
        Tag nbt = DataUtils.encodeForTag(DataComponentPatch.CODEC, stack.getComponentsPatch());
        if (nbt == null) {
            nbt = new CompoundTag();
        }
        Map<Tag, Integer> itemInfo = jobItems.computeIfAbsent(item,
                k -> Collections.synchronizedMap(new HashMap<>()));
        itemInfo.put(nbt, num);
    }

    public void removeItem(ItemStack stack) {
        Item item = stack.getItem();

        Tag nbt = DataUtils.encodeForTag(DataComponentPatch.CODEC, stack.getComponentsPatch());
        if (nbt == null) {
            nbt = new CompoundTag();
        }
        if (jobItems.get(item) != null && jobItems.get(item).get(nbt) != null) {
            jobItems.get(item).remove(nbt);
        }
        if (jobItems.get(item).isEmpty()) {
            jobItems.remove(item);
        }
    }

    public List<ItemStack> getAllItems() {
        List<ItemStack> itemStacks = new ArrayList<>();
        if (jobItems != null && !jobItems.isEmpty()) {
            jobItems.forEach((item, value) ->
                    value.forEach((nbt, count) ->
                            itemStacks.add(DataUtils.toItemStack(item, count, DataUtils.decode(DataComponentPatch.CODEC, nbt)))));
        }
        return itemStacks;
    }

    public JsonObject toJson() {
        JsonObject jobData = new JsonObject();

        jobData.add("items", new JsonArray());
        jobItems.forEach((item, itemInfo) ->
                itemInfo.forEach((nbt, num) ->
                        jobData.getAsJsonArray("items").add(DataUtils.fromItemStack(item, num, DataUtils.decode(DataComponentPatch.CODEC, nbt)))));

        return jobData;
    }

    public static boolean register(String name, JsonObject object) {
        if (!types.containsKey(name)) {
            Map<Item, Map<Tag, Integer>> items = Collections.synchronizedMap(new HashMap<>());
            JsonArray itemsJson = DataUtils.getValue(object, "items", new JsonArray());
            itemsJson.asList().forEach(element -> {
                ItemStack stack = DataUtils.toItemStack(element.getAsJsonObject());
                items.computeIfAbsent(stack.getItem(),
                                i -> Collections.synchronizedMap(new HashMap<>())).
                        put(DataUtils.encodeForTag(DataComponentPatch.CODEC, stack.getComponentsPatch()), stack.getCount());
            });
            types.put(name, new JobType(name, items));
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

    public static List<JobType> getAllType() {
        return new ArrayList<>(types.values());
    }

    public static List<String> getAllTypeName() {
        return new ArrayList<>(types.keySet());
    }

    @Nullable
    public static JobType getType(String name) {
        return types.get(name);
    }

    public static void init() {
        FreeEpicGames.LOGGER.info("Registering JobTypes...");
        DataUtils.getPackAllData(DataPacket.JOB_TYPE).forEach(JobType::register);
        FreeEpicGames.LOGGER.info("Registered {} JobTypes.", types.size());
    }


    public static void expire() {
        expire(false);
    }

    public static void expire(boolean forced) {
        if (!forced) {
            FreeEpicGames.LOGGER.info("Saving JobTypes...");
            Map<String, JsonObject> jsonMap = new HashMap<>();
            types.forEach((name, type) -> jsonMap.put(name, type.toJson()));
            DataUtils.savePacketAllData(DataPacket.JOB_TYPE, jsonMap);
        }
        FreeEpicGames.LOGGER.info("Expiring JobTypes...");
        types.clear();
        FreeEpicGames.LOGGER.info("Expired JobTypes.");
    }
}
