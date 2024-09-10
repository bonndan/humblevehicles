package dev.murad.shipping.entity.custom.train.locomotive

import dev.murad.shipping.ShippingConfig
import dev.murad.shipping.entity.accessor.SteamHeadVehicleDataAccessor
import dev.murad.shipping.entity.container.SteamHeadVehicleContainer
import dev.murad.shipping.entity.custom.vessel.tug.AbstractTugEntity
import dev.murad.shipping.setup.ModEntityTypes
import dev.murad.shipping.setup.ModItems
import dev.murad.shipping.setup.ModSounds
import dev.murad.shipping.util.FuelItemStackHandler
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
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.items.ItemStackHandler
import kotlin.math.ceil

class SteamLocomotiveEntity : AbstractLocomotiveEntity, ItemHandlerVanillaContainerWrapper, WorldlyContainer {
    private val fuelItemHandler = FuelItemStackHandler()

    // How many ticks left on this fuel
    protected var burnTime: Int = 0

    // Max number of ticks for this fuel
    protected var burnCapacity: Int = 0

    val isLit: Boolean
        get() = burnTime > 0

    val burnProgress: Int
        get() {
            var i = burnCapacity
            if (i == 0) {
                i = 200
            }

            return burnTime * 13 / i
        }


    override val dataAccessor: SteamHeadVehicleDataAccessor
        get() = SteamHeadVehicleDataAccessor.Builder()
            .withBurnProgress { this.burnProgress }
            .withId(this.id)
            .withOn { isEngineOn() }
            .withRouteSize { navigator.routeSize }
            .withVisitedSize { navigator.visitedSize }
            .withLit { this.isLit }
            .withCanMove { enrollmentHandler.mayMove() }
            .build() as SteamHeadVehicleDataAccessor

    override fun tickFuel(): Boolean {
        if (burnTime > 0) {
            burnTime--
            return true
        } else {
            val burnTime = fuelItemHandler.tryConsumeFuel()
            val adjustedBurnTime = ceil(burnTime * FURNACE_FUEL_MULTIPLIER!!.get()) as Int
            this.burnCapacity = adjustedBurnTime
            this.burnTime = adjustedBurnTime
            return adjustedBurnTime > 0
        }
    }

    override fun onUndock() {
        super.onUndock()
        this.playSound(ModSounds.STEAM_TUG_WHISTLE.get(), 1f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f)
    }

    override fun createContainerProvider(): MenuProvider {
        return object : MenuProvider {
            override fun getDisplayName(): Component {
                return Component.translatable("entity.humblevehicles.steam_locomotive")
            }

            override fun createMenu(i: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu {
                return SteamHeadVehicleContainer<SteamLocomotiveEntity>(
                    i, level(), dataAccessor, playerInventory, player
                )
            }
        }
    }

    override fun remove(r: RemovalReason) {
        if (!level().isClientSide) {
            Containers.dropContents(this.level(), this, this)
        }
        super.remove(r)
    }

    constructor(type: EntityType<*>, level: Level) : super(type, level)

    constructor(level: Level, x: Double, y: Double, z: Double) : super(
        ModEntityTypes.STEAM_LOCOMOTIVE.get(),
        level,
        x,
        y,
        z
    )


    override fun getPickResult(): ItemStack {
        return ItemStack(ModItems.STEAM_LOCOMOTIVE.get())
    }


    override fun doMovementEffect() {
        val level = this.level()
        val blockpos = this.onPos.above().above()
        val random = level.random
        if (random.nextFloat() < ShippingConfig.Client.LOCO_SMOKE_MODIFIER.get()) {
            for (i in 0 until random.nextInt(2) + 2) {
                AbstractTugEntity.makeParticles(level, blockpos, this)
            }
        }
    }

    override fun canTakeItemThroughFace(index: Int, itemStack: ItemStack, dir: Direction): Boolean {
        return false
    }

    override fun getSlotsForFace(dir: Direction): IntArray {
        return intArrayOf(0)
    }

    override fun canPlaceItemThroughFace(index: Int, itemStack: ItemStack, dir: Direction?): Boolean {
        return stalling.isDocked()
    }

    override fun getRawHandler(): ItemStackHandler {
        return fuelItemHandler
    }

    public override fun readAdditionalSaveData(compound: CompoundTag) {
        fuelItemHandler.deserializeNBT(registryAccess(), compound.getCompound("fuelItems"))
        burnTime = if (compound.contains("burn")) compound.getInt("burn") else 0
        burnCapacity = if (compound.contains("burn_capacity")) compound.getInt("burn_capacity") else 0
        super.readAdditionalSaveData(compound)
    }

    public override fun addAdditionalSaveData(compound: CompoundTag) {
        compound.put("fuelItems", fuelItemHandler.serializeNBT(registryAccess()))
        compound.putInt("burn", burnTime)
        compound.putInt("burn_capacity", burnCapacity)
        super.addAdditionalSaveData(compound)
    }

    companion object {
        // This has to remain as ConfigValue as the class isn't reloaded when changing worlds
        private val FURNACE_FUEL_MULTIPLIER = ShippingConfig.Server.STEAM_LOCO_FUEL_MULTIPLIER
    }
}
