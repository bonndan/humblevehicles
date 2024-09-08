package dev.murad.shipping.entity.container

import dev.murad.shipping.entity.accessor.EnergyHeadVehicleDataAccessor
import dev.murad.shipping.entity.custom.HeadVehicle
import dev.murad.shipping.setup.ModItems
import dev.murad.shipping.setup.ModMenuTypes
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.SlotItemHandler
import java.util.*

class EnergyHeadVehicleContainer<T>(
    windowId: Int,
    world: Level,
    data: EnergyHeadVehicleDataAccessor,
    playerInventory: Inventory,
    player: Player?
) : AbstractHeadVehicleContainer<EnergyHeadVehicleDataAccessor, T>(
        ModMenuTypes.ENERGY_LOCOMOTIVE_CONTAINER.get(),
        windowId,
        world,
        data,
        playerInventory,
        player
    ) where T : Entity, T : HeadVehicle {
    init {
        if (null != entity) {
            Optional.ofNullable<IItemHandler>(entity!!.getCapability(Capabilities.ItemHandler.ENTITY))
                .ifPresent { h: IItemHandler? ->
                    addSlot(
                        SlotItemHandler(h, 0, 32, 35).setBackground(EMPTY_ATLAS_LOC, ModItems.EMPTY_ENERGY)
                    )
                }
        }
    }

    val energy: Int
        get() = data!!.energy

    val capacity: Int
        get() = data!!.capacity

    val energyCapacityRatio: Double
        get() {
            if (capacity == 0) {
                return 1.0
            }

            return energy.toDouble() / capacity
        }
}
