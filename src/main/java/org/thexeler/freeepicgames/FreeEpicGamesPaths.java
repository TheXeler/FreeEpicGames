package org.thexeler.freeepicgames;

import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FreeEpicGamesPaths {
    public static final Path ROOT_DIRECTORS = makeSureExist(FMLPaths.GAMEDIR.get().resolve("fegames"));

    public static final Path JOB_DIRECTORS = makeSureExist(ROOT_DIRECTORS.resolve("job"));
    public static final Path JOB_TYPE_DIR = makeSureExist(JOB_DIRECTORS.resolve("type"));

    public static final Path NPC_DIRECTORS = makeSureExist(ROOT_DIRECTORS.resolve("npc"));
    public static final Path NPC_TYPE_DIR = makeSureExist(NPC_DIRECTORS.resolve("type"));
    public static final Path NPC_SCRIPT_DIR = makeSureExist(NPC_DIRECTORS.resolve("script"));

    public static final Path RAID_DIRECTORS = makeSureExist(ROOT_DIRECTORS.resolve("raid"));
    public static final Path RAID_TEMPLATE_DIR = makeSureExist(RAID_DIRECTORS.resolve("template"));

    public static final Path RAID_TREASURE_DIRECTORS = makeSureExist(RAID_DIRECTORS.resolve("treasure"));
    public static final Path RAID_TREASURE_TYPE_DIR = makeSureExist(RAID_TREASURE_DIRECTORS.resolve("list"));

    public static Path makeSureExist(Path path) {
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            FreeEpicGames.LOGGER.error(e.getMessage());
        }
        return path;
    }
}
