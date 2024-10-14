package com.github.bonndan.humblevehicles.entity.custom.train.locomotive

import com.github.bonndan.humblevehicles.entity.container.EnergyHeadVehicleContainer
import com.github.bonndan.humblevehicles.entity.custom.EnergyEngine
import com.github.bonndan.humblevehicles.setup.ModEntityTypes
import com.github.bonndan.humblevehicles.setup.ModItems
import com.github.bonndan.humblevehicles.util.ItemHandlerVanillaContainerWrapper
import net.minecraft.network.chat.Component
import net.minecraft.world.Containers
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class EnergyLocomotiveEntity : AbstractLocomotiveEntity, ItemHandlerVanillaContainerWrapper {

    init {
        engine = EnergyEngine(saveStateCallback)
    }

    constructor(type: EntityType<*>, level: Level) : super(type, level)

    constructor(level: Level, x: Double, y: Double, z: Double) : super(
        ModEntityTypes.ENERGY_LOCOMOTIVE.get(), level, x, y, z
    )

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

    override fun remove(r: RemovalReason) {
        if (!level().isClientSide) {
            Containers.dropContents(this.level(), this, this)
        }
        super.remove(r)
    }

    override fun getPickResult(): ItemStack {
        return ItemStack(ModItems.ENERGY_LOCOMOTIVE.get())
    }
}
