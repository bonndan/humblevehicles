package com.github.bonndan.humblevehicles.entity.custom.train

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.Pose
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.entity.PartEntity

class VehicleFrontPart(parent: Entity) : PartEntity<Entity>(parent) {
    init {
        this.refreshDimensions()
    }

    override fun hurtServer(level: ServerLevel, pSource: DamageSource, pAmount: Float): Boolean {
        return if (this.isInvulnerableToBase(pSource)) {
            false
        } else {
            parent.hurtServer(level, pSource, pAmount) == true
        }
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
        val x: Double =
            tugEntity.getX() + tugEntity.getDirection().getStepX() * getParent()!!.getBoundingBox().getXsize()
        val z: Double =
            tugEntity.getZ() + tugEntity.getDirection().getStepZ() * getParent()!!.getBoundingBox().getXsize()
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

    override fun defineSynchedData(pBuilder: SynchedEntityData.Builder) {
    }

    override fun readAdditionalSaveData(pCompound: CompoundTag) {
    }

    override fun addAdditionalSaveData(pCompound: CompoundTag) {
    }
}