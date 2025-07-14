package org.thexeler.slacker.registry;

import com.mojang.serialization.Codec;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.StructureModifier;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.holdersets.HolderSetType;

import java.util.function.Supplier;

@Mod.EventBusSubscriber
public class SlackerRegistries {
    // COMMON
    public static DefaultedRegistry<Block> BLOCK;
    public static IForgeRegistry<Block> BLOCKS;
    public static DefaultedRegistry<Fluid> FLUID;
    public static IForgeRegistry<Fluid> FLUIDS;
    public static Registry<Item> Item;
    public static IForgeRegistry<Item> ITEMS;
    public static Registry<MobEffect> MOB_EFFECT;
    public static IForgeRegistry<MobEffect> MOB_EFFECTS;
    public static Registry<SoundEvent> SOUND_EVENT;
    public static IForgeRegistry<SoundEvent> SOUND_EVENTS;
    public static Registry<Potion> POTION;
    public static IForgeRegistry<Potion> POTIONS;
    public static DefaultedRegistry<Enchantment> ENCHANTMENT;
    public static IForgeRegistry<Enchantment> ENCHANTMENTS;
    public static DefaultedRegistry<EntityType<?>> ENTITY_TYPE;
    public static IForgeRegistry<EntityType<?>> ENTITY_TYPES;
    public static Registry<BlockEntityType<?>> BLOCK_ENTITY_TYPE;
    public static IForgeRegistry<BlockEntityType<?>> BLOCK_ENTITY_TYPES;
    public static Registry<ParticleType<?>> PARTICLE_TYPE;
    public static IForgeRegistry<ParticleType<?>> PARTICLE_TYPES;
    public static Registry<MenuType<?>> MENU;
    public static IForgeRegistry<MenuType<?>> MENU_TYPES;
    public static Registry<RecipeType<?>> RECIPE_TYPE;
    public static IForgeRegistry<RecipeType<?>> RECIPE_TYPES;
    public static Registry<RecipeSerializer<?>> RECIPE_SERIALIZER;
    public static IForgeRegistry<RecipeSerializer<?>> RECIPE_SERIALIZERS;
    public static Registry<Attribute> ATTRIBUTE;
    public static IForgeRegistry<Attribute> ATTRIBUTES;
    public static Registry<StatType<?>> STAT_TYPE;
    public static IForgeRegistry<StatType<?>> STAT_TYPES;
    public static Registry<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPE;
    public static IForgeRegistry<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES;
    public static DefaultedRegistry<VillagerProfession> VILLAGER_PROFESSION;
    public static IForgeRegistry<VillagerProfession> VILLAGER_PROFESSIONS;
    public static Registry<PoiType> POINT_OF_INTEREST_TYPE;
    public static IForgeRegistry<PoiType> POI_TYPES;
    public static DefaultedRegistry<MemoryModuleType<?>> MEMORY_MODULE_TYPE;
    public static IForgeRegistry<MemoryModuleType<?>> MEMORY_MODULE_TYPES;
    public static DefaultedRegistry<SensorType<?>> SENSOR_TYPE;
    public static IForgeRegistry<SensorType<?>> SENSOR_TYPES;
    public static Registry<Schedule> SCHEDULE;
    public static IForgeRegistry<Schedule> SCHEDULES;
    public static Registry<Activity> ACTIVITY;
    public static IForgeRegistry<Activity> ACTIVITIES;
    public static Registry<WorldCarver<?>> CARVER;
    public static IForgeRegistry<WorldCarver<?>> WORLD_CARVERS;
    public static Registry<Feature<?>> FEATURE;
    public static IForgeRegistry<Feature<?>> FEATURES;
    public static DefaultedRegistry<ChunkStatus> CHUNK_STATUS;
    //public static IForgeRegistry<ChunkStatus> CHUNK_STATUS;
    //TODO
    public static IForgeRegistry<BlockStateProviderType<?>> BLOCK_STATE_PROVIDER_TYPES;
    public static IForgeRegistry<FoliagePlacerType<?>> FOLIAGE_PLACER_TYPES;
    public static IForgeRegistry<TreeDecoratorType<?>> TREE_DECORATOR_TYPES;
    public static IForgeRegistry<Biome> BIOMES;

    // 120 ONLY
    public static Registry<PaintingVariant> PAINTING_VARIANT;
    public static IForgeRegistry<PaintingVariant> PAINTING_VARIANTS;

    // 121 ONLY
    public static DefaultedRegistry<VillagerType> VILLAGER_TYPE;
    public static IForgeRegistry<VillagerType> VILLAGER_TYPES;

