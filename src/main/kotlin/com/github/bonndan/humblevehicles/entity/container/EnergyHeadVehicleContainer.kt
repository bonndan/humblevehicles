package com.github.bonndan.humblevehicles.entity.container

import com.github.bonndan.humblevehicles.entity.accessor.HeadVehicleDataAccessor
import com.github.bonndan.humblevehicles.entity.custom.HeadVehicle
import com.github.bonndan.humblevehicles.setup.ModItems
import com.github.bonndan.humblevehicles.setup.ModMenuTypes
import com.github.bonndan.humblevehicles.util.ItemHandlerVanillaContainerWrapper
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.neoforged.neoforge.items.SlotItemHandler

class EnergyHeadVehicleContainer<T>(
    windowId: Int,
    world: Level,
    data: HeadVehicleDataAccessor,
    playerInventory: Inventory,
    player: Player?
) : AbstractHeadVehicleContainer<T>(
    ModMenuTypes.ENERGY_HEAD_CONTAINER.get(),
    windowId,
    world,
    data,
    playerInventory,
    player
) where T : Entity, T : HeadVehicle {

    init {
        if (null != entity) {
            val e = entity
            if (e is ItemHandlerVanillaContainerWrapper) {
                addSlot(
                    SlotItemHandler(e.getRawHandler(), 0, 32, 35)
                        .setBackground(EMPTY_ATLAS_LOC, ModItems.EMPTY_ENERGY)
                )
            }
        }
    }
}
