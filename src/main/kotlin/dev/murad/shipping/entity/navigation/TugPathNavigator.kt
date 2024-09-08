package dev.murad.shipping.entity.navigation

import dev.murad.shipping.ShippingConfig
import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation
import net.minecraft.world.level.Level
import net.minecraft.world.level.pathfinder.PathFinder
import net.minecraft.world.phys.Vec3

class TugPathNavigator(p_i45873_1_: Mob, p_i45873_2_: Level) :
    WaterBoundPathNavigation(p_i45873_1_, p_i45873_2_) {
    init {
        setMaxVisitedNodesMultiplier(ShippingConfig.Server.TUG_PATHFINDING_MULTIPLIER!!.get().toFloat())
    }

    override fun createPathFinder(p_179679_1_: Int): PathFinder {
        this.nodeEvaluator = TugNodeProcessor(this.level)
        return PathFinder(this.nodeEvaluator, p_179679_1_)
    }

    override fun moveTo(p_75492_1_: Double, p_75492_3_: Double, p_75492_5_: Double, p_75492_7_: Double): Boolean {
        return this.moveTo(this.createPath(p_75492_1_, p_75492_3_, p_75492_5_, 0), p_75492_7_)
    }

    override fun doStuckDetection(p_179677_1_: Vec3) {
        if (this.tick - this.lastStuckCheck > 100) {
            if (p_179677_1_.distanceToSqr(this.lastStuckCheckPos) < 2.25) {
                this.stop()
            }

            this.lastStuckCheck = this.tick
            this.lastStuckCheckPos = p_179677_1_
        }

        if (this.path != null && !path!!.isDone) {
            val vector3i = path!!.nextNodePos
            if (vector3i == this.timeoutCachedNode) {
                this.timeoutTimer += Util.getMillis() - this.lastTimeoutCheck
            } else {
                this.timeoutCachedNode = vector3i
                val d0 = p_179677_1_.distanceTo(Vec3.atCenterOf(this.timeoutCachedNode))
                this.timeoutLimit = if (mob.speed > 0.0f) (d0 / mob.speed.toDouble()) * 1000 else 0.0
            }

            if (this.timeoutLimit > 0.0 && timeoutTimer.toDouble() > this.timeoutLimit * 2.0) {
                this.timeoutCachedNode = BlockPos.ZERO
                this.timeoutTimer = 0L
                this.timeoutLimit = 0.0
                this.stop()
            }

            this.lastTimeoutCheck = Util.getMillis()
        }
    }
}
