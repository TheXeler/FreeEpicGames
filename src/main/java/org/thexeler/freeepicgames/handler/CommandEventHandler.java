package org.thexeler.freeepicgames.handler;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.thexeler.freeepicgames.FreeEpicGamesConfigs;
import org.thexeler.freeepicgames.command.ModelCaptureCommand;
import org.thexeler.freeepicgames.command.ModelJobCommand;
import org.thexeler.freeepicgames.command.ModelNpcCommand;
import org.thexeler.freeepicgames.command.ModelRaidCommand;
import org.thexeler.lamp.ForgeLamp;
import org.thexeler.lamp.actor.ForgeCommandActor;
import revxrsal.commands.Lamp;

@EventBusSubscriber
public class CommandEventHandler {

    @SubscribeEvent
    public void onCommandsRegister(RegisterCommandsEvent event) {
        Lamp<ForgeCommandActor> lamp = ForgeLamp.builder(event.getDispatcher()).build();
        lamp.register(new ModelNpcCommand());
        if (FreeEpicGamesConfigs.isEnabledRaid) {
            lamp.register(new ModelRaidCommand());
        }
        if (FreeEpicGamesConfigs.isEnabledJob) {
            lamp.register(new ModelJobCommand());
        }
        if (FreeEpicGamesConfigs.isEnabledCapture) {
            lamp.register(new ModelCaptureCommand());
        }
    }
}
