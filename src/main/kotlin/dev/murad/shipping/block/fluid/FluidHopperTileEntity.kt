package dev.murad.shipping.block.fluid

import dev.murad.shipping.block.IVesselLoader
import dev.murad.shipping.setup.ModTileEntitiesTypes
import dev.murad.shipping.util.FluidDisplayUtil
import dev.murad.shipping.util.LinkableEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.Connection
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.fluids.FluidUtil
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.templates.FluidTank
import java.util.*

class FluidHopperTileEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(ModTileEntitiesTypes.FLUID_HOPPER.get(), pos, state), IVesselLoader {
    private var cooldownTime = 0

    var tank: FluidTank = object : FluidTank(CAPACITY) {
        override fun onContentsChanged() {
            val state = level!!.getBlockState(worldPosition)
            level!!.sendBlockUpdated(worldPosition, state, state, 3)
            setChanged()
        }
    }
        protected set


    fun use(player: Player, hand: InteractionHand): Boolean {
        val result = FluidUtil.interactWithFluidHandler(player, hand, tank)
        player.displayClientMessage(FluidDisplayUtil.getFluidDisplay(tank), false)
        return result
    }

    override fun loadAdditional(pTag: CompoundTag, pRegistries: HolderLookup.Provider) {
        super.loadAdditional(pTag, pRegistries)
        tank.readFromNBT(pRegistries, pTag)
    }

    override fun saveAdditional(pTag: CompoundTag, pRegistries: HolderLookup.Provider) {
        super.saveAdditional(pTag, pRegistries)
        tank.writeToNBT(pRegistries, pTag)
    }

    override fun getUpdateTag(pRegistries: HolderLookup.Provider): CompoundTag {
        val tag = super.getUpdateTag(pRegistries)
        saveAdditional(tag, pRegistries) // okay to send entire inventory on chunk load
        return tag
    }

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket? {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun onDataPacket(
        connection: Connection,
        pkt: ClientboundBlockEntityDataPacket,
        lookup: HolderLookup.Provider
    ) {
        super.onDataPacket(connection, pkt, lookup)
        this.loadAdditional(pkt.tag, lookup)
    }

    private fun serverTickInternal() {
        if (this.level != null) {
            --this.cooldownTime
            if (this.cooldownTime <= 0) {
                // do not short-circuit
                this.cooldownTime = if (java.lang.Boolean.logicalOr(this.tryExportFluid(), tryImportFluid())) 0 else 10
            }
        }
    }

    private fun getExternalFluidHandler(pos: BlockPos): Optional<IFluidHandler> {
        val iFluidHandler =
            level!!.getCapability(Capabilities.FluidHandler.BLOCK, pos, null)
        if (iFluidHandler != null) {
            return Optional.of(iFluidHandler)
        }

        return Optional.ofNullable(getEntityCapability(pos, Capabilities.FluidHandler.ENTITY, this.level))
    }

    private fun tryImportFluid(): Boolean {
        return getExternalFluidHandler(this.blockPos.above())
            .map { iFluidHandler: IFluidHandler? ->
                !FluidUtil.tryFluidTransfer(
                    this.tank, iFluidHandler, 50, true
                ).isEmpty
            }
            .orElse(false)
    }

    private fun tryExportFluid(): Boolean {
        return getExternalFluidHandler(this.blockPos.relative(blockState.getValue(FluidHopperBlock.FACING)))
            .map { iFluidHandler: IFluidHandler? ->
                !FluidUtil.tryFluidTransfer(
                    iFluidHandler,
                    this.tank,
                    50,
                    true
                ).isEmpty
            }
            .orElse(false)
    }

    override fun <T> hold(vehicle: T, mode: IVesselLoader.Mode?): Boolean where T : Entity, T : LinkableEntity<T> {
        val capability = Optional.ofNullable(
            vehicle.getCapability(Capabilities.FluidHandler.ENTITY, null)
        )
        return capability.map { iFluidHandler: IFluidHandler? ->
            when (mode) {
                IVesselLoader.Mode.IMPORT -> return@map !FluidUtil.tryFluidTransfer(
                    this.tank, iFluidHandler, 1, false
                ).isEmpty

                IVesselLoader.Mode.EXPORT -> return@map !FluidUtil.tryFluidTransfer(
                    iFluidHandler,
                    this.tank,
                    1,
                    false
                ).isEmpty

                else -> return@map false
            }
        }.orElse(false)
    }

    companion object {
        const val CAPACITY: Int = FluidType.BUCKET_VOLUME * 10

        fun serverTick(level: Level?, blockPos: BlockPos, blockState: BlockState, e: FluidHopperTileEntity) {
            e.serverTickInternal()
        }
    }
}
