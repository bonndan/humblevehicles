package dev.murad.shipping.entity.custom.train.locomotive

import dev.murad.shipping.ShippingConfig
import dev.murad.shipping.entity.container.SteamHeadVehicleContainer
import dev.murad.shipping.entity.custom.FueledEngine
import dev.murad.shipping.entity.custom.SmokeGenerator.makeSmoke
import dev.murad.shipping.setup.ModEntityTypes
import dev.murad.shipping.setup.ModItems
import dev.murad.shipping.setup.ModSounds
import dev.murad.shipping.util.ItemHandlerVanillaContainerWrapper
import net.minecraft.core.Direction
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

class SteamLocomotiveEntity : AbstractLocomotiveEntity, ItemHandlerVanillaContainerWrapper, WorldlyContainer {

    init {
        engine = FueledEngine(ShippingConfig.Server.STEAM_LOCO_FUEL_MULTIPLIER!!.get())
    }

    constructor(type: EntityType<*>, level: Level) : super(type, level)

    constructor(level: Level, x: Double, y: Double, z: Double) : super(
        ModEntityTypes.STEAM_LOCOMOTIVE.get(),
        level,
        x,
        y,
        z
    )

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
                    i,
                    level(),
                    getDataAccessor(),
                    playerInventory,
                    player
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

    override fun getPickResult(): ItemStack {
        return ItemStack(ModItems.STEAM_LOCOMOTIVE.get())
    }

    override fun doMovementEffect() {

        val level = this.level()
        val random = level.random
        if (random.nextFloat() < ShippingConfig.Client.LOCO_SMOKE_MODIFIER.get()) {
            makeSmoke(level(), true, onPos, this)
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
}
