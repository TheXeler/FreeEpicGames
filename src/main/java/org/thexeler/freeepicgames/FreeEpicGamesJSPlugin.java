package org.thexeler.freeepicgames;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import org.thexeler.freeepicgames.event.kubejs.CommonEventsPostJS;
import org.thexeler.freeepicgames.event.kubejs.FreeEpicGamesEventJS;
import org.thexeler.slacker.SlackerForge;

public class FreeEpicGamesJSPlugin extends KubeJSPlugin {
    @Override
    public void init() {
        SlackerForge.EVENT_BUS.register(CommonEventsPostJS.class);
    }

    @Override
    public void registerEvents() {
        FreeEpicGamesEventJS.MENU_EVENT.register();
        FreeEpicGamesEventJS.NPC_EVENT.register();
        FreeEpicGamesEventJS.RAID_EVENT.register();
    }
}
