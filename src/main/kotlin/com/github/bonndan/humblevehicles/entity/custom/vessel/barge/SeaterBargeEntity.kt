package com.github.bonndan.humblevehicles.entity.custom.vessel.barge

import com.github.bonndan.humblevehicles.setup.ModEntityTypes
import com.github.bonndan.humblevehicles.setup.ModItems
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.Entity.MoveFunction
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.animal.Animal
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

class SeaterBargeEntity : AbstractBargeEntity {
    constructor(type: EntityType<out SeaterBargeEntity?>, world: Level) : super(type, world)

    constructor(worldIn: Level, x: Double, y: Double, z: Double) : super(
        ModEntityTypes.SEATER_BARGE.get(),
        worldIn,
        x,
        y,
        z
    )


    public override fun getDropItem(): Item? {
        return ModItems.SEATER_BARGE.get()
    }

     override fun canAddPassenger(p_184219_1_: Entity): Boolean {
        return this.getPassengers().isEmpty()
    }

    private fun clampRotation(p_184454_1_: Entity) {
        p_184454_1_.setYBodyRot(this.getYRot())
        val f = Mth.wrapDegrees(p_184454_1_.getYRot() - this.getYRot())
        val f1 = Mth.clamp(f, -105.0f, 105.0f)
        p_184454_1_.yRotO += f1 - f
        p_184454_1_.setYRot(p_184454_1_.getYRot() + f1 - f)
        p_184454_1_.setYHeadRot(p_184454_1_.getYRot())
    }

    override fun onPassengerTurned(passenger: Entity) {
        this.clampRotation(passenger)
    }

    public override fun positionRider(passenger: Entity, pCallback: MoveFunction) {
        if (!this.hasPassenger(passenger)) {
            return
        }

        val f = -0.1f
        val passengerRidingOffsetY = this.getPassengerRidingPosition(passenger).y - this.getY() //TODO
        val f1 = ((if (this.dead) 0.01 else passengerRidingOffsetY)).toFloat()
        val vector3d =
            (Vec3(f.toDouble(), 0.0, 0.0)).yRot(-this.getYRot() * (Math.PI.toFloat() / 180f) - (Math.PI.toFloat() / 2f))
        passenger.setPos(this.getX() + vector3d.x, this.getY() - 0.5 + f1.toDouble(), this.getZ() + vector3d.z)
        if (passenger is Animal && this.getPassengers().size > 1) {
            val j = if (passenger.getId() % 2 == 0) 90 else 270
            passenger.setYBodyRot(passenger.yBodyRot + j.toFloat())
            passenger.setYHeadRot(passenger.getYHeadRot() + j.toFloat())
        }
    }

    override fun doInteract(player: Player?) {
        player?.startRiding(this)
    }
}
