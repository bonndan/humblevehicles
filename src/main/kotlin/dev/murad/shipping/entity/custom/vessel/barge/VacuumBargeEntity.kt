package dev.murad.shipping.entity.custom.vessel.barge

import dev.murad.shipping.entity.custom.TrainInventoryProvider
import dev.murad.shipping.setup.ModEntityTypes
import dev.murad.shipping.setup.ModItems
import dev.murad.shipping.util.InventoryUtils.moveItemStackIntoHandler
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import java.util.function.Predicate

class VacuumBargeEntity : AbstractBargeEntity {
    // There's no point in saving this... probably
    private var itemCheckDelay = 0

    constructor(type: EntityType<out VacuumBargeEntity?>, world: Level) : super(type, world)
    constructor(worldIn: Level, x: Double, y: Double, z: Double) : super(
        ModEntityTypes.VACUUM_BARGE.get(),
        worldIn,
        x,
        y,
        z
    )

    // Only called on the server side
    override fun doInteract(player: Player?) {
        val size = connectedInventories.size

        player?.displayClientMessage(
            when (size) {
                0 -> Component.translatable("global.littlelogistics.no_connected_inventory_barge")
                else -> Component.translatable("global.littlelogistics.connected_inventory", size)
            }, false
        )
    }

    public override fun remove(r: RemovalReason) {
        super.remove(r)
    }

    public override fun tick() {
        super.tick()

        if (this.level().isClientSide) {
            return
        }

        if (this.itemCheckDelay > 0) {
            this.itemCheckDelay--
            return
        }

        val level = (level() as ServerLevel)

        // Render fire particles
        level.sendParticles<SimpleParticleType?>(
            ParticleTypes.FLAME,
            getX(), getY() + 0.85, getZ(),
            6,
            0.1, 0.1, 0.1, 0.0
        )

        // perform item check
        val searchBox = AABB(getX(), getY(), getZ(), getX(), getY(), getZ())
            .inflate(
                VacuumBargeEntity.Companion.PICK_RADIUS,
                VacuumBargeEntity.Companion.PICK_HEIGHT / 2.0,
                VacuumBargeEntity.Companion.PICK_RADIUS
            )

        val items = this.level()
            .getEntitiesOfClass<ItemEntity>(
                ItemEntity::class.java,
                searchBox,
                Predicate { e: ItemEntity -> e.distanceToSqr(this) < (VacuumBargeEntity.Companion.PICK_RADIUS * VacuumBargeEntity.Companion.PICK_RADIUS) })

        if (!items.isEmpty()) {
            val inventoryProviders = connectedInventories
            for (item in items) {
                val initial = item.getItem()
                var leftOver = initial.copy()
                for (provider in inventoryProviders) {
                    if (leftOver.isEmpty()) {
                        break
                    }

                    val itemHandler = provider!!.getTrainInventoryHandler()
                    if (itemHandler.isPresent()) {
                        leftOver = moveItemStackIntoHandler(itemHandler.get(), leftOver)
                    }
                }
                item.setItem(leftOver)

                if (initial != leftOver) {
                    // spawn particles
                    level.sendParticles<SimpleParticleType?>(
                        ParticleTypes.PORTAL,
                        item.getX(), item.getY(), item.getZ(), 15,
                        0.2, 0.2, 0.2, 0.0
                    )
                }
            }
        }
        this.itemCheckDelay = VacuumBargeEntity.Companion.ITEM_CHECK_DELAY
    }

    override fun getDropItem(): Item? {
        return ModItems.VACUUM_BARGE.get()
    }

    companion object {
        private const val ITEM_CHECK_DELAY = 20
        private const val PICK_RADIUS = 10.0
        private const val PICK_HEIGHT = 4.0
    }
}
