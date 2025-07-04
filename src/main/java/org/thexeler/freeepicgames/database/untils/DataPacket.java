package org.thexeler.freeepicgames.database.untils;

import org.thexeler.freeepicgames.FreeEpicGamesPaths;

import java.nio.file.Path;

public enum DataPacket {
    JOB_TYPE,
    RAID_TEMPLATE,
    RAID_TREASURE_TYPE,
    NPC_TYPE,
    NPC_SCRIPT,
    CHEST_MENU_TYPE;

    public static Path getDirectory(DataPacket type) {
        return switch (type) {
            case JOB_TYPE -> FreeEpicGamesPaths.JOB_TYPE_DIR;
            case RAID_TEMPLATE -> FreeEpicGamesPaths.RAID_TEMPLATE_DIR;
            case RAID_TREASURE_TYPE -> FreeEpicGamesPaths.RAID_TREASURE_TYPE_DIR;
            case NPC_TYPE -> FreeEpicGamesPaths.NPC_TYPE_DIR;
            case NPC_SCRIPT -> FreeEpicGamesPaths.NPC_SCRIPT_DIR;
            case CHEST_MENU_TYPE -> FreeEpicGamesPaths.CHEST_MENU_TYPE_DIR;
        };
    }
}