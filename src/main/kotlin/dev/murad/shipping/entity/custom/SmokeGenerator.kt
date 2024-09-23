package dev.murad.shipping.entity.custom

import dev.murad.shipping.ShippingConfig
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import java.util.function.Supplier

object SmokeGenerator {

    //TODO remove entity from API
    fun makeSmoke(world: Level, independentMotion: Boolean, onPos: BlockPos, entity: Entity) {

        if (!world.isClientSide || !independentMotion) return

        val blockpos: BlockPos = onPos.above().above()
        val random: RandomSource = world.random
        if (random.nextFloat() < ShippingConfig.Client.TUG_SMOKE_MODIFIER.get()) {
            for (i in 0 until random.nextInt(2) + 2) {
                makeParticles(world, blockpos, entity)
            }
        }
    }

    private fun makeParticles(level: Level, pos: BlockPos, entity: Entity) {

        val random: RandomSource = level.getRandom()
        val h: Supplier<Boolean> = Supplier { random.nextDouble() < 0.5 }

        val dx: Double = (entity.getX() - entity.xOld) / 12.0
        val dy: Double = (entity.getY() - entity.yOld) / 12.0
        val dz: Double = (entity.getZ() - entity.zOld) / 12.0

        val xDrift: Double = (if (h.get()) 1 else -1) * random.nextDouble() * 2
        val zDrift: Double = (if (h.get()) 1 else -1) * random.nextDouble() * 2

        val particleType: SimpleParticleType =
            if (random.nextBoolean()) ParticleTypes.CAMPFIRE_SIGNAL_SMOKE else ParticleTypes.CAMPFIRE_COSY_SMOKE

        level.addAlwaysVisibleParticle(
            particleType,
            true,
            pos.getX()
                .toDouble() + 0.5 + random.nextDouble() / 3.0 * (if (random.nextBoolean()) 1 else -1).toDouble(),
            pos.getY().toDouble() + random.nextDouble() + random.nextDouble(),
            pos.getZ()
                .toDouble() + 0.5 + random.nextDouble() / 3.0 * (if (random.nextBoolean()) 1 else -1).toDouble(),
            0.007 * xDrift + dx, 0.05 + dy, 0.007 * zDrift + dz
        )
    }
}