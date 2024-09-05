package dev.murad.shipping.block.fluid;

import dev.murad.shipping.block.IVesselLoader;
import dev.murad.shipping.setup.ModTileEntitiesTypes;
import dev.murad.shipping.util.FluidDisplayUtil;
import dev.murad.shipping.util.LinkableEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Optional;

public class FluidHopperTileEntity extends BlockEntity implements IVesselLoader {

    public static final int CAPACITY = FluidType.BUCKET_VOLUME * 10;
    private int cooldownTime = 0;

    public FluidHopperTileEntity(BlockPos pos, BlockState state) {
        super(ModTileEntitiesTypes.FLUID_HOPPER.get(), pos, state);
    }

    protected FluidTank tank = new FluidTank(CAPACITY) {
        @Override
        protected void onContentsChanged() {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, 3);
            setChanged();
        }
    };


    public boolean use(Player player, InteractionHand hand) {
        boolean result = FluidUtil.interactWithFluidHandler(player, hand, tank);
        player.displayClientMessage(FluidDisplayUtil.getFluidDisplay(tank), false);
        return result;
    }

    public FluidTank getTank() {
        return this.tank;
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        this.getTank().readFromNBT(pRegistries, pTag);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        this.getTank().writeToNBT(pRegistries, pTag);
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag(HolderLookup.@NotNull Provider pRegistries) {
        var tag = super.getUpdateTag(pRegistries);
        saveAdditional(tag, pRegistries);    // okay to send entire inventory on chunk load
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookup) {
        super.onDataPacket(connection, pkt, lookup);
        this.loadAdditional(pkt.getTag(), lookup);
    }

    private void serverTickInternal() {
        if (this.level != null) {
            --this.cooldownTime;
            if (this.cooldownTime <= 0) {
                // do not short-circuit
                this.cooldownTime = Boolean.logicalOr(this.tryExportFluid(), tryImportFluid()) ? 0 : 10;
            }
        }
    }

    private Optional<IFluidHandler> getExternalFluidHandler(BlockPos pos) {

        IFluidHandler iFluidHandler = this.level.getCapability(Capabilities.FluidHandler.BLOCK, pos, null);
        if (iFluidHandler != null) {
            return Optional.of(iFluidHandler);
        }

        return  IVesselLoader.getEntityCapability(pos, Capabilities.FluidHandler.ENTITY, this.level);
    }

    private boolean tryImportFluid() {
        return getExternalFluidHandler(this.getBlockPos().above())
                .map(iFluidHandler ->
                        !FluidUtil.tryFluidTransfer(this.tank, iFluidHandler, 50, true).isEmpty()
                ).orElse(false);
    }

    private boolean tryExportFluid() {
        return getExternalFluidHandler(this.getBlockPos().relative(this.getBlockState().getValue(FluidHopperBlock.FACING)))
                .map(iFluidHandler ->
                        !FluidUtil.tryFluidTransfer(iFluidHandler, this.tank, 50, true).isEmpty()
                ).orElse(false);
    }

    @Override
    public <T extends Entity & LinkableEntity<T>> boolean hold(T vehicle, Mode mode) {
        var capability = Optional.ofNullable(vehicle.getCapability(Capabilities.FluidHandler.ENTITY, null));
        return capability.map(iFluidHandler -> {
            switch (mode) {
                case IMPORT:
                    return !FluidUtil.tryFluidTransfer(this.tank, iFluidHandler, 1, false).isEmpty();
                case EXPORT:
                    return !FluidUtil.tryFluidTransfer(iFluidHandler, this.tank, 1, false).isEmpty();
                default:
                    return false;
            }
        }).orElse(false);
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, FluidHopperTileEntity e) {
        e.serverTickInternal();
    }
}
