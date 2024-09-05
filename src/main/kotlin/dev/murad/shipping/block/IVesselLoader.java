package dev.murad.shipping.block;

import dev.murad.shipping.util.LinkableEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.EntityCapability;

import java.util.List;
import java.util.Optional;

public interface IVesselLoader {
    enum Mode {
        EXPORT,
        IMPORT
    }

    static <T> Optional<T> getEntityCapability(BlockPos pos, EntityCapability<T, ?> capability, Level level) {
        List<Entity> fluidEntities = level.getEntities((Entity) null,
                getSearchBox(pos),
                (e -> entityPredicate(e, pos, capability))
        );

        if (fluidEntities.isEmpty()) {
            return Optional.empty();
        } else {
            Entity entity = fluidEntities.getFirst();
            return Optional.ofNullable(entity.getCapability(capability,null));
        }
    }

    static boolean entityPredicate(Entity entity, BlockPos pos, EntityCapability<?, ?> capability) {
        return Optional.ofNullable(entity.getCapability(capability,null)).map(cap -> {
            if (entity instanceof LinkableEntity l) {
                return l.allowDockInterface() && (l.getBlockPos().getX() == pos.getX() && l.getBlockPos().getZ() == pos.getZ());
            } else {
                return true;
            }
        }).orElse(false);
    }

    static AABB getSearchBox(BlockPos pos) {
        return new AABB(
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                pos.getX() + 1D,
                pos.getY() + 1D,
                pos.getZ() + 1D);
    }

    <T extends Entity & LinkableEntity<T>> boolean hold(T vehicle, Mode mode);

}
