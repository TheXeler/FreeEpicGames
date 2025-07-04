package org.thexeler.freeepicgames.command.lamp.parameters;

import com.google.common.collect.ForwardingList;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.thexeler.freeepicgames.command.lamp.actor.ForgeCommandActor;
import org.thexeler.freeepicgames.command.lamp.exception.EmptyEntitySelectorException;
import org.thexeler.freeepicgames.command.lamp.exception.MalformedEntitySelectorException;
import revxrsal.commands.Lamp;
import revxrsal.commands.annotation.list.AnnotationList;
import revxrsal.commands.exception.CommandErrorException;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static revxrsal.commands.util.Classes.getFirstGeneric;
import static revxrsal.commands.util.Classes.getRawType;
import static revxrsal.commands.util.Preconditions.notNull;

public final class EntitySelectorListParameterTypeFactory implements ParameterType.Factory<ForgeCommandActor> {

    @Override
    @SuppressWarnings({"unchecked"})
    public @Nullable <T> ParameterType<ForgeCommandActor, T> create(@NotNull Type parameterType, @NotNull AnnotationList annotations, @NotNull Lamp<ForgeCommandActor> lamp) {
        Class<?> rawType = getRawType(parameterType);
        if (rawType != EntitySelectorList.class)
            return null;
        Class<? extends Entity> entityClass = getRawType(getFirstGeneric(parameterType, Entity.class))
                .asSubclass(Entity.class);
        return (ParameterType<ForgeCommandActor, T>) new EntitySelectorListParameterType(entityClass);
    }

    static final class EntitySelectorListParameterType implements ParameterType<ForgeCommandActor, EntitySelectorList<? extends Entity>> {

        private final Class<?> entityType;

        public EntitySelectorListParameterType(Class<?> entityType) {
            this.entityType = entityType;
        }

        @Override
        public EntitySelectorList<? extends Entity> parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<ForgeCommandActor> context) {
            String selectorString = input.readString();
            try {
                EntitySelector selector = new EntitySelectorParser(new StringReader(selectorString)).getSelector();
                List<Entity> c = new ArrayList<>(selector.findEntities(context.actor().source()));
                c.removeIf(obj -> !entityType.isInstance(obj));
                if (c.isEmpty())
                    throw new EmptyEntitySelectorException(selectorString);
                return new SelectorList<>(c);
            } catch (IllegalArgumentException e) {
                throw new MalformedEntitySelectorException(selectorString, e.getCause().getMessage());
            } catch (CommandSyntaxException e) {
                throw new CommandErrorException(e.getCause().getMessage());
            } catch (NoSuchMethodError e) {
                throw new CommandErrorException("Entity selectors on legacy versions are not supported yet!");
            }
        }
    }

    static final class SelectorList<E extends Entity> extends ForwardingList<E> implements EntitySelectorList<E> {

        private final List<E> entities;

        public SelectorList(List<E> entities) {
            this.entities = notNull(entities, "entities list");
        }

        @Override
        protected @NotNull List<E> delegate() {
            return entities;
        }
    }
}
