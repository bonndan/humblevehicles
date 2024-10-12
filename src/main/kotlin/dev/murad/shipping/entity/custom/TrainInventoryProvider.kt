package dev.murad.shipping.entity.custom

import net.neoforged.neoforge.items.ItemStackHandler
import java.util.Optional


/**
 * A train inventory provider is a barge or a car that provides inventories
 * to other barges or cars ahead of it.
 */
interface TrainInventoryProvider {
    // TODO: fluid provider
    fun getTrainInventoryHandler(): Optional<ItemStackHandler> {
        return Optional.empty<ItemStackHandler>()
    }
}
