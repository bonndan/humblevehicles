package com.github.bonndan.humblevehicles.entity.custom.vessel.tug

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.Pose
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.LeadItem
import net.neoforged.neoforge.entity.PartEntity

class VehicleFrontPart(parent: Entity) : PartEntity<Entity>(parent) {
    init {
        this.refreshDimensions()
    }

    override fun hurt(pSource: DamageSource, pAmount: Float): Boolean {
        return if (this.isInvulnerableTo(pSource)) false
        else parent.hurt(pSource, pAmount) == true
    }

    override fun `is`(pEntity: Entity): Boolean {
        return this === pEntity || parent === pEntity
    }

    override fun getPickResult(): ItemStack? {
        return parent.pickResult
    }

    override fun getDimensions(pPose: Pose): EntityDimensions {
        return parent.getDimensions(pPose)
    }

    override fun shouldBeSaved(): Boolean {
        return true
    }

    fun updatePosition(tugEntity: Entity) {
        val oldX: Double = this.getX()
        val oldY: Double = this.getY()
        val oldZ: Double = this.getZ()
        val x: Double = tugEntity.getX() + tugEntity.getDirection().getStepX() * getParent()!!.getBoundingBox().getXsize()
        val z: Double = tugEntity.getZ() + tugEntity.getDirection().getStepZ() * getParent()!!.getBoundingBox().getXsize()
        val y: Double = tugEntity.getY()
        this.setPos(x, y, z)
        this.zOld = oldZ
        this.zo = oldZ
        this.xOld = oldX
        this.xo = oldX
        this.yOld = oldY
        this.yo = oldY
    }

    override fun isPickable(): Boolean {
        return !this.isRemoved()
    }

    val pos: BlockPos
        get() {
            return getOnPos()
        }

    override fun interact(player: Player, hand: InteractionHand): InteractionResult {
        if (parent is AbstractTugEntity) {
            val tugEntity = parent as AbstractTugEntity
            if (player.getItemInHand(hand).getItem() is LeadItem || tugEntity.getLeashHolder() == player) {
                return tugEntity.interact(player, hand)
            }
            return tugEntity.mobInteract(player, hand)
        }

        return parent!!.interact(player, hand)
    }

    override fun defineSynchedData(pBuilder: SynchedEntityData.Builder) {
    }

    override fun readAdditionalSaveData(pCompound: CompoundTag) {
    }

    override fun addAdditionalSaveData(pCompound: CompoundTag) {
    }
}
