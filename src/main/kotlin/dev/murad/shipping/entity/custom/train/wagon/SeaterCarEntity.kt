package dev.murad.shipping.entity.custom.train.wagon

import dev.murad.shipping.setup.ModEntityTypes
import dev.murad.shipping.setup.ModItems
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

class SeaterCarEntity : AbstractWagonEntity {
    constructor(p_38087_: EntityType<SeaterCarEntity>, p_38088_: Level) : super(p_38087_, p_38088_)

    constructor(
        level: Level,
        aDouble: Double,
        aDouble1: Double,
        aDouble2: Double
    ) : super(ModEntityTypes.SEATER_CAR.get(), level, aDouble, aDouble1, aDouble2)

    override fun getPickResult(): ItemStack {
        return ItemStack(ModItems.SEATER_CAR.get())
    }

    override fun interact(pPlayer: Player, pHand: InteractionHand): InteractionResult {
        val ret = super.interact(pPlayer, pHand)
        if (ret.consumesAction()) return ret

        return if (pPlayer.isSecondaryUseActive) {
            InteractionResult.PASS
        } else if (this.isVehicle) {
            InteractionResult.PASS
        } else if (!level().isClientSide) {
            if (pPlayer.startRiding(this)) InteractionResult.CONSUME else InteractionResult.PASS
        } else {
            InteractionResult.SUCCESS
        }
    }

    /**
     * Called every tick the minecart is on an activator rail.
     */
    override fun activateMinecart(pX: Int, pY: Int, pZ: Int, pReceivingPower: Boolean) {
        if (pReceivingPower) {
            if (this.isVehicle) {
                this.ejectPassengers()
            }

            if (this.hurtTime == 0) {
                this.hurtDir = -this.hurtDir
                this.hurtTime = 10
                this.damage = 50.0f
                this.markHurt()
            }
        }
    }

    override fun positionRider(passenger: Entity, pCallback: MoveFunction) {
        if (this.hasPassenger(passenger)) {
            if (passenger is Player) {
                // Position player differently than all other entities
                // TODO: Maybe we could override Entity#getPassengersRidingOffset instead
                val f = -0.22f
                val vector3d = Vec3(
                    f.toDouble(),
                    0.0,
                    0.0
                ).yRot(-this.yRot * (Math.PI.toFloat() / 180f) - (Math.PI.toFloat() / 2f))
                pCallback.accept(passenger, this.x + vector3d.x, this.y, this.z + vector3d.z)
            } else {
                super.positionRider(passenger, pCallback)
            }
        }
    }

    private fun clampRotation(p_184454_1_: Entity) {
        p_184454_1_.setYBodyRot(this.yRot)
        val f = Mth.wrapDegrees(p_184454_1_.yRot - this.yRot)
        val f1 = Mth.clamp(f, -105.0f, 105.0f)
        p_184454_1_.yRotO += f1 - f
        p_184454_1_.yRot = p_184454_1_.yRot + f1 - f
        p_184454_1_.yHeadRot = p_184454_1_.yRot
    }

    override fun onPassengerTurned(p_184190_1_: Entity) {
        this.clampRotation(p_184190_1_)
    }

    override fun getMinecartType(): Type {
        return Type.RIDEABLE
    }
}
