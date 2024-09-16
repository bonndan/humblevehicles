package dev.murad.shipping.entity.custom.vessel.tug

import dev.murad.shipping.ShippingConfig
import dev.murad.shipping.capability.ReadWriteEnergyStorage
import dev.murad.shipping.entity.accessor.EnergyHeadVehicleDataAccessor
import dev.murad.shipping.entity.container.EnergyHeadVehicleContainer
import dev.murad.shipping.item.EnergyUtil.getEnergyStorage
import dev.murad.shipping.setup.ModEntityTypes
import dev.murad.shipping.setup.ModItems
import dev.murad.shipping.util.InventoryUtils.getEnergyCapabilityInSlot
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.animal.WaterAnimal
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.common.ModConfigSpec
import net.neoforged.neoforge.items.ItemStackHandler

class EnergyTugEntity : AbstractTugEntity {

    private val itemHandler = createHandler()
    private val internalBattery = ReadWriteEnergyStorage(MAX_ENERGY.get(), MAX_TRANSFER.get(), Int.MAX_VALUE)

    constructor(type: EntityType<out WaterAnimal>, world: Level) : super(type, world) {
        internalBattery.setEnergy(0)
    }

    constructor(worldIn: Level, x: Double, y: Double, z: Double) : super(
        ModEntityTypes.ENERGY_TUG.get(),
        worldIn,
        x,
        y,
        z
    ) {
        internalBattery.setEnergy(0)
    }

    override fun getDropItem(): Item? {
        // todo: Store contents?
        return ModItems.ENERGY_TUG.get()
    }

    override fun createContainerProvider(): MenuProvider {
        return object : MenuProvider {
            override fun getDisplayName(): Component {
                return Component.translatable("screen.humblevehicles.energy_tug")
            }

            override fun createMenu(i: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu {
                return EnergyHeadVehicleContainer<EnergyTugEntity>(i, level(), getDataAccessor(), playerInventory, player)
            }
        }
    }

    private fun createHandler(): ItemStackHandler {

        return object : ItemStackHandler(1) {
            override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
                return getEnergyStorage(stack).isPresent
            }

            override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
                if (!isItemValid(slot, stack)) {
                    return stack
                }

                return super.insertItem(slot, stack, simulate)
            }
        }
    }

    override fun getRawHandler(): ItemStackHandler {
        return itemHandler
    }

    override fun makeSmoke() {
    }

    // Energy tug can be loaded at all times since there is no concern
    // with mix-ups like with fluids and items
    override fun allowDockInterface(): Boolean {
        return true
    }

    override fun getDataAccessor(): EnergyHeadVehicleDataAccessor {
        return EnergyHeadVehicleDataAccessor.Builder()
            .withEnergy { internalBattery.energyStored }
            .withCapacity { internalBattery.maxEnergyStored }
            .withLit { internalBattery.energyStored > 0 } // has energy
            .withId(this.id)
            .withVisitedSize { getNextStop() }
            .withOn { isEngineOn() }
            .withCanMove { enrollmentHandler.mayMove() }
            .withRouteSize { getPath()?.size ?: 0 }
            .build() as EnergyHeadVehicleDataAccessor
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        internalBattery.readAdditionalSaveData(compound.getCompound("energy_storage"))
        if (compound.contains("inv")) {
            val old = ItemStackHandler()
            old.deserializeNBT(this.registryAccess(), compound.getCompound("inv"))
            itemHandler.setStackInSlot(0, old.getStackInSlot(1))
        } else {
            itemHandler.deserializeNBT(this.registryAccess(), compound.getCompound("tugItemHandler"))
        }
        super.readAdditionalSaveData(compound)
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        val energyNBT = CompoundTag()
        internalBattery.addAdditionalSaveData(energyNBT)
        compound.put("energy_storage", energyNBT)
        compound.put("tugItemHandler", itemHandler.serializeNBT(this.registryAccess()))
        super.addAdditionalSaveData(compound)
    }

    override fun tick() {
        // grab energy from capacitor
        if (!level().isClientSide) {
            val capability = getEnergyCapabilityInSlot(0, itemHandler)
            if (capability != null) {
                // simulate first
                var toExtract = capability.extractEnergy(MAX_TRANSFER.get(), true)
                toExtract = internalBattery.receiveEnergy(toExtract, false)
                capability.extractEnergy(toExtract, false)
            }
        }

        super.tick()
    }

    override fun tickFuel(): Boolean {
        return internalBattery.extractEnergy(ENERGY_USAGE.get(), false) > 0
    }

    override fun isEmpty(): Boolean {
        return itemHandler.getStackInSlot(0).isEmpty
    }

    override fun getItem(pSlot: Int): ItemStack {
        return itemHandler.getStackInSlot(pSlot)
    }


    override fun setItem(p_70299_1_: Int, p_70299_2_: ItemStack) {
        if (!itemHandler.isItemValid(p_70299_1_, p_70299_2_)) {
            return
        }
        itemHandler.insertItem(p_70299_1_, p_70299_2_, false)
        if (!p_70299_2_.isEmpty && p_70299_2_.count > this.maxStackSize) {
            p_70299_2_.count = this.maxStackSize
        }
    }

    companion object {
        private val MAX_ENERGY: ModConfigSpec.ConfigValue<Int> = ShippingConfig.Server.ENERGY_TUG_BASE_CAPACITY!!
        private val MAX_TRANSFER: ModConfigSpec.ConfigValue<Int> = ShippingConfig.Server.ENERGY_TUG_BASE_MAX_CHARGE_RATE!!
        private val ENERGY_USAGE: ModConfigSpec.ConfigValue<Int> = ShippingConfig.Server.ENERGY_TUG_BASE_ENERGY_USAGE!!

        fun setCustomAttributes(): AttributeSupplier.Builder {
            return AbstractTugEntity.setCustomAttributes()
        }
    }

}
