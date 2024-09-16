package dev.murad.shipping.entity.custom.train.locomotive

import dev.murad.shipping.ShippingConfig
import dev.murad.shipping.capability.ReadWriteEnergyStorage
import dev.murad.shipping.entity.accessor.EnergyHeadVehicleDataAccessor
import dev.murad.shipping.entity.accessor.HeadVehicleDataAccessor
import dev.murad.shipping.entity.container.EnergyHeadVehicleContainer
import dev.murad.shipping.item.EnergyUtil
import dev.murad.shipping.setup.ModEntityTypes
import dev.murad.shipping.setup.ModItems
import dev.murad.shipping.util.InventoryUtils
import dev.murad.shipping.util.ItemHandlerVanillaContainerWrapper
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.Containers
import net.minecraft.world.MenuProvider
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.items.ItemStackHandler

class EnergyLocomotiveEntity : AbstractLocomotiveEntity, ItemHandlerVanillaContainerWrapper, WorldlyContainer {

    private val energyItemHandler = object : ItemStackHandler() {

        override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
            return EnergyUtil.getEnergyStorage(stack).isPresent
        }

        override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
            if (!isItemValid(slot, stack)) {
                return stack
            }

            return super.insertItem(slot, stack, simulate)
        }
    }

    private val internalBattery = ReadWriteEnergyStorage(MAX_ENERGY, MAX_TRANSFER, Int.MAX_VALUE)

    constructor(type: EntityType<*>, level: Level) : super(type, level) {
        internalBattery.setEnergy(0)
    }

    constructor(level: Level, x: Double, y: Double, z: Double) : super(
        ModEntityTypes.ENERGY_LOCOMOTIVE.get(), level, x, y, z
    ) {
        internalBattery.setEnergy(0)
    }

    //TODO the itemhandler capability allowed only energy items, the battery capabiltiy is ReadWriteEnergyStorage
    /*
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return energyItemHandlerOpt.cast();
        } else if (cap == ForgeCapabilities.ENERGY) {
            return internalBatteryOpt.cast();
        }

        return super.getCapability(cap, side);
     */

    override fun remove(r: RemovalReason) {
        if (!level().isClientSide) {
            Containers.dropContents(this.level(), this, this)
        }
        super.remove(r)
    }

    override fun createContainerProvider(): MenuProvider {
        return object : MenuProvider {
            override fun getDisplayName(): Component {
                return Component.translatable("entity.humblevehicles.energy_locomotive")
            }

            override fun createMenu(i: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu {
                return EnergyHeadVehicleContainer<EnergyLocomotiveEntity>(
                    i, level(),
                    getDataAccessor() as EnergyHeadVehicleDataAccessor, playerInventory, player
                )
            }
        }
    }

    override fun getDataAccessor(): HeadVehicleDataAccessor {
        return EnergyHeadVehicleDataAccessor.Builder()
            .withEnergy { internalBattery.energyStored }
            .withCapacity { internalBattery.maxEnergyStored }
            .withLit { internalBattery.energyStored > 0 } // has energy
            .withId(this.id)
            .withOn { isEngineOn() }
            .withRouteSize { navigator.routeSize }
            .withVisitedSize { navigator.visitedSize }
            .withCanMove { enrollmentHandler.mayMove() }
            .build() as EnergyHeadVehicleDataAccessor
    }

    override fun tick() {
        // grab energy from capacitor
        if (!level().isClientSide) {
            val capability = InventoryUtils.getEnergyCapabilityInSlot(0, energyItemHandler)
            if (capability != null) {
                // simulate first
                var toExtract = capability.extractEnergy(MAX_TRANSFER, true)
                toExtract = internalBattery.receiveEnergy(toExtract, false)
                capability.extractEnergy(toExtract, false)
            }
        }

        super.tick()
    }

    override fun tickFuel(): Boolean {
        return internalBattery.extractEnergy(ENERGY_USAGE, false) > 0
    }


    override fun getPickResult(): ItemStack {
        return ItemStack(ModItems.ENERGY_LOCOMOTIVE.get())
    }

    override fun getRawHandler(): ItemStackHandler {
        return energyItemHandler
    }

    override fun getSlotsForFace(dir: Direction): IntArray {
        return intArrayOf(0)
    }

    override fun canPlaceItemThroughFace(index: Int, itemStack: ItemStack, dir: Direction?): Boolean {
        return stalling.isDocked()
    }

    override fun canTakeItemThroughFace(index: Int, itemStack: ItemStack, dir: Direction): Boolean {
        return false
    }

    public override fun readAdditionalSaveData(compound: CompoundTag) {
        energyItemHandler.deserializeNBT(this.registryAccess(), compound.getCompound("inv"))
        internalBattery.readAdditionalSaveData(compound.getCompound("energy_storage"))
        super.readAdditionalSaveData(compound)
    }

    public override fun addAdditionalSaveData(compound: CompoundTag) {
        compound.put("inv", energyItemHandler.serializeNBT(registryAccess()))
        val energyNBT = CompoundTag()
        internalBattery.addAdditionalSaveData(energyNBT)
        compound.put("energy_storage", energyNBT)
        super.addAdditionalSaveData(compound)
    }

    override fun getDropItem(): Item {
        return super.getDropItem()
    }

    companion object {
        private val MAX_ENERGY: Int = ShippingConfig.Server.ENERGY_LOCO_BASE_CAPACITY!!.get()
        private val MAX_TRANSFER: Int = ShippingConfig.Server.ENERGY_LOCO_BASE_MAX_CHARGE_RATE!!.get()
        private val ENERGY_USAGE: Int = ShippingConfig.Server.ENERGY_LOCO_BASE_ENERGY_USAGE!!.get()
    }
}
