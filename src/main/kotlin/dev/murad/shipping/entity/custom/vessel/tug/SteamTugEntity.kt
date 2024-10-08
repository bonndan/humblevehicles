package dev.murad.shipping.entity.custom.vessel.tug

import dev.murad.shipping.entity.container.SteamHeadVehicleContainer
import dev.murad.shipping.entity.custom.FueledEngine
import dev.murad.shipping.entity.custom.SmokeGenerator.makeSmoke
import dev.murad.shipping.entity.custom.vessel.TugControl
import dev.murad.shipping.entity.models.PositionAdjustedEntity
import dev.murad.shipping.entity.models.vessel.SteamTugModel.Companion.MODEL_Y_OFFSET
import dev.murad.shipping.setup.ModEntityTypes
import dev.murad.shipping.setup.ModItems
import dev.murad.shipping.setup.ModSounds
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.animal.WaterAnimal
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3

class SteamTugEntity : AbstractTugEntity, PositionAdjustedEntity {

    init {
        engine = FueledEngine(saveStateCallback)
        control = TugControl
        boundingBox= AABB(0.0, 0.0, 0.0, 1.0, 2.0, 2.0)
    }

    constructor(type: EntityType<out WaterAnimal>, world: Level) : super(type, world)

    constructor(worldIn: Level, x: Double, y: Double, z: Double) : super(
        ModEntityTypes.STEAM_TUG.get(),
        worldIn,
        x,
        y,
        z
    )

    override fun createContainerProvider(): MenuProvider {
        return object : MenuProvider {
            override fun getDisplayName(): Component {
                return Component.translatable("screen.humblevehicles.tug")
            }

            override fun createMenu(i: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu {
                return SteamHeadVehicleContainer<SteamTugEntity>(i, level(), getDataAccessor(), playerInventory, player)
            }
        }
    }

    override fun tick() {
        super.tick()
        if (level().isClientSide) {
            //two above and a bit in the back
            val emitterPos = onPos.above().above().toVec3().add(Vec3(0.0, 0.0, 0.2))
            makeSmoke(level(), emitterPos, Vec3(x, y, z), Vec3(xOld, yOld, zOld))
        }
    }

    override fun getDropItem(): Item {
        return ModItems.STEAM_TUG.get()
    }

    override fun onUndock() {
        super.onUndock()
        this.playSound(ModSounds.STEAM_TUG_WHISTLE.get(), 1f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f)
    }

    /**
     * shift passenger nearer to steering wheel
     */
    override fun getPassengerRidingPosition(pEntity: Entity): Vec3 {
        val vec3 = super.getPassengerRidingPosition(pEntity)
        return vec3.add(transformPoint(Vec3(0.0, -0.1, 0.2), yRot))
    }

    override fun getModelYOffset(): Double {
        return MODEL_Y_OFFSET
    }

    companion object {
        fun setCustomAttributes(): AttributeSupplier.Builder {
            return AbstractTugEntity.setCustomAttributes()
        }
    }
}
