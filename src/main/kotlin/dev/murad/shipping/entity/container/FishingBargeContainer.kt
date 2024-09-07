package dev.murad.shipping.entity.container

import dev.murad.shipping.entity.custom.vessel.barge.FishingBargeEntity
import dev.murad.shipping.setup.ModMenuTypes
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.SlotItemHandler
import java.util.*

// Unused
class FishingBargeContainer(
    windowId: Int, world: Level, entityId: Int,
    playerInventory: Inventory, player: Player?
) :
    AbstractItemHandlerContainer(ModMenuTypes.FISHING_BARGE_CONTAINER.get(), windowId, playerInventory, player) {
    private val fishingBargeEntity = world.getEntity(entityId) as FishingBargeEntity?

    init {
        layoutPlayerInventorySlots(8, 49 + 18 * 2)

        if (fishingBargeEntity != null) {
            Optional.ofNullable(fishingBargeEntity.getCapability(Capabilities.ItemHandler.ENTITY))
                .ifPresent { h: IItemHandler? ->
                    for (l in 0..2) {
                        for (k in 0..8) {
                            this.addSlot(
                                SlotItemHandler(
                                    h,
                                    l * 9 + k,
                                    8 + k * 18,
                                    18 * (l + 1)
                                )
                            )
                        }
                    }
                }
        }
    }

    override val slotNum: Int
        get() = 27

    override fun stillValid(player: Player): Boolean {
        return true
        //        return fishingBargeEntity.stillValid(player);
    }
}
