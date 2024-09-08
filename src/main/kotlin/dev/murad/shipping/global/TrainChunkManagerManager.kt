package dev.murad.shipping.global;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TrainChunkManagerManager extends SavedData {
    private final Table<ResourceKey<Level>, UUID, PlayerTrainChunkManager> managers = TreeBasedTable.create();

    public static TrainChunkManagerManager get(MinecraftServer server) {
        return server
                .overworld()
                .getDataStorage()
                .computeIfAbsent(
                        getPlayerTrainChunkManagerFactory(server),
                        "littlelogistics:trainchunkmanagermanager");
    }

    private static @NotNull Factory<TrainChunkManagerManager> getPlayerTrainChunkManagerFactory(MinecraftServer server) {

        return new Factory<>(
                () -> new TrainChunkManagerManager(server),
                (tag, provider) -> new TrainChunkManagerManager(tag, server),
                DataFixTypes.CHUNK
        );
    }

    private TrainChunkManagerManager(MinecraftServer level) {
    }

    private TrainChunkManagerManager(CompoundTag tag, MinecraftServer server) {
        for (Tag cell : tag.getList("saved", 10)) {
            if (cell instanceof CompoundTag compoundTag) {
                ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, ResourceLocation.read(compoundTag.getString("level")).getOrThrow());
                ServerLevel level = server.getLevel(dimension);
                if (level == null) {
                    return;
                }
                UUID uuid = compoundTag.getUUID("UUID");
                server.execute(() -> PlayerTrainChunkManager.getSaved(level, uuid)
                        .ifPresent(manager -> managers.put(dimension, uuid, manager)));
            }
        }
    }

    @Override
    public CompoundTag save(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        ListTag topList = new ListTag();
        for (var cell : managers.cellSet()) {
            CompoundTag inner = new CompoundTag();
            inner.putString("level", cell.getRowKey().location().toString());
            inner.putUUID("UUID", cell.getColumnKey());
            topList.add(inner);
        }
        pTag.put("saved", topList);
        return pTag;
    }

    public void enroll(PlayerTrainChunkManager playerTrainChunkManager) {
        managers.put(playerTrainChunkManager.getLevel().dimension(), playerTrainChunkManager.getUuid(), playerTrainChunkManager);
        setDirty();
    }

    public Set<PlayerTrainChunkManager> getManagers(ResourceKey<Level> dimension) {
        return new HashSet<>(managers.row(dimension).values());
    }

    public Set<PlayerTrainChunkManager> getManagers(UUID uuid) {
        return new HashSet<>(managers.column(uuid).values());
    }

    public int countVehicles(UUID uuid) {
        return getManagers(uuid).stream().reduce(0, (i, manager) -> i + manager.getNumVehicles(), Integer::sum);
    }
}
