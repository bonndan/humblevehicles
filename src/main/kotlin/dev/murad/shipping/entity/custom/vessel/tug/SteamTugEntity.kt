package dev.murad.shipping.entity.custom.vessel.tug

import dev.murad.shipping.ShippingConfig
import dev.murad.shipping.entity.accessor.SteamHeadVehicleDataAccessor
import dev.murad.shipping.entity.container.SteamHeadVehicleContainer
import dev.murad.shipping.setup.ModEntityTypes
import dev.murad.shipping.setup.ModItems
import dev.murad.shipping.setup.ModSounds
import dev.murad.shipping.util.FuelItemStackHandler
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
import kotlin.math.ceil

class SteamTugEntity : AbstractTugEntity {

    private val FURNACE_FUEL_MULTIPLIER: ModConfigSpec.ConfigValue<Double>? =
        ShippingConfig.Server.STEAM_TUG_FUEL_MULTIPLIER
    private val fuelItemHandler: FuelItemStackHandler = FuelItemStackHandler()
    protected var burnTime: Int = 0
    protected var burnCapacity: Int = 0

    constructor(type: EntityType<out WaterAnimal>, world: Level) : super(type, world)

    constructor(worldIn: Level, x: Double, y: Double, z: Double) : super(ModEntityTypes.STEAM_TUG.get(), worldIn, x, y, z)

    override fun createContainerProvider(): MenuProvider {
        return object : MenuProvider {
            override fun getDisplayName(): Component {
                return Component.translatable("screen.humblevehicles.tug")
            }

            override fun createMenu(i: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu {
                return SteamHeadVehicleContainer<SteamTugEntity>(i, level(), getDataAccessor(), playerInventory, player)
            }
        }
    }

    val burnProgress: Int
        get() {
            var i: Int = burnCapacity
            if (i == 0) {
                i = 200
            }

            return burnTime * 13 / i
        }

    val isLit: Boolean
        // CONTAINER STUFF
        get() = burnTime > 0

    override fun getDataAccessor(): SteamHeadVehicleDataAccessor = SteamHeadVehicleDataAccessor.Builder()
        .withBurnProgress({ this.burnProgress })
        .withId(this.id)
        .withLit { this.isLit }
        .withVisitedSize { getNextStop() }
        .withOn { isEngineOn() }
        .withRouteSize { getPath()?.size ?: 0 }
        .withCanMove { enrollmentHandler.mayMove() }
        .build() as SteamHeadVehicleDataAccessor

    override fun tickFuel(): Boolean {
        if (burnTime > 0) {
            burnTime--
            return true
        } else {
            val burnTime: Int = fuelItemHandler.tryConsumeFuel()
            val adjustedBurnTime: Int = ceil(burnTime * FURNACE_FUEL_MULTIPLIER!!.get()) as Int
            this.burnCapacity = adjustedBurnTime
            this.burnTime = adjustedBurnTime
            return adjustedBurnTime > 0
        }
    }

    override fun getDropItem(): Item? {
        return ModItems.STEAM_TUG.get()
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        burnTime = if (compound.contains("burn")) compound.getInt("burn") else 0
        burnCapacity = if (compound.contains("burn_capacity")) compound.getInt("burn_capacity") else 0
        fuelItemHandler.deserializeNBT(registryAccess(), compound.getCompound("fuelItems"))
        super.readAdditionalSaveData(compound)
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        compound.putInt("burn", burnTime)
        compound.putInt("burn_capacity", burnCapacity)
        compound.put("fuelItems", fuelItemHandler.serializeNBT(registryAccess()))
        super.addAdditionalSaveData(compound)
    }

    override fun onUndock() {
        super.onUndock()
        this.playSound(ModSounds.STEAM_TUG_WHISTLE.get(), 1f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f)
    }

    // Have to implement IInventory to work with hoppers
    override fun isEmpty(): Boolean {
        return fuelItemHandler.getStackInSlot(0).isEmpty
    }

    override fun getItem(p_70301_1_: Int): ItemStack {
        return fuelItemHandler.getStackInSlot(p_70301_1_)
    }

    override fun setItem(slot: Int, stack: ItemStack) {
        if (!fuelItemHandler.isItemValid(slot, stack)) {
            return
        }
        fuelItemHandler.insertItem(slot, stack, false)
        if (!stack.isEmpty && stack.count > this.maxStackSize) {
            stack.count = this.maxStackSize
        }
    }

    companion object {
        fun setCustomAttributes(): AttributeSupplier.Builder {
            return AbstractTugEntity.setCustomAttributes()
        }
    }
}
