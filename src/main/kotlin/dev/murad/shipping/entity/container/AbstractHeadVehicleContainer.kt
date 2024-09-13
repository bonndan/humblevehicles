package dev.murad.shipping.entity.container

import dev.murad.shipping.entity.accessor.HeadVehicleDataAccessor
import dev.murad.shipping.entity.custom.HeadVehicle
import dev.murad.shipping.network.EnrollVehiclePacket
import dev.murad.shipping.network.SetEnginePacket
import dev.murad.shipping.network.VehiclePacketHandler
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.level.Level
import net.neoforged.neoforge.items.SlotItemHandler

abstract class AbstractHeadVehicleContainer<T : HeadVehicleDataAccessor, U>(
    containerType: MenuType<*>,
    windowId: Int,
    world: Level,
    protected var data: T,
    playerInventory: Inventory,
    player: Player?
) :
    AbstractItemHandlerContainer(containerType, windowId, playerInventory, player) where U : Entity, U : HeadVehicle {

    protected var entity: U? = data.getEntityUUID()?.let { world.getEntity(it) as U? }

    init {
        this.player = playerInventory.player
        layoutPlayerInventorySlots(8, 84)
        this.addDataSlots(data)

        entity?.let {
            addSlot(
                SlotItemHandler(it.getRouteItemHandler(), 0, 98, 57)
                    .setBackground(EMPTY_ATLAS_LOC, it.getRouteIcon())
            )
        }
    }

    override val slotNum: Int
        get() = 2

    val isLit: Boolean
        get() = data.isLit

    val isOn: Boolean
        get() = data.isOn

    fun routeSize(): Int {
        return data.routeSize()
    }

    fun visitedSize(): Int {
        return data.visitedSize()
    }

    fun setEngine(state: Boolean) {
        VehiclePacketHandler.send(SetEnginePacket(entity!!.id, state))
    }

    fun enroll() {
        VehiclePacketHandler.send(EnrollVehiclePacket(entity!!.id))
    }

    val owner: String
        get() = entity?.owner()!!

    fun canMove(): Boolean {
        return data.canMove()
    }

    val routeText: String
        get() = visitedSize().toString() + "/" + routeSize()

    override fun stillValid(pPlayer: Player): Boolean {
        return entity?.isValid(pPlayer) == true
    }

    companion object {
        val EMPTY_ATLAS_LOC: ResourceLocation = InventoryMenu.BLOCK_ATLAS
    }
}
