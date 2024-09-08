package dev.murad.shipping.entity.custom.train.wagon

import dev.murad.shipping.capability.StallingCapability
import dev.murad.shipping.entity.custom.train.AbstractTrainCarEntity
import dev.murad.shipping.entity.custom.train.locomotive.AbstractLocomotiveEntity
import dev.murad.shipping.util.Train
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import java.util.*

abstract class AbstractWagonEntity : AbstractTrainCarEntity {
    constructor(entityType: EntityType<*>, level: Level) : super(entityType, level)

    constructor(entityType: EntityType<*>, level: Level, aDouble: Double, aDouble1: Double, aDouble2: Double) : super(
        entityType,
        level,
        aDouble,
        aDouble1,
        aDouble2
    )

    override fun setDominated(entity: AbstractTrainCarEntity) {
        linkingHandler.follower = Optional.of(entity)
    }


    override fun setDominant(entity: AbstractTrainCarEntity) {
        this.setTrain(entity.getTrain())
        linkingHandler.leader = Optional.of(entity)
    }

    override fun tick() {
        if (capability.isFrozen() || linkingHandler.train?.tug
                ?.map { s -> s as AbstractLocomotiveEntity }
                ?.map { obj: AbstractLocomotiveEntity -> obj.shouldFreezeTrain() }?.orElse(false) == true
        ) {
            this.deltaMovement = Vec3.ZERO
        } else {
            super.tick()
        }
    }

    override fun removeDominated() {
        if (!this.isAlive) {
            return
        }
        linkingHandler.follower = Optional.empty()
        linkingHandler.train?.tail = this
    }

    override fun removeDominant() {
        if (!this.isAlive) {
            return
        }
        linkingHandler.leader = Optional.empty()
        this.setTrain(Train(this))
    }

    override fun setTrain(train: Train<AbstractTrainCarEntity>) {
        linkingHandler.train = train
        train.tail = this
        linkingHandler.follower.ifPresent { dominated: AbstractTrainCarEntity ->
            // avoid recursion loops
            if (dominated.getTrain() != train) {
                dominated.setTrain(train)
            }
        }
    }

    val isDockable: Boolean
        // hack to disable hoppers
        get() = linkingHandler.leader.map { dom: AbstractTrainCarEntity? ->
            this.distanceToSqr(
                dom
            ) < 1.05
        }.orElse(true)


    override fun allowDockInterface(): Boolean {
        return isDockable
    }

    private val capability: StallingCapability = object : StallingCapability {

        override fun isDocked(): Boolean =
            delegate().map(StallingCapability::isDocked).orElse(false)

        override fun dock(x: Double, y: Double, z: Double) {
            delegate().ifPresent { s: StallingCapability -> s.dock(x, y, z) }
        }

        override fun undock() {
            delegate().ifPresent { obj: StallingCapability -> obj.undock() }
        }

        override fun isStalled(): Boolean = delegate().map(StallingCapability::isStalled).orElse(false)

        override fun stall() {
            delegate().ifPresent { obj: StallingCapability -> obj.stall() }
        }

        override fun unstall() {
            delegate().ifPresent { obj: StallingCapability -> obj.unstall() }
        }

        override fun isFrozen(): Boolean = super@AbstractWagonEntity.isFrozen()

        override fun freeze() {
            super@AbstractWagonEntity.setFrozen(true)
        }

        override fun unfreeze() {
            super@AbstractWagonEntity.setFrozen(false)
        }

        private fun delegate(): Optional<StallingCapability> {
            val e = linkingHandler.train?.head
            if (e is AbstractLocomotiveEntity) {
                return Optional.ofNullable<StallingCapability>(
                    e.getCapability(StallingCapability.STALLING_CAPABILITY)
                )
            }
            return Optional.empty()
        }
    }
}
