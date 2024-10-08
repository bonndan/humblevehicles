package dev.murad.shipping.entity.custom.train.locomotive

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
import net.minecraft.world.phys.Vec3
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3

class SteamLocomotiveEntity : AbstractLocomotiveEntity, ItemHandlerVanillaContainerWrapper, WorldlyContainer {

    init {
        engine = FueledEngine(saveStateCallback)
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
        makeSmoke(level(), onPos.above().above().toVec3(), Vec3(x, y, z), Vec3(xOld, yOld, zOld))
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
