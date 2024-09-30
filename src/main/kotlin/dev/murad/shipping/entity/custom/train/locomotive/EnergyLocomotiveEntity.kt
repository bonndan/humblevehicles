package dev.murad.shipping.entity.custom.train.locomotive

import dev.murad.shipping.entity.container.EnergyHeadVehicleContainer
import dev.murad.shipping.entity.custom.EnergyEngine
import dev.murad.shipping.setup.ModEntityTypes
import dev.murad.shipping.setup.ModItems
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

class EnergyLocomotiveEntity : AbstractLocomotiveEntity, ItemHandlerVanillaContainerWrapper, WorldlyContainer {

    init {
        engine = EnergyEngine()
    }

    constructor(type: EntityType<*>, level: Level) : super(type, level)

    constructor(level: Level, x: Double, y: Double, z: Double) : super(
        ModEntityTypes.ENERGY_LOCOMOTIVE.get(), level, x, y, z
    )

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
                    i,
                    level(),
                    getDataAccessor(),
                    playerInventory,
                    player
                )
            }
        }
    }


    override fun getPickResult(): ItemStack {
        return ItemStack(ModItems.ENERGY_LOCOMOTIVE.get())
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
}
