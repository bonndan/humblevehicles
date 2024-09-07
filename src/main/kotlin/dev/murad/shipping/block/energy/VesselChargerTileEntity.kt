package dev.murad.shipping.block.energy

import dev.murad.shipping.ShippingConfig
import dev.murad.shipping.block.IVesselLoader
import dev.murad.shipping.capability.ReadWriteEnergyStorage
import dev.murad.shipping.setup.ModTileEntitiesTypes
import dev.murad.shipping.util.LinkableEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.energy.IEnergyStorage
import java.util.*

class VesselChargerTileEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(ModTileEntitiesTypes.VESSEL_CHARGER.get(), pos, state), IVesselLoader {

    private val internalBattery = ReadWriteEnergyStorage(MAX_CAPACITY, MAX_TRANSFER, MAX_TRANSFER)
    private var cooldownTime = 0

    init {
        internalBattery.setEnergy(0)
    }

    private fun serverTickInternal() {
        if (this.level != null) {
            --this.cooldownTime
            if (this.cooldownTime <= 0) {
                this.cooldownTime = if (tryChargeEntity()) 0 else 10
            }
        }
    }

    private fun tryChargeEntity(): Boolean {
        return getEntityCapability(
            blockPos.relative(blockState.getValue(VesselChargerBlock.FACING)),
            Capabilities.EnergyStorage.ENTITY,
            level
        )?.let { iEnergyStorage: IEnergyStorage ->
            val vesselCap = iEnergyStorage.receiveEnergy(MAX_TRANSFER, true)
            val toTransfer = internalBattery.extractEnergy(vesselCap, false)
            iEnergyStorage.receiveEnergy(toTransfer, false) > 0
        } ?: false
    }

    override fun loadAdditional(pTag: CompoundTag, pRegistries: HolderLookup.Provider) {
        super.loadAdditional(pTag, pRegistries)
        internalBattery.readAdditionalSaveData(pTag.getCompound("energy_storage"))
    }

    override fun saveAdditional(pTag: CompoundTag, pRegistries: HolderLookup.Provider) {
        val energyNBT = CompoundTag()
        internalBattery.addAdditionalSaveData(energyNBT)
        super.saveAdditional(pTag, pRegistries)
        pTag.put("energy_storage", energyNBT)
    }

    override fun <T> hold(vehicle: T, mode: IVesselLoader.Mode?): Boolean where T : Entity, T : LinkableEntity<T> {
        val capability = vehicle.getCapability(Capabilities.EnergyStorage.ENTITY, null)
        return Optional.ofNullable(capability).map { energyHandler: IEnergyStorage ->
            if (mode == IVesselLoader.Mode.EXPORT) {
                return@map energyHandler.energyStored < energyHandler.maxEnergyStored - 50 && internalBattery.energyStored > 50
            }
            false
        }.orElse(false)
    }

    fun use(player: Player, hand: InteractionHand?) {
        player.displayClientMessage(
            Component.translatable(
                "block.littlelogistics.vessel_charger.capacity",
                internalBattery.energyStored, internalBattery.maxEnergyStored
            ), false
        )
    }

    companion object {
        private val MAX_TRANSFER: Int = ShippingConfig.Server.VESSEL_CHARGER_BASE_MAX_TRANSFER!!.get()
        private val MAX_CAPACITY: Int = ShippingConfig.Server.VESSEL_CHARGER_BASE_CAPACITY!!.get()

        fun serverTick(pLevel: Level?, pPos: BlockPos?, pState: BlockState?, e: VesselChargerTileEntity) {
            e.serverTickInternal()
        }
    }
}
