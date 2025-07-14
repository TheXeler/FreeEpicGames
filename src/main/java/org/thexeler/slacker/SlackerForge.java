package org.thexeler.slacker;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class SlackerForge {
    public static final Logger LOGGER = LoggerFactory.getLogger("slacker");
    public static final SlackerBus EVENT_BUS = new SlackerBus(MinecraftForge.EVENT_BUS);

    public static class SlackerBus {
        private final IEventBus bus;

        public SlackerBus(IEventBus bus) {
            this.bus = bus;
        }

        public void register(Object target) {
            bus.register(target);
        }

        public <T extends Event> void addListener(Consumer<T> consumer) {
            bus.addListener(consumer);
        }

        public <T extends Event> void addListener(Class<T> eventType, Consumer<T> consumer) {
            bus.addListener(EventPriority.NORMAL, false, eventType, consumer);
        }

        public <T extends Event> void addListener(EventPriority priority, Consumer<T> consumer) {
            bus.addListener(priority, consumer);
        }

        public <T extends Event> void addListener(EventPriority priority, Class<T> eventType, Consumer<T> consumer) {
            bus.addListener(priority, false, eventType, consumer);
        }

        public <T extends Event> void addListener(EventPriority priority, boolean receiveCanceled, Consumer<T> consumer) {
            bus.addListener(priority, receiveCanceled, consumer);
        }

        public <T extends Event> void addListener(EventPriority priority, boolean receiveCanceled, Class<T> eventType, Consumer<T> consumer) {
            bus.addListener(priority, receiveCanceled, eventType, consumer);
        }

        public <T extends Event> void addListener(boolean receiveCanceled, Consumer<T> consumer) {
            bus.addListener(EventPriority.NORMAL, receiveCanceled, consumer);
        }

        public <T extends Event> void addListener(boolean receiveCanceled, Class<T> eventType, Consumer<T> consumer) {
            bus.addListener(EventPriority.NORMAL, receiveCanceled, eventType, consumer);
        }

        public void unregister(Object object) {
            bus.unregister(object);
        }

        public <T extends Event> T post(T event) {
            bus.post(event);
            return event;
        }

        public <T extends Event> T post(EventPriority phase, T event) {
            event.setPhase(phase);
            bus.post(event);
            return event;
        }
    }
}
