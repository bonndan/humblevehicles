package dev.murad.shipping.entity.container

import dev.murad.shipping.entity.accessor.SteamHeadVehicleDataAccessor
import dev.murad.shipping.entity.custom.HeadVehicle
import dev.murad.shipping.setup.ModMenuTypes
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.SlotItemHandler
import java.util.*

class SteamHeadVehicleContainer<T>(
    windowId: Int,
    world: Level,
    data: SteamHeadVehicleDataAccessor,
    playerInventory: Inventory,
    player: Player?
) : AbstractHeadVehicleContainer<SteamHeadVehicleDataAccessor, T>(
    ModMenuTypes.STEAM_LOCOMOTIVE_CONTAINER.get(),
    windowId,
    world,
    data,
    playerInventory,
    player
) where T : Entity, T : HeadVehicle {
    init {
        if (null != entity) {
            Optional.ofNullable(entity!!.getCapability(Capabilities.ItemHandler.ENTITY))
                .ifPresent { h -> addSlot(SlotItemHandler(h, 0, 42, 40)) }
        }
        this.addDataSlots(data.rawData)
    }

    val burnProgress: Int
        get() = data.burnProgress
}
