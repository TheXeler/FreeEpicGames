package org.thexeler.freeepicgames;

import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import net.neoforged.neoforge.common.NeoForge;
import org.thexeler.freeepicgames.event.kubejs.CommonEventsPostJS;
import org.thexeler.freeepicgames.event.kubejs.FreeEpicGamesEventJS;

public class FreeEpicGamesJSPlugin implements KubeJSPlugin {
    @Override
    public void init() {
        NeoForge.EVENT_BUS.register(CommonEventsPostJS.class);
    }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(FreeEpicGamesEventJS.MENU_EVENT);
        registry.register(FreeEpicGamesEventJS.NPC_EVENT);
        registry.register(FreeEpicGamesEventJS.RAID_EVENT);
    }
}
