package dev.murad.shipping.entity.custom.train.wagon

import dev.murad.shipping.setup.ModEntityTypes
import dev.murad.shipping.setup.ModItems
import dev.murad.shipping.util.ItemHandlerVanillaContainerWrapper
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.*
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.items.ItemStackHandler
import java.util.stream.IntStream

class ChestCarEntity : AbstractWagonEntity, ItemHandlerVanillaContainerWrapper, WorldlyContainer, MenuProvider {

    protected val itemHandler: ItemStackHandler = createHandler()

    constructor(type: EntityType<ChestCarEntity>, level: Level) : super(type, level)

    constructor(type: EntityType<ChestCarEntity>, level: Level, x: Double, y: Double, z: Double) : super(
        type,
        level,
        x,
        y,
        z
    )

    override fun remove(r: RemovalReason) {
        if (!level().isClientSide) {
            Containers.dropContents(this.level(), this, this)
        }
        super.remove(r)
    }

    private fun createHandler(): ItemStackHandler {
        return ItemStackHandler(27)
    }

    override fun getPickResult(): ItemStack {
        return if (this.type == ModEntityTypes.BARREL_CAR.get()) {
            ItemStack(ModItems.BARREL_CAR.get())
        } else {
            ItemStack(ModItems.CHEST_CAR.get())
        }
    }

    override fun interact(player: Player, hand: InteractionHand): InteractionResult {
        val ret = super.interact(player, hand)
        if (ret.consumesAction()) return ret

        if (!level().isClientSide) {
            player.openMenu(this)
        }
        return InteractionResult.CONSUME
    }

    override fun createMenu(pContainerId: Int, pInventory: Inventory, pPlayer: Player): AbstractContainerMenu? {
        return if (pPlayer.isSpectator) {
            null
        } else {
            ChestMenu.threeRows(pContainerId, pInventory, this)
        }
    }

    override fun stillValid(pPlayer: Player): Boolean {
        return if (this.isRemoved) {
            false
        } else {
            !(this.distanceToSqr(pPlayer) > 64.0)
        }
    }


    override fun getRawHandler(): ItemStackHandler {
        return itemHandler
    }

    public override fun addAdditionalSaveData(t: CompoundTag) {
        super.addAdditionalSaveData(t)
        t.put("inv", itemHandler.serializeNBT(registryAccess()))
    }

    public override fun readAdditionalSaveData(t: CompoundTag) {
        super.readAdditionalSaveData(t)
        itemHandler.deserializeNBT(registryAccess(), t.getCompound("inv"))
    }

    // hack to disable hoppers before docking complete
    override fun getSlotsForFace(p_180463_1_: Direction): IntArray {
        return IntStream.range(0, containerSize).toArray()
    }

    override fun canPlaceItemThroughFace(p_180462_1_: Int, p_180462_2_: ItemStack, p_180462_3_: Direction?): Boolean {
        return isDockable
    }

    override fun canTakeItemThroughFace(p_180461_1_: Int, p_180461_2_: ItemStack, p_180461_3_: Direction): Boolean {
        return isDockable
    }
}