    public static Supplier<IForgeRegistry<EntityDataSerializer<?>>> ENTITY_DATA_SERIALIZERS;
    public static Supplier<IForgeRegistry<Codec<? extends IGlobalLootModifier>>> GLOBAL_LOOT_MODIFIER_SERIALIZERS;
    public static Supplier<IForgeRegistry<Codec<? extends BiomeModifier>>> BIOME_MODIFIER_SERIALIZERS;
    public static Supplier<IForgeRegistry<Codec<? extends StructureModifier>>> STRUCTURE_MODIFIER_SERIALIZERS;
    public static Supplier<IForgeRegistry<FluidType>> FLUID_TYPES;
    public static Supplier<IForgeRegistry<HolderSetType>> HOLDER_SET_TYPES;
    public static Supplier<IForgeRegistry<ItemDisplayContext>> DISPLAY_CONTEXTS;

//    @SubscribeEvent(priority = EventPriority.HIGHEST)
//    public static void init(FMLCommonSetupEvent event) {
//        BLOCKS = ForgeRegistries.BLOCKS;
//        FLUIDS = ForgeRegistries.FLUIDS;
//        ITEMS = ForgeRegistries.ITEMS;
//        MOB_EFFECTS = ForgeRegistries.MOB_EFFECTS;
//        SOUND_EVENTS = ForgeRegistries.SOUND_EVENTS;
//        POTIONS = ForgeRegistries.POTIONS;
//        ENCHANTMENTS = ForgeRegistries.ENCHANTMENTS;
//        ENTITY_TYPES = ForgeRegistries.ENTITY_TYPES;
//        BLOCK_ENTITY_TYPES = ForgeRegistries.BLOCK_ENTITY_TYPES;
//        PARTICLE_TYPES = ForgeRegistries.PARTICLE_TYPES;
//        MENU_TYPES = ForgeRegistries.MENU_TYPES;
//        PAINTING_VARIANTS = ForgeRegistries.PAINTING_VARIANTS;
//        RECIPE_TYPES = ForgeRegistries.RECIPE_TYPES;
//        RECIPE_SERIALIZERS = ForgeRegistries.RECIPE_SERIALIZERS;
//        ATTRIBUTES = ForgeRegistries.ATTRIBUTES;
//        STAT_TYPES = ForgeRegistries.STAT_TYPES;
//        COMMAND_ARGUMENT_TYPES = ForgeRegistries.COMMAND_ARGUMENT_TYPES;
//        VILLAGER_PROFESSIONS = ForgeRegistries.VILLAGER_PROFESSIONS;
//        POI_TYPES = ForgeRegistries.POI_TYPES;
//        MEMORY_MODULE_TYPES = ForgeRegistries.MEMORY_MODULE_TYPES;
//        SENSOR_TYPES = ForgeRegistries.SENSOR_TYPES;
//        SCHEDULES = ForgeRegistries.SCHEDULES;
//        ACTIVITIES = ForgeRegistries.ACTIVITIES;
//        WORLD_CARVERS = ForgeRegistries.WORLD_CARVERS;
//        FEATURES = ForgeRegistries.FEATURES;
//        //CHUNK_STATUS = ForgeRegistries.CHUNK_STATUS;
//        BLOCK_STATE_PROVIDER_TYPES = ForgeRegistries.BLOCK_STATE_PROVIDER_TYPES;
//        FOLIAGE_PLACER_TYPES = ForgeRegistries.FOLIAGE_PLACER_TYPES;
//        TREE_DECORATOR_TYPES = ForgeRegistries.TREE_DECORATOR_TYPES;
//        BIOMES = ForgeRegistries.BIOMES;
//
//        ENTITY_DATA_SERIALIZERS = ForgeRegistries.ENTITY_DATA_SERIALIZERS;
//        GLOBAL_LOOT_MODIFIER_SERIALIZERS = ForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS;
//        BIOME_MODIFIER_SERIALIZERS = ForgeRegistries.BIOME_MODIFIER_SERIALIZERS;
//        STRUCTURE_MODIFIER_SERIALIZERS = ForgeRegistries.STRUCTURE_MODIFIER_SERIALIZERS;
//        FLUID_TYPES = ForgeRegistries.FLUID_TYPES;
//        HOLDER_SET_TYPES = ForgeRegistries.HOLDER_SET_TYPES;
//        DISPLAY_CONTEXTS = ForgeRegistries.DISPLAY_CONTEXTS;
//    }
}
