package org.thexeler.freeepicgames.handler;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.thexeler.freeepicgames.FreeEpicGamesConfigs;
import org.thexeler.freeepicgames.command.ModelCaptureCommand;
import org.thexeler.freeepicgames.command.ModelJobCommand;
import org.thexeler.freeepicgames.command.ModelNpcCommand;
import org.thexeler.freeepicgames.command.ModelRaidCommand;
import org.thexeler.lamp.ForgeLamp;
import org.thexeler.lamp.actor.ForgeCommandActor;
import revxrsal.commands.Lamp;

@Mod.EventBusSubscriber
public class CommandEventHandler {
    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        Lamp<ForgeCommandActor> lamp = ForgeLamp.builder(event.getDispatcher()).build();
        lamp.register(new ModelNpcCommand());
        if (FreeEpicGamesConfigs.isEnabledRaid) {
            lamp.register(new ModelRaidCommand());
        }
        if (FreeEpicGamesConfigs.isEnabledClasses) {
            lamp.register(new ModelJobCommand());
        }
        if (FreeEpicGamesConfigs.isEnabledCapture) {
            lamp.register(new ModelCaptureCommand());
        }
    }
}
